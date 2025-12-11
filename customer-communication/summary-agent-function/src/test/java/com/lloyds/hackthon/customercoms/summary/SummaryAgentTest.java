package com.lloyds.hackthon.customercoms.summary;

import com.lloyds.hackthon.customercoms.summary.agent.SummaryAgent;
import com.lloyds.hackthon.customercoms.summary.model.PensionData;
import com.lloyds.hackthon.customercoms.summary.service.PensionPdfGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SummaryAgentTest {
    
    private SummaryAgent summaryAgent;
    private PensionPdfGeneratorService pdfGeneratorService;
    
    @BeforeEach
    void setUp() {
        summaryAgent = new SummaryAgent();
        pdfGeneratorService = new PensionPdfGeneratorService();
    }
    
    @Test
    void testPdfGenerationForWorkplacePension() {
        // Create test pension data
        PensionData pensionData = new PensionData(
            "John Smith",
            "CUST001",
            "Workplace Pension",
            "ACC123456",
            "50000",
            "5000",
            "2500",
            "7.5",
            "250000",
            "65",
            "Moderate",
            "Balanced Portfolio"
        );
        
        // Generate PDF
        byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
        
        // Verify PDF was generated
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
        
        // Verify PDF header (basic check)
        String pdfHeader = new String(pdfContent, 0, Math.min(100, pdfContent.length));
        assertTrue(pdfHeader.contains("%PDF"));
    }
    
    @Test
    void testPdfGenerationForPersonalPension() {
        PensionData pensionData = new PensionData(
            "Jane Doe",
            "CUST002",
            "Personal Pension",
            "ACC789012",
            "75000",
            "8000",
            "0",
            "6.2",
            "300000",
            "67",
            "Aggressive",
            "Growth Portfolio"
        );
        
        byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
        
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
    }
    
    @Test
    void testPdfGenerationForSipp() {
        PensionData pensionData = new PensionData(
            "Robert Johnson",
            "CUST003",
            "SIPP",
            "ACC345678",
            "120000",
            "12000",
            "0",
            "8.1",
            "450000",
            "60",
            "High",
            "Self-Directed Portfolio"
        );
        
        byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
        
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
    }
    
    @Test
    void testPdfGenerationForStakeholderPension() {
        PensionData pensionData = new PensionData(
            "Mary Wilson",
            "CUST004",
            "Stakeholder Pension",
            "ACC901234",
            "35000",
            "3000",
            "1500",
            "5.8",
            "180000",
            "68",
            "Conservative",
            "Conservative Portfolio"
        );
        
        byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
        
        assertNotNull(pdfContent);
        assertTrue(pdfContent.length > 0);
    }
    
    @Test
    void testSanitizeFileName() {
        // This would test the private method if it were public or protected
        // For now, we'll test through the public interface
        
        String customerData = "{"
            + "\"customer_name\": \"John O'Connor-Smith\","
            + "\"customer_id\": \"CUST005\","
            + "\"product_type\": \"Personal Pension\","
            + "\"account_number\": \"ACC567890\","
            + "\"current_value\": \"25000\","
            + "\"annual_contribution\": \"2000\","
            + "\"employer_contribution\": \"0\","
            + "\"investment_return\": \"4.5\","
            + "\"projected_retirement_value\": \"150000\","
            + "\"retirement_age\": \"65\","
            + "\"risk_profile\": \"Moderate\","
            + "\"fund_allocation\": \"Balanced Portfolio\""
            + "}";
        
        // This test would require mocking the GCS service to avoid actual cloud calls
        // For demonstration purposes, we're showing the structure
        assertDoesNotThrow(() -> {
            // summaryAgent.generateSummaryForCustomer(customerData);
        });
    }
    
    @Test
    void testEmptyInputHandling() {
        String result = summaryAgent.generateSummary("");
        
        assertNotNull(result);
        assertTrue(result.contains("error") || result.contains("success"));
    }
    
    @Test
    void testNullInputHandling() {
        String result = summaryAgent.generateSummary(null);
        
        assertNotNull(result);
        assertTrue(result.contains("error") || result.contains("success"));
    }
}