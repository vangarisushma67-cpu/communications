package com.lloyds.hackthon.customercoms.summary.agent;

import com.lloyds.hackthon.customercoms.summary.tool.SummaryTool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class SummaryAgent {
    private static final Logger logger = Logger.getLogger(SummaryAgent.class.getName());
    private final SummaryTool summaryTool;
    private final Properties config;

    public SummaryAgent() {
        this.config = loadConfiguration();
        this.summaryTool = new SummaryTool(config);
    }

    public String generateSummary(String input) {
        logger.info("Generating summary for input: " + input);
        
        try {
            // Load summary prompt
            String prompt = loadPrompt();
            
            // Use summary tool to process the request
            return summaryTool.createSummary(input, prompt);
        } catch (Exception e) {
            logger.severe("Error in summary generation: " + e.getMessage());
            return "{\"error\": \"Summary generation failed\", \"message\": \"" + e.getMessage() + "\"}";
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
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("summary-prompt.txt")) {
            if (input != null) {
                return new String(input.readAllBytes());
            } else {
                return "You are a summary agent. Please create a concise summary of the following content.";
            }
        } catch (IOException e) {
            logger.warning("Error loading prompt, using default: " + e.getMessage());
            return "You are a summary agent. Please create a concise summary of the following content.";
        }
    }
}