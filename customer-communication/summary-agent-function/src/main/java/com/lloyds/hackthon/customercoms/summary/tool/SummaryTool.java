package com.lloyds.hackthon.customercoms.summary.tool;

import java.util.Properties;
import java.util.logging.Logger;

public class SummaryTool {
    private static final Logger logger = Logger.getLogger(SummaryTool.class.getName());
    public SummaryTool() {
    }

    public String createSummary(String input, String prompt) {
        logger.info("Creating summary with OpenAI LLM");
        
        // Placeholder summary logic
        if (input == null || input.trim().isEmpty()) {
            return "{\"status\": \"error\", \"message\": \"No input provided for summary\", \"summary\": \"\"}";
        }

        // Simple placeholder summary
        String placeholderSummary = "This is a placeholder summary of the provided content. " +
                "The original content was " + input.length() + " characters long.";
        
        return String.format(
            "{\"status\": \"success\", \"message\": \"Summary generated successfully\", \"summary\": \"%s\", \"original_length\": %d, \"summary_length\": %d, \"compression_ratio\": %.2f}",
            placeholderSummary,
            input.length(),
            placeholderSummary.length(),
            (double) placeholderSummary.length() / input.length()
        );
    }
}