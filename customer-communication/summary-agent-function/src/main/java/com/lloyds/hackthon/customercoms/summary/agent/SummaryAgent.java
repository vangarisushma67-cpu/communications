package com.lloyds.hackthon.customercoms.summary.agent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lloyds.hackthon.customercoms.summary.model.PensionData;
import com.lloyds.hackthon.customercoms.summary.service.GoogleCloudStorageService;
import com.lloyds.hackthon.customercoms.summary.service.PensionPdfGeneratorService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class SummaryAgent {
    private static final Logger logger = Logger.getLogger(SummaryAgent.class.getName());
    private final GoogleCloudStorageService gcsService;
    private final PensionPdfGeneratorService pdfGeneratorService;
    private final Properties config;
    private final Gson gson;

    // Default bucket names
    private static final String SOURCE_BUCKET = "pension-data-summary";
    private static final String SOURCE_FILE = "Pension_Data_With_RAG.csv";
    private static final String DESTINATION_BUCKET = "pension-data-communication";

    public SummaryAgent() {
        this.config = loadConfiguration();
        this.gcsService = new GoogleCloudStorageService();
        this.pdfGeneratorService = new PensionPdfGeneratorService();
        this.gson = new Gson();
    }

    /**
     * Main method to process pension data and generate PDF summaries
     */
    public String generateSummary(String input) {
        logger.info("Starting pension summary generation process");
        
        try {
            // Parse input to get bucket and file information (if provided)
            JsonObject inputJson = parseInput(input);
            String sourceBucket = inputJson.has("sourceBucket") ? 
                inputJson.get("sourceBucket").getAsString() : SOURCE_BUCKET;
            String sourceFile = inputJson.has("sourceFile") ? 
                inputJson.get("sourceFile").getAsString() : SOURCE_FILE;
            String destinationBucket = inputJson.has("destinationBucket") ? 
                inputJson.get("destinationBucket").getAsString() : DESTINATION_BUCKET;
            
            // Download and parse CSV data
            List<PensionData> pensionDataList = gcsService.downloadAndParseCsv(sourceBucket, sourceFile);
            
            if (pensionDataList.isEmpty()) {
                return createErrorResponse("No pension data found in the CSV file");
            }
            
            // Generate PDF for each pension record
            int successCount = 0;
            int errorCount = 0;
            
            for (PensionData pensionData : pensionDataList) {
                try {
                    // Generate PDF
                    byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
                    
                    // Create filename: ${customer_name}_summary.pdf
                    String fileName = sanitizeFileName(pensionData.getCustomerName()) + "_summary.pdf";
                    
                    // Upload to destination bucket
                    gcsService.uploadPdf(destinationBucket, fileName, pdfContent);
                    
                    successCount++;
                    logger.info("Successfully generated PDF for customer: " + pensionData.getCustomerName());
                    
                } catch (Exception e) {
                    errorCount++;
                    logger.severe("Error generating PDF for customer " + pensionData.getCustomerName() + ": " + e.getMessage());
                }
            }
            
            // Return summary of the operation
            return createSuccessResponse(pensionDataList.size(), successCount, errorCount, destinationBucket);
            
        } catch (Exception e) {
            logger.severe("Error in pension summary generation: " + e.getMessage());
            return createErrorResponse("Pension summary generation failed: " + e.getMessage());
        }
    }

    /**
     * Alternative method to process specific customer data
     */
    public String generateSummaryForCustomer(String customerData) {
        logger.info("Generating summary for specific customer data");
        
        try {
            // Parse customer data JSON
            PensionData pensionData = gson.fromJson(customerData, PensionData.class);
            
            // Generate PDF
            byte[] pdfContent = pdfGeneratorService.generatePensionStatementPdf(pensionData);
            
            // Create filename
            String fileName = sanitizeFileName(pensionData.getCustomerName()) + "_summary.pdf";
            
            // Upload to destination bucket
            gcsService.uploadPdf(DESTINATION_BUCKET, fileName, pdfContent);
            
            return createSingleCustomerSuccessResponse(pensionData.getCustomerName(), fileName);
            
        } catch (Exception e) {
            logger.severe("Error generating summary for customer: " + e.getMessage());
            return createErrorResponse("Customer summary generation failed: " + e.getMessage());
        }
    }

    private JsonObject parseInput(String input) {
        try {
            if (input == null || input.trim().isEmpty()) {
                return new JsonObject();
            }
            return gson.fromJson(input, JsonObject.class);
        } catch (Exception e) {
            logger.warning("Could not parse input as JSON, using defaults: " + e.getMessage());
            return new JsonObject();
        }
    }

    private String sanitizeFileName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return "unknown_customer";
        }
        
        // Replace spaces and special characters with underscores
        return customerName.trim()
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", "_")
                .toLowerCase();
    }

    private String createSuccessResponse(int totalRecords, int successCount, int errorCount, String destinationBucket) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "Pension summary generation completed");
        response.addProperty("totalRecords", totalRecords);
        response.addProperty("successfulPdfs", successCount);
        response.addProperty("failedPdfs", errorCount);
        response.addProperty("destinationBucket", destinationBucket);
        response.addProperty("timestamp", java.time.Instant.now().toString());
        
        return gson.toJson(response);
    }

    private String createSingleCustomerSuccessResponse(String customerName, String fileName) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "PDF generated successfully for customer: " + customerName);
        response.addProperty("customerName", customerName);
        response.addProperty("fileName", fileName);
        response.addProperty("destinationBucket", DESTINATION_BUCKET);
        response.addProperty("timestamp", java.time.Instant.now().toString());
        
        return gson.toJson(response);
    }

    private String createErrorResponse(String errorMessage) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "error");
        response.addProperty("message", errorMessage);
        response.addProperty("timestamp", java.time.Instant.now().toString());
        
        return gson.toJson(response);
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
}