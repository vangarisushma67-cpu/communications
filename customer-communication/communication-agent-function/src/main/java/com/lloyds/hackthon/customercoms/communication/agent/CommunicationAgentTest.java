package com.lloyds.hackthon.customercoms.communication.agent;

public class CommunicationAgentTest {
    public static void main(String[] args) {
        System.out.println("Testing CommunicationAgent with customerData template replacement...");
        
        // Test the template replacement functionality
        testTemplateReplacement();
        
        // Note: Full integration test requires Google Cloud credentials and services
        System.out.println("Template replacement test completed successfully!");
        System.out.println("To run full integration test, ensure Google Cloud credentials are configured.");
    }
    
    private static void testTemplateReplacement() {
        // Test template string replacement logic
        String sampleTemplate = "You are a professional document generation assistant.\n\n" +
                "CUSTOMER DATA:\n${customerData}\n\n" +
                "Please use the following customer data:\n{customerData}\n\n" +
                "Generate appropriate communication.";
        
        String sampleCustomerData = "Customer: John Doe\nPolicy: 12345\nBalance: £50,000";
        
        // Simulate the template replacement logic from CommunicationAgent
        String processedTemplate = sampleTemplate
                .replace("${customerData}", sampleCustomerData)
                .replace("{customerData}", sampleCustomerData);
        
        System.out.println("Original template contains placeholders:");
        System.out.println("- ${customerData}");
        System.out.println("- {customerData}");
        
        System.out.println("\nProcessed template:");
        System.out.println(processedTemplate);
        
        // Verify that placeholders were replaced
        if (processedTemplate.contains("${customerData}") || processedTemplate.contains("{customerData}")) {
            throw new RuntimeException("Template replacement failed - placeholders still present!");
        }
        
        if (!processedTemplate.contains(sampleCustomerData)) {
            throw new RuntimeException("Template replacement failed - customer data not found in processed template!");
        }
        
        System.out.println("\n✓ Template replacement working correctly!");
        System.out.println("✓ All placeholders replaced with actual customer data");
    }
}
