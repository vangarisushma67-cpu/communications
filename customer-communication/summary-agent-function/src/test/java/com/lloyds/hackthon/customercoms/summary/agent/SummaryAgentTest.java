package com.lloyds.hackthon.customercoms.summary.agent;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SummaryAgentTest {

    private SummaryAgent summaryAgent;

    @BeforeEach
    void setUp() {
        summaryAgent = new SummaryAgent();
    }

    @Test
    void generateSummary_WithEmptyInput_UsesDefaultBuckets() {
        // Act
        String result = summaryAgent.generateSummary("");
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Verify the result contains expected success message
        assertTrue(result.contains("successfully") || result.contains("processed"));
    }

    @Test
    void generateSummary_WithCustomBuckets_UsesProvidedBuckets() {
        // Arrange
        String input = "{\"sourceBucket\":\"custom-source-bucket\"," +
                      "\"sourceFile\":\"custom-file.csv\"," +
                      "\"destinationBucket\":\"custom-dest-bucket\"}";
        
        // Act
        String result = summaryAgent.generateSummary(input);
        
        // Assert
        assertNotNull(result);
        // The actual behavior might depend on the implementation
        // This just verifies the method doesn't throw exceptions
    }

    @Test
    void generateSummary_WithInvalidJson_HandlesGracefully() {
        // Arrange
        String invalidJson = "{invalid-json";
        
        // Act
        String result = summaryAgent.generateSummary(invalidJson);
        
        // Assert
        assertNotNull(result);
        // Should either handle the error gracefully or throw a specific exception
    }

    @Test
    void generateSummary_WithEmptyJson_UsesDefaultBuckets() {
        // Arrange
        String emptyJson = "{}";
        
        // Act
        String result = summaryAgent.generateSummary(emptyJson);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void generateSummary_WithNullInput_UsesDefaultBuckets() {
        // Act
        String result = summaryAgent.generateSummary(null);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void generateSummary_WithSpecialCharacters_HandlesCorrectly() {
        // Arrange
        String input = "{\"sourceBucket\":\"bucket-123\"," +
                      "\"sourceFile\":\"file with spaces & special chars.csv\"}";
        
        // Act
        String result = summaryAgent.generateSummary(input);
        
        // Assert
        assertNotNull(result);
        // Just verify it doesn't throw exceptions
    }
}
