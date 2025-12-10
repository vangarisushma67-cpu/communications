package com.lloyds.hackthon.customercoms.validation.agent;

public class ValidationAgentTest {
    public static void main(String[] args) {
        ValidationAgent validationAgent = new ValidationAgent();
        String result = validationAgent.processValidation("test");
        System.out.println(result);
    }
}
