package com.lloyds.hackthon.customercoms.rules.function;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.lloyds.hackthon.customercoms.rules.agent.RulesAgent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class RulesFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(RulesFunction.class.getName());
    private final RulesAgent rulesAgent;

    public RulesFunction() {
        this.rulesAgent = new RulesAgent();
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
            
            logger.info("Received rules processing request: " + requestBody);
            
            String result = rulesAgent.processRules(requestBody);
            
            try (BufferedWriter writer = response.getWriter()) {
                writer.write(result);
            }
            
            response.setStatusCode(200);
        } catch (Exception e) {
            logger.severe("Error processing rules request: " + e.getMessage());
            response.setStatusCode(500);
            try (BufferedWriter writer = response.getWriter()) {
                writer.write("{\"error\": \"Internal server error\"}");
            }
        }
    }
}