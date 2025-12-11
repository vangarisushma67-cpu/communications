package com.lloyds.hackthon.customercoms.communication.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.lloyds.hackthon.customercoms.communication.util.CommonUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommunicationAgent {
    private static final Logger logger = Logger.getLogger(CommunicationAgent.class.getName());
    private String input;

    public CommunicationAgent() {
        // Default constructor
    }

    public String generateCommunication(String input) {
        this.input = input;
        logger.info("Generating communication for input: " + input);

        try {
            List<String> statementFiles = CommonUtils.listPensionDataFiles();
            for (String statement : statementFiles) {
                byte[] statementData = CommonUtils.readCloudStoragePDFFile("pension-data-communication", statement);
                String promptTemplate = loadPrompt();
//                promptTemplate = promptTemplate.replace("${customerData}", statementData);
                LlmAgent agent = LlmAgent.builder()
                        .name("Communication Agent")
                        .description("You are a professional document generation assistant for Lloyds Banking Group.")
                        .model("gemini-2.5-pro")
                        .instruction(promptTemplate)
                        .build();

                String response = sendAndGetResponse(agent, statementData, statement);
                CommonUtils.writeCloudStorageFile("final-communication", statement, response);
            }
            return "Communication generation completed successfully";
        } catch (Exception e) {
            logger.severe("Error in communication generation: " + e.getMessage());
            return "{\"error\": \"Communication generation failed\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    private String loadPrompt() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("communication-prompt.txt")) {
            if (input != null) {
                return new String(input.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                return "You are a communication agent. Please generate appropriate customer communication based on the provided input.";
            }
        } catch (IOException e) {
            logger.warning("Error loading prompt, using default: " + e.getMessage());
            return "You are a communication agent. Please generate appropriate customer communication based on the provided input.";
        }
    }

    public String sendAndGetResponse(LlmAgent agent, byte[] statementData, String outputFilename) {
        logger.info("Sending message to LLM agent: " + agent.name());
        String runid = String.valueOf(new Random().nextInt());
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService()
                .createSession(agent.name(), runid)
                .blockingGet();

        try (ByteArrayOutputStream htmlContent = new ByteArrayOutputStream()) {
            // Prepare session data
            session.state().put("customerData", new String(statementData, StandardCharsets.UTF_8));
            
            // Create the user message with instruction
            String prompt = agent.instruction().toString();
            Content userMsg = Content.fromParts(Part.fromText(prompt));

            // Run the agent and collect the HTML response
            runner.runAsync(runid, session.id(), userMsg)
                .doOnError(error -> logger.severe("Error in agent execution: " + error.getMessage()))
                .blockingForEach(event -> {
                    if (event.content().isPresent()) {
                        event.content().get().parts().ifPresent(parts -> {
                            parts.forEach(part -> {
                                try {
                                    // Get text content which should contain HTML
                                    if (part.text() != null && !part.text().isEmpty()) {
                                        htmlContent.write(part.text().get().getBytes(StandardCharsets.UTF_8));
                                        logger.info("Received HTML content from LLM");
                                    }
                                } catch (Exception e) {
                                    logger.severe("Error processing HTML content: " + e.getMessage());
                                    throw new RuntimeException("Failed to process HTML response", e);
                                }
                            });
                        });
                    }
                });

            String html = htmlContent.toString(StandardCharsets.UTF_8);
            if (html.isEmpty()) {
                throw new RuntimeException("No HTML content was received from the agent");
            }
            
            // Convert HTML to PDF
            byte[] pdfData = convertHtmlToPdf(html);
            
            // Save the PDF to storage
            savePdfToStorage(pdfData, outputFilename);
            
            return "Successfully generated and saved PDF to final-communication/" + outputFilename;
        } catch (Exception e) {
            logger.severe("Error in sendAndGetResponse: " + e.getMessage());
            throw new RuntimeException("Failed to generate communication: " + e.getMessage(), e);
        }
    }

    private void savePdfToStorage(byte[] pdfBytes, String filename) throws IOException {
        try (InputStream pdfStream = new ByteArrayInputStream(pdfBytes)) {
            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of("final-communication", filename);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();
            storage.create(blobInfo, pdfStream);
            logger.info("Saved PDF to final-communication/" + filename);
        } catch (Exception e) {
            logger.severe("Error saving PDF to storage: " + e.getMessage());
            throw e;
        }
    }
    
    private byte[] convertHtmlToPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Clean up the HTML first
            String cleanedHtml = cleanHtml(html);
            
            // Create a well-formed XHTML document
            String xhtml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                         "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                         "<head>\n" +
                         "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                         "  <title>Document</title>\n" +
                         "</head>\n" +
                         "<body>" + cleanedHtml + "</body>\n" +
                         "</html>";
            
            // Configure iText renderer
            ITextRenderer renderer = new ITextRenderer();
            
            // Add font resolver for better font support
            ITextFontResolver fontResolver = renderer.getFontResolver();
            try {
                // Add system fonts or custom fonts if needed
                // fontResolver.addFont("path/to/font.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception e) {
                logger.warning("Could not load custom font: " + e.getMessage());
            }
            
            // Set document from XHTML string
            renderer.setDocumentFromString(xhtml);
            
            // Layout and render the PDF
            renderer.layout();
            renderer.createPDF(outputStream, false);
            renderer.finishPDF();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.severe("Error converting HTML to PDF: " + e.getMessage());
            throw new RuntimeException("Failed to convert HTML to PDF: " + e.getMessage(), e);
        }
    }
    
    private String cleanHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "<html><body>No content</body></html>";
        }
        
        // Fix common HTML issues
        String cleaned = html.trim();
        
        // 1. Fix unescaped ampersands in URLs and HTML entities
        cleaned = cleaned.replaceAll("&(?!(?:[a-zA-Z]\\+|#\\d\\+|#x[0-9a-fA-F]\\+);)", "&amp;");
        
        // 2. Fix self-closing tags first
        cleaned = cleaned.replaceAll("<(meta|link|img|br|hr|input|col|area|base|source)([^>]*)>", "<$1$2 />");
        
        // 3. Ensure proper HTML structure if missing
        if (!cleaned.toLowerCase().contains("<html") && !cleaned.toLowerCase().contains("<!doctype")) {
            cleaned = "<!DOCTYPE html>\n" +
                     "<html>\n" +
                     "<head>\n" +
                     "  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\n" +
                     "  <title>Document</title>\n" +
                     "</head>\n" +
                     "<body>" + cleaned + "</body>\n" +
                     "</html>";
        }
        
        // 3. Remove any null bytes that might cause issues
        cleaned = cleaned.replace("\u0000", "");
        
        // 4. Fix unclosed tags and ensure XHTML compliance
        cleaned = cleaned.replaceAll("<br>|<br[^>]*>", "<br/>")
                       .replaceAll("<hr>|<hr[^>]*>", "<hr/>")
                       .replaceAll("<img([^>]+)>", "<img$1 />")
                       .replaceAll("<meta([^>]+)>", "<meta$1 />")
                       .replaceAll("<link([^>]+)>", "<link$1 />");
        
        return cleaned;
    }
    
    private byte[] generatePdfFromText(String text) {
        // Simple fallback method in case HTML generation fails
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Pension Statement Summary", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);
            
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
            document.add(new Paragraph(text, contentFont));
            
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.severe("Error generating fallback PDF: " + e.getMessage());
            throw new RuntimeException("Failed to generate fallback PDF", e);
        }
    }
    
    private String loadHtmlTemplate() throws IOException {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/email-template.html")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Email template not found in resources/templates/email-template.html");
                }
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.severe("Error loading HTML template: " + e.getMessage());
                throw e;
            }
        }

    private byte[] generatePdfFromHtml(String html) throws Exception {
        if (html == null || html.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content cannot be null or empty");
        }
        
        // Add basic HTML structure if not present
        if (!html.trim().startsWith("<html")) {
            String template = loadHtmlTemplate();
            html = template.replace("{{content}}", html);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.severe("Error generating PDF from HTML: " + e.getMessage());
            throw e;
        }
    }

    private String extractTextFromContent(Content content) {
        if (content == null) {
            return "";
        }
        
        StringBuilder llmResponse = new StringBuilder();
        
        // First try to get text from parts
        Optional<java.util.List<Part>> parts = content.parts();
        if (parts != null && parts.isPresent()) {
            String textFromParts = parts.get().stream()
                .filter(part -> part != null && part.text() != null && part.text().isPresent())
                .map(part -> part.text().get())
                .collect(Collectors.joining("\n"));
            llmResponse.append(textFromParts);
        }

        // If no text from parts, try reflection
        if (llmResponse.length() == 0) {
            try {
                java.lang.reflect.Method getTextMethod = content.getClass().getMethod("getText");
                if (getTextMethod != null) {
                    Object text = getTextMethod.invoke(content);
                    if (text instanceof String) {
                        llmResponse.append((String) text);
                    }
                }
            } catch (Exception e) {
                logger.warning("Could not extract text using reflection: " + e.getMessage());
            }
        }

        // If still empty, use string representation
        if (llmResponse.length() == 0) {
            llmResponse = new StringBuilder(content.toString());
        }

        return llmResponse.toString();
    }
}