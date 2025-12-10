package com.lloyds.hackthon.customercoms.rules.tool;

import com.lloyds.hackthon.customercoms.rules.util.CommonUtils;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class RulesTool {
    private static final Logger logger = Logger.getLogger(RulesTool.class.getName());
    private final Properties config;
    private final String openAiApiKey;

    public RulesTool(Properties config) {
        this.config = config;
        this.openAiApiKey = config.getProperty("openai.api.key", "");
    }

    public Map<String, Object> applyRules() {
        logger.info("Applying business rules with OpenAI LLM");
        String rulesDefinition = CommonUtils.readCloudStorageFile("pension-data-raw","Execution-Workbook.xlsx");
        String validData = CommonUtils.readCloudStorageFile("pension-data-raw","Pension_Data.csv");

        String csvRules = "";

        return Map.of("executed-rules", csvRules);
    }
}