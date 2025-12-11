package com.lloyds.hackthon.customercoms.summary.function;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.lloyds.hackthon.customercoms.summary.agent.SummaryAgent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class SummaryFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(SummaryFunction.class.getName());
    private final SummaryAgent summaryAgent;

    public SummaryFunction() {
        this.summaryAgent = new SummaryAgent();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.setContentType("application/json");
        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatusCode(200);
            return;
        }

        try {
            String requestBody = request.getReader().lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
            
            logger.info("Received pension summary request");
            
            String result;
            
            // Check if this is a request for a specific customer or batch processing
            if (requestBody != null && requestBody.contains("\"customerData\"")) {
                // Single customer processing
                result = summaryAgent.generateSummaryForCustomer(requestBody);
            } else {
                // Batch processing from CSV
                result = summaryAgent.generateSummary(requestBody);
            }
            
            try (BufferedWriter writer = response.getWriter()) {
                writer.write(result);
            }
            
            response.setStatusCode(200);
            logger.info("Pension summary request processed successfully");
            
        } catch (Exception e) {
            logger.severe("Error processing pension summary request: " + e.getMessage());
            response.setStatusCode(500);
            try (BufferedWriter writer = response.getWriter()) {
                writer.write("{\"status\": \"error\", \"message\": \"Internal server error: " + e.getMessage() + "\"}");
            }
        }
    }
}