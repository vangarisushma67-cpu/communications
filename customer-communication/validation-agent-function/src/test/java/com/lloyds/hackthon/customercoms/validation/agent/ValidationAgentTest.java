package com.lloyds.hackthon.customercoms.validation.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationAgentTest {

    private ValidationAgent validationAgent;

    @BeforeEach
    void setUp() {
        validationAgent = new ValidationAgent();
    }

    @Test
    void processValidation_WithValidInput_ReturnsExpectedResult() {
        // Arrange
        String input = "test input";
        
        // Act
        String result = validationAgent.processValidation(input);

        // Assert
        assertNotNull(result);
        // Since we're not mocking, we can check the structure of the response
        // Adjust these assertions based on the actual expected response
        assertFalse(result.isEmpty());
        assertFalse(result.contains("error"));
    }

    @Test
    void processValidation_WithNullInput_ReturnsErrorResponse() {
        // Act
        String result = validationAgent.processValidation(null);

        // Assert
        assertNotNull(result);
        // Check if the response contains an error indicator
        assertTrue(result.contains("error") || result.contains("failed"));
    }

    @Test
    void processValidation_WithEmptyInput_ReturnsErrorResponse() {
        // Act
        String result = validationAgent.processValidation("");

        // Assert
        assertNotNull(result);
        // Check if the response contains an error indicator
        assertTrue(result.contains("error") || result.contains("failed"));
    }
    
    @Test
    void processValidation_WithSpecialCharacters_HandlesCorrectly() {
        // Arrange
        String input = "Test with special chars: & < > ' \"";
        
        // Act
        String result = validationAgent.processValidation(input);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
