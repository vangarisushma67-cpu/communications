package com.lloyds.hackthon.customercoms.rules.tool;
import com.google.adk.tools.Annotations;
import com.google.adk.tools.ToolContext;
import com.google.gson.Gson;
import com.lloyds.hackthon.customercoms.rules.util.CommonUtils;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RulesTool {
    private static final Logger logger = Logger.getLogger(RulesTool.class.getName());
    private final Gson gson = new Gson();

    private static final String[] RULES_TABS = {
        "Data Integrity & Vulnerability", 
        "Price & Value Assessment",
        "Clarity & Understanding", 
        "Outlier & Consistency Check"
    };

    public RulesTool() {
    }

    public static Map<String, Object> applyRules() {
        logger.info("Invokes RulesTool");
        
        try {
            // 1. Load the rules from Excel
            Map<String, List<List<String>>> rulesWorkbook = CommonUtils.readExcelFromCloudStorage(
                "pension-data-rules",
                "Execution-Workbook.xlsx"
            );
            
            // 2. Load the data to be validated
            String validData = CommonUtils.readCloudStorageFile("pension-data-rules", "pension-data-valid.csv");
            
            // 3. Process each row of data against the rules
            String[] rows = validData.split("\n");
            List<String> processedRows = new ArrayList<>();
            
            // Add header with RAG_Trigger column
            if (rows.length > 0) {
                processedRows.add(rows[0].trim() + ",RAG_Trigger");
            }
            
            // Process data rows
            for (int i = 1; i < rows.length; i++) {
                String row = rows[i].trim();
                if (row.isEmpty()) continue;
                
                Map<String, List<String>> rowTriggers = new HashMap<>();
                
                // Apply rules from each tab
                for (String tabName : RULES_TABS) {
                    List<List<String>> rules = rulesWorkbook.get(tabName);
                    if (rules != null) {
                        List<String> triggers = applyRulesFromTab(tabName, rules, row);
                        if (!triggers.isEmpty()) {
                            rowTriggers.put(tabName, triggers);
                        }
                    }
                }
                
                // Add RAG trigger information to the row
                String ragTrigger = rowTriggers.isEmpty() 
                    ? "No issues" 
                    : rowTriggers.entrySet().stream()
                        .map(e -> e.getKey() + ": " + String.join("; ", e.getValue()))
                        .collect(Collectors.joining(" | "));
                
                processedRows.add(row + "," + ragTrigger);
            }
            
            // 4. Save the processed data
            String outputCsv = String.join("\n", processedRows);
            String outputFileName = "Pension_Data_With_RAG.csv";
            CommonUtils.writeCloudStorageFile("pension-data-summary", outputFileName, outputCsv);
            
            // 5. Return results
            return Map.of(
                "status", "success",
                "processed_rows", rows.length - 1,
                "output_file", outputFileName,
                "message", "Rules applied successfully"
            );
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error applying rules: " + e.getMessage(), e);
            return Map.of(
                "status", "error",
                "message", "Failed to apply rules: " + e.getMessage()
            );
        }
    }
    
    private static List<String> applyRulesFromTab(String tabName, List<List<String>> rules, String rowData) {
        List<String> triggers = new ArrayList<>();
        
        // Skip header row if present
        int startRow = (rules.size() > 0 && rules.get(0).get(0).equalsIgnoreCase("Rule ID")) ? 1 : 0;
        
        for (int i = startRow; i < rules.size(); i++) {
            List<String> rule = rules.get(i);
            if (rule.size() < 3) continue; // Ensure we have at least rule ID, condition, and description
            
            String ruleId = rule.get(0);
            String condition = rule.get(1);
            String description = rule.size() > 2 ? rule.get(2) : "";
            
            try {
                // In a real implementation, you would evaluate the condition against the rowData
                // This is a simplified example that just checks if the condition is mentioned in the row
                if (rowData.toLowerCase().contains(condition.toLowerCase())) {
                    triggers.add(String.format("%s: %s", ruleId, description));
                }
            } catch (Exception e) {
                logger.warning(String.format("Error applying rule %s: %s", ruleId, e.getMessage()));
            }
        }
        
        return triggers;
    }
}