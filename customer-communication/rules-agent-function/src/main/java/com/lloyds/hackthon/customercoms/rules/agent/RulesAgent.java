package com.lloyds.hackthon.customercoms.rules.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.lloyds.hackthon.customercoms.rules.tool.RulesTool;
import com.lloyds.hackthon.customercoms.rules.util.CommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RulesAgent {
    private static final Logger logger = Logger.getLogger(RulesAgent.class.getName());
    private final Properties config;

    public RulesAgent() {
        this.config = loadConfiguration();
    }

    public String processRules(String input) {
        logger.info("Processing rules for input: " + input);
        
        try {
            String validData = CommonUtils.readCloudStorageFile("pension-data-communication","pension-data-valid.csv");
            // Load rules prompt
            String prompt = loadPrompt();
            LlmAgent agent = LlmAgent.builder()
                    .name("Rules Agent")
                    .description("You are a rules agent. Please execute the rules using RulesTool.")
                    // Set the model. gemini-2.5-flash is ideal for fast, structured output.
                    .model("gemini-2.5-pro")
                    .instruction(prompt + "\n" + validData)
                    .tools(List.of(FunctionTool.create(RulesTool.class, "applyRules")))
                    .build();
            // Use rules tool to process the request
            String response = sendAndGetResponse(agent);
//            CommonUtils.writeCloudStorageFile("pension-data-summary", "pension-data-summary.csv", response);
            return response;
        } catch (Exception e) {
            logger.severe("Error in rules processing: " + e.getMessage());
            return "{\"error\": \"Rules processing failed\", \"message\": \"" + e.getMessage() + "\"}";
        }
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
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("rules-prompt.txt")) {
            if (input != null) {
                return new String(input.readAllBytes());
            } else {
                return "You are a rules agent. Please apply business rules to the following input.";
            }
        } catch (IOException e) {
            logger.warning("Error loading prompt, using default: " + e.getMessage());
            return "You are a rules agent. Please apply business rules to the following input.";
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
}