package com.lloyds.hackthon.customercoms.communication.agent;

import com.lloyds.hackthon.customercoms.communication.tool.CommunicationTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class CommunicationAgent {
    private static final Logger logger = Logger.getLogger(CommunicationAgent.class.getName());
    private final CommunicationTool communicationTool;
    private final Properties config;

    public CommunicationAgent() {
        this.config = loadConfiguration();
        this.communicationTool = new CommunicationTool(config);
    }

    public String generateCommunication(String input) {
        logger.info("Generating communication for input: " + input);
        
        try {
            // Load communication prompt
            String prompt = loadPrompt();
            
            // Use communication tool to process the request
            return communicationTool.createCommunication(input, prompt);
        } catch (Exception e) {
            logger.severe("Error in communication generation: " + e.getMessage());
            return "{\"error\": \"Communication generation failed\", \"message\": \"" + e.getMessage() + "\"}";
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
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("communication-prompt.txt")) {
            if (input != null) {
                return new String(input.readAllBytes());
            } else {
                return "You are a communication agent. Please generate appropriate customer communication based on the provided input.";
            }
        } catch (IOException e) {
            logger.warning("Error loading prompt, using default: " + e.getMessage());
            return "You are a communication agent. Please generate appropriate customer communication based on the provided input.";
        }
    }
}