package com.lloyds.hackthon.customercoms.communication.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.lloyds.hackthon.customercoms.communication.util.CommonUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                String statementData = CommonUtils.readCloudStorageFile("pension-data-communication", statement);
                String promptTemplate = loadPrompt();
                
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

    public String sendAndGetResponse(LlmAgent agent, String statementData, String outputFilename) {
            logger.info("Sending message to LLM agent: " + agent.name());
            String runid = String.valueOf(new Random().nextInt());
            InMemoryRunner runner = new InMemoryRunner(agent);
            Session session = runner.sessionService()
                    .createSession(agent.name(), runid)
                    .blockingGet();

            // Create a new state with the customer data
            Map<String, Object> state = new HashMap<>();
            state.put("customerData", statementData);
            
            // Update the session with the new state
            session.state().putAll(state);
            
            // Create the user message with both instruction and customer data
            Content userMsg = Content.fromParts(
                    Part.fromText(agent.instruction().toString() + "\n\nCUSTOMER DATA:\n" + statementData)
            );
            
            // Set the customer data in the session state
            session.state().put("customerData", statementData);
            // Send the message and collect the HTML response
            StringBuilder htmlBuilder = new StringBuilder();
            runner.runAsync(runid, session.id(), userMsg)
                    .blockingForEach(event -> {
                        if (event.content().isPresent()) {
                            String response = extractTextFromContent(event.content().get());
                            if (!response.isEmpty()) {
                                htmlBuilder.append(response).append("\n");
                            }
                        }
                    });

            String htmlContent = htmlBuilder.toString().trim();

            try {
                byte[] pdfBytes = generatePdfFromHtml(htmlContent);

                // Upload PDF to Cloud Storage
                String bucketName = "final-communication";
                CommonUtils.writeCloudStorageFile(bucketName, outputFilename, pdfBytes, "application/pdf");

                return "PDF generated and uploaded successfully to " + bucketName + "/" + outputFilename;
            } catch (Exception e) {
                logger.severe("Error generating/uploading PDF: " + e.getMessage());
                throw new RuntimeException("Failed to generate/upload PDF", e);
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
            }
        }

        private String extractTextFromContent(Content content) {
        String llmResponse = "";
        // First try to get text from parts
        if (content.parts() != null && content.parts().isPresent()) {
            llmResponse = content.parts().get().stream()
                    .filter(part -> part != null && part.text() != null && part.text().isPresent())
                    .map(part -> part.text().get())
                    .collect(Collectors.joining("\n"));
        }

        // If no text from parts, try reflection
        if (llmResponse.isEmpty()) {
            try {
                java.lang.reflect.Method getTextMethod = content.getClass().getMethod("getText");
                if (getTextMethod != null) {
                    Object text = getTextMethod.invoke(content);
                    if (text instanceof String) {
                        llmResponse = (String) text;
                    }
                }
            } catch (Exception e) {
                logger.warning("Could not extract text using reflection: " + e.getMessage());
            }
        }

        // If still empty, use string representation
        if (llmResponse.isEmpty()) {
            llmResponse = content.toString();
        }

        return llmResponse;
    }
}