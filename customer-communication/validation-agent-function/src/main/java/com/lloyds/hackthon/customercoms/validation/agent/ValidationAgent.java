package com.lloyds.hackthon.customercoms.validation.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.lloyds.hackthon.customercoms.validation.tool.ValidationTool;
import com.lloyds.hackthon.customercoms.validation.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ValidationAgent {
    private static final Logger logger = Logger.getLogger(ValidationAgent.class.getName());
    private final ValidationTool validationTool;
    private final Properties config;

    public ValidationAgent() {
        this.config = loadConfiguration();
        this.validationTool = new ValidationTool(config);
    }

    public String processValidation(String input) {
        logger.info("Processing validation for input: " + input);
        String pensionData = CommonUtils.readCloudStorageFile("pension-data-raw","Pension_Data.csv");
        try {
            // Load validation prompt
            String prompt = loadPrompt();
            LlmAgent agent = LlmAgent.builder()
                    .name("Validation Agent")
                    .description("You are a validation agent. Please validate the following input and provide feedback.")
                    // Set the model. gemini-2.5-flash is ideal for fast, structured output.
                    .model("gemini-2.5-pro")
                    .instruction(prompt + "\n" + pensionData)
                    .build();

            String response = sendAndGetResponse(agent);
            System.out.println(response);
            CommonUtils.writeCloudStorageFile("pension-data-rules", "pension-data-valid.csv", response);
            return validationTool.validate(input, prompt);
        } catch (Exception e) {
            logger.severe("Error in validation processing: " + e.getMessage());
            return "{\"error\": \"Validation processing failed\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }
    /**
     * Extracts text from a Content object using multiple fallback methods
     */
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
    private Properties loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                logger.warning("application.properties not found, using default configuration");
            }
        } catch (IOException e) {
            logger.severe("Error loading configuration: " + e.getMessage());
        }
        return props;
    }

    private String loadPrompt() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("validation-prompt.txt")) {
            if (input != null) {
                return new String(input.readAllBytes());
            } else {
                return "You are a validation agent. Please validate the following input and provide feedback.";
            }
        } catch (IOException e) {
            logger.warning("Error loading prompt, using default: " + e.getMessage());
            return "You are a validation agent. Please validate the following input and provide feedback.";
        }
    }

    public String sendAndGetResponse(LlmAgent agent) {
        logger.info("Sending message to LLM agent: " + agent.name());
        String runid = String.valueOf(new Random().nextInt());
        InMemoryRunner runner = new InMemoryRunner(agent);
        Session session = runner.sessionService()
                .createSession(agent.name(), runid)
                .blockingGet();

        // Use the agent's instruction as the user message
        String userMessage = agent.instruction().toString();

        Content userMsg = Content.fromParts(Part.fromText(userMessage));
//        logger.info("Sending message: " + userMessage);

        // Send the message and collect the response
        StringBuilder responseBuilder = new StringBuilder();
        runner.runAsync(runid, session.id(), userMsg)
                .blockingForEach(event -> {
                    if (event.content().isPresent()) {
                        Content content = event.content().get();
                        // Extract text from content parts
                        String response = extractTextFromContent(content);
                        if (!response.isEmpty()) {
                            responseBuilder.append(response).append("\n");
                        }
                    }
                });

        return responseBuilder.toString().trim();
    }
}