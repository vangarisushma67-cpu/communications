package com.lloyds.hackthon.customercoms.rules.agent;

import com.lloyds.hackthon.customercoms.rules.tool.RulesTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class RulesAgent {
    private static final Logger logger = Logger.getLogger(RulesAgent.class.getName());
    private final RulesTool rulesTool;
    private final Properties config;

    public RulesAgent() {
        this.config = loadConfiguration();
        this.rulesTool = new RulesTool(config);
    }

    public String processRules(String input) {
        logger.info("Processing rules for input: " + input);
        
        try {
            // Load rules prompt
            String prompt = loadPrompt();
            
            // Use rules tool to process the request
            return rulesTool.applyRules(input, prompt);
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
}