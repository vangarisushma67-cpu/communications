package com.lloyds.hackthon.customercoms.validation.agent;

import com.lloyds.hackthon.customercoms.validation.tool.ValidationTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

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
        
        try {
            // Load validation prompt
            String prompt = loadPrompt();
            
            // Use validation tool to process the request
            return validationTool.validate(input, prompt);
        } catch (Exception e) {
            logger.severe("Error in validation processing: " + e.getMessage());
            return "{\"error\": \"Validation processing failed\", \"message\": \"" + e.getMessage() + "\"}";
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
}