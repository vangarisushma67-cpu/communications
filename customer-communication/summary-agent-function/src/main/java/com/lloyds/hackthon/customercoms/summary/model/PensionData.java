package com.lloyds.hackthon.customercoms.summary.model;

import com.opencsv.bean.CsvBindByName;

public class PensionData {
    @CsvBindByName(column = "customer_name")
    private String customerName;
    
    @CsvBindByName(column = "customer_id")
    private String customerId;
    
    @CsvBindByName(column = "product_type")
    private String productType;
    
    @CsvBindByName(column = "account_number")
    private String accountNumber;
    
    @CsvBindByName(column = "current_value")
    private String currentValue;
    
    @CsvBindByName(column = "annual_contribution")
    private String annualContribution;
    
    @CsvBindByName(column = "employer_contribution")
    private String employerContribution;
    
    @CsvBindByName(column = "investment_return")
    private String investmentReturn;
    
    @CsvBindByName(column = "projected_retirement_value")
    private String projectedRetirementValue;
    
    @CsvBindByName(column = "retirement_age")
    private String retirementAge;
    
    @CsvBindByName(column = "risk_profile")
    private String riskProfile;
    
    @CsvBindByName(column = "fund_allocation")
    private String fundAllocation;

    // Constructors
    public PensionData() {}

    public PensionData(String customerName, String customerId, String productType, 
                      String accountNumber, String currentValue, String annualContribution,
                      String employerContribution, String investmentReturn, 
                      String projectedRetirementValue, String retirementAge,
                      String riskProfile, String fundAllocation) {
        this.customerName = customerName;
        this.customerId = customerId;
        this.productType = productType;
        this.accountNumber = accountNumber;
        this.currentValue = currentValue;
        this.annualContribution = annualContribution;
        this.employerContribution = employerContribution;
        this.investmentReturn = investmentReturn;
        this.projectedRetirementValue = projectedRetirementValue;
        this.retirementAge = retirementAge;
        this.riskProfile = riskProfile;
        this.fundAllocation = fundAllocation;
    }

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getCurrentValue() { return currentValue; }
    public void setCurrentValue(String currentValue) { this.currentValue = currentValue; }

    public String getAnnualContribution() { return annualContribution; }
    public void setAnnualContribution(String annualContribution) { this.annualContribution = annualContribution; }

    public String getEmployerContribution() { return employerContribution; }
    public void setEmployerContribution(String employerContribution) { this.employerContribution = employerContribution; }

    public String getInvestmentReturn() { return investmentReturn; }
    public void setInvestmentReturn(String investmentReturn) { this.investmentReturn = investmentReturn; }

    public String getProjectedRetirementValue() { return projectedRetirementValue; }
    public void setProjectedRetirementValue(String projectedRetirementValue) { this.projectedRetirementValue = projectedRetirementValue; }

    public String getRetirementAge() { return retirementAge; }
    public void setRetirementAge(String retirementAge) { this.retirementAge = retirementAge; }

    public String getRiskProfile() { return riskProfile; }
    public void setRiskProfile(String riskProfile) { this.riskProfile = riskProfile; }

    public String getFundAllocation() { return fundAllocation; }
    public void setFundAllocation(String fundAllocation) { this.fundAllocation = fundAllocation; }

    @Override
    public String toString() {
        return "PensionData{" +
                "customerName='" + customerName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", productType='" + productType + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", currentValue='" + currentValue + '\'' +
                ", annualContribution='" + annualContribution + '\'' +
                ", employerContribution='" + employerContribution + '\'' +
                ", investmentReturn='" + investmentReturn + '\'' +
                ", projectedRetirementValue='" + projectedRetirementValue + '\'' +
                ", retirementAge='" + retirementAge + '\'' +
                ", riskProfile='" + riskProfile + '\'' +
                ", fundAllocation='" + fundAllocation + '\'' +
                '}';
    }
}