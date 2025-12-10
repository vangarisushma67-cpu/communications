package com.lloyds.hackthon.customercoms.validation.function;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.lloyds.hackthon.customercoms.validation.agent.ValidationAgent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class ValidationFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(ValidationFunction.class.getName());
    private final ValidationAgent validationAgent;

    public ValidationFunction() {
        this.validationAgent = new ValidationAgent();
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
            
            logger.info("Received validation request: " + requestBody);
            
            String result = validationAgent.processValidation(requestBody);
            
            try (BufferedWriter writer = response.getWriter()) {
                writer.write(result);
            }
            
            response.setStatusCode(200);
        } catch (Exception e) {
            logger.severe("Error processing validation request: " + e.getMessage());
            response.setStatusCode(500);
            try (BufferedWriter writer = response.getWriter()) {
                writer.write("{\"error\": \"Internal server error\"}");
            }
        }
    }
}