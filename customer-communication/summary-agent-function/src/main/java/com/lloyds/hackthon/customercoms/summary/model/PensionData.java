package com.lloyds.hackthon.customercoms.summary.model;

import com.opencsv.bean.CsvBindByName;

public class PensionData {
    // Customer Information
    @CsvBindByName(column = "FIRSTNAME")
    private String firstName;

    @CsvBindByName(column = "SURNAME")
    private String surname;

    @CsvBindByName(column = "CUSTOMER_AGE")
    private Integer customerAge;

    @CsvBindByName(column = "SALUTATION")
    private String salutation;

    @CsvBindByName(column = "LETTER_TITLE")
    private String letterTitle;

    @CsvBindByName(column = "DOB")
    private String dateOfBirth;

    // Address Information
    @CsvBindByName(column = "HOUSE_NO")
    private String houseNumber;

    @CsvBindByName(column = "STREET")
    private String street;

    @CsvBindByName(column = "POSTTOWN")
    private String postTown;

    @CsvBindByName(column = "COUNTY")
    private String county;

    @CsvBindByName(column = "COUNTRY")
    private String country;

    @CsvBindByName(column = "POSTCODE")
    private String postcode;

    // Policy Information
    @CsvBindByName(column = "POLICY_NUMBER")
    private String policyNumber;

    @CsvBindByName(column = "SCHEME_NUMBER")
    private String schemeNumber;

    @CsvBindByName(column = "SCHEME_NAME")
    private String schemeName;

    @CsvBindByName(column = "BANCS_PRODUCT_CD")
    private String bancsProductCode;

    @CsvBindByName(column = "LEGACY CLASS CODE")
    private String legacyClassCode;

    @CsvBindByName(column = "BANCS_PRODUCT_NAME")
    private String productName;

    @CsvBindByName(column = "NORMAL_RETIREMENT_DATE")
    private String normalRetirementDate;

    @CsvBindByName(column = "PRODUCT_TYPE")
    private String productType;

    // Payment Information
    @CsvBindByName(column = "PAYMENTS IN")
    private String paymentsIn;

    @CsvBindByName(column = "PAYMENT FREQUENCY")
    private String paymentFrequency;

    // Policy Values
    @CsvBindByName(column = "POLICY_VALUE 2025")
    private String policyValue2025;

    @CsvBindByName(column = "POLICY_VALUE 2024")
    private String policyValue2024;

    @CsvBindByName(column = "FUTURE VALUE @ NRD")
    private String futureValueAtNRD;

    @CsvBindByName(column = "ANNUAL TAXABLE INCOME")
    private String annualTaxableIncome;

    @CsvBindByName(column = "YEARLY FUND CHARGES")
    private String yearlyFundCharges;

    @CsvBindByName(column = "POLICY FEE")
    private String policyFee;

    @CsvBindByName(column = "LOYALTY BONUS DISCOUNT")
    private String loyaltyBonusDiscount;

    @CsvBindByName(column = "DEATH VALUE")
    private String deathValue;

    // Investment Details
    @CsvBindByName(column = "FUND NAME")
    private String fundName;

    @CsvBindByName(column = "TOTAL_UNIT")
    private String totalUnits;

    @CsvBindByName(column = "UNIT_PRICE")
    private String unitPrice;

    @CsvBindByName(column = "PRICE_DATE")
    private String priceDate;

    // Dates
    @CsvBindByName(column = "LIFESTYLE TARGET DATE")
    private String lifestyleTargetDate;

    @CsvBindByName(column = "WAIVER END")
    private String waiverEndDate;

    @CsvBindByName(column = "ACCIDENTAL DEATH DATE")
    private String accidentalDeathDate;

    // Additional Variables
    @CsvBindByName(column = "VARIABLE1")
    private String variable1;

    @CsvBindByName(column = "VARIABLE2")
    private String variable2;

    @CsvBindByName(column = "VARIABLE3")
    private String variable3;

    @CsvBindByName(column = "VARIABLE4")
    private String variable4;

    @CsvBindByName(column = "VARIABLE5")
    private String variable5;

    // Validation and Recommendations
    @CsvBindByName(column = "VALIDATION_ERRORS_WARNINGS")
    private String validationErrorsWarnings;

    @CsvBindByName(column = "RECOMMENDATIONS")
    private String recommendations;

    @CsvBindByName(column = "CONFIDENCE_SCORE")
    private Integer confidenceScore;

    @CsvBindByName(column = "RAG_Trigger")
    private String ragTrigger;

    // Helper Methods
    public String getFullName() {
        return (firstName != null ? firstName + " " : "") + (surname != null ? surname : "");
    }

    public String getFullAddress() {
        return String.format("%s %s, %s, %s, %s, %s",
                houseNumber != null ? houseNumber : "",
                street != null ? street : "",
                postTown != null ? postTown : "",
                county != null ? county : "",
                country != null ? country : "",
                postcode != null ? postcode : "");
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public Integer getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(Integer customerAge) {
        this.customerAge = customerAge;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public void setLetterTitle(String letterTitle) {
        this.letterTitle = letterTitle;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostTown() {
        return postTown;
    }

    public void setPostTown(String postTown) {
        this.postTown = postTown;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getSchemeNumber() {
        return schemeNumber;
    }

    public void setSchemeNumber(String schemeNumber) {
        this.schemeNumber = schemeNumber;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getBancsProductCode() {
        return bancsProductCode;
    }

    public void setBancsProductCode(String bancsProductCode) {
        this.bancsProductCode = bancsProductCode;
    }

    public String getLegacyClassCode() {
        return legacyClassCode;
    }

    public void setLegacyClassCode(String legacyClassCode) {
        this.legacyClassCode = legacyClassCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNormalRetirementDate() {
        return normalRetirementDate;
    }

    public void setNormalRetirementDate(String normalRetirementDate) {
        this.normalRetirementDate = normalRetirementDate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPaymentsIn() {
        return paymentsIn;
    }

    public void setPaymentsIn(String paymentsIn) {
        this.paymentsIn = paymentsIn;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String getPolicyValue2025() {
        return policyValue2025;
    }

    public void setPolicyValue2025(String policyValue2025) {
        this.policyValue2025 = policyValue2025;
    }

    public String getPolicyValue2024() {
        return policyValue2024;
    }

    public void setPolicyValue2024(String policyValue2024) {
        this.policyValue2024 = policyValue2024;
    }

    public String getFutureValueAtNRD() {
        return futureValueAtNRD;
    }

    public void setFutureValueAtNRD(String futureValueAtNRD) {
        this.futureValueAtNRD = futureValueAtNRD;
    }

    public String getAnnualTaxableIncome() {
        return annualTaxableIncome;
    }

    public void setAnnualTaxableIncome(String annualTaxableIncome) {
        this.annualTaxableIncome = annualTaxableIncome;
    }

    public String getYearlyFundCharges() {
        return yearlyFundCharges;
    }

    public void setYearlyFundCharges(String yearlyFundCharges) {
        this.yearlyFundCharges = yearlyFundCharges;
    }

    public String getPolicyFee() {
        return policyFee;
    }

    public void setPolicyFee(String policyFee) {
        this.policyFee = policyFee;
    }

    public String getLoyaltyBonusDiscount() {
        return loyaltyBonusDiscount;
    }

    public void setLoyaltyBonusDiscount(String loyaltyBonusDiscount) {
        this.loyaltyBonusDiscount = loyaltyBonusDiscount;
    }

    public String getDeathValue() {
        return deathValue;
    }

    public void setDeathValue(String deathValue) {
        this.deathValue = deathValue;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(String totalUnits) {
        this.totalUnits = totalUnits;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public String getLifestyleTargetDate() {
        return lifestyleTargetDate;
    }

    public void setLifestyleTargetDate(String lifestyleTargetDate) {
        this.lifestyleTargetDate = lifestyleTargetDate;
    }

    public String getWaiverEndDate() {
        return waiverEndDate;
    }

    public void setWaiverEndDate(String waiverEndDate) {
        this.waiverEndDate = waiverEndDate;
    }

    public String getAccidentalDeathDate() {
        return accidentalDeathDate;
    }

    public void setAccidentalDeathDate(String accidentalDeathDate) {
        this.accidentalDeathDate = accidentalDeathDate;
    }

    public String getVariable1() {
        return variable1;
    }

    public void setVariable1(String variable1) {
        this.variable1 = variable1;
    }

    public String getVariable2() {
        return variable2;
    }

    public void setVariable2(String variable2) {
        this.variable2 = variable2;
    }

    public String getVariable3() {
        return variable3;
    }

    public void setVariable3(String variable3) {
        this.variable3 = variable3;
    }

    public String getVariable4() {
        return variable4;
    }

    public void setVariable4(String variable4) {
        this.variable4 = variable4;
    }

    public String getVariable5() {
        return variable5;
    }

    public void setVariable5(String variable5) {
        this.variable5 = variable5;
    }

    public String getValidationErrorsWarnings() {
        return validationErrorsWarnings;
    }

    public void setValidationErrorsWarnings(String validationErrorsWarnings) {
        this.validationErrorsWarnings = validationErrorsWarnings;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getRagTrigger() {
        return ragTrigger;
    }

    public void setRagTrigger(String ragTrigger) {
        this.ragTrigger = ragTrigger;
    }

    @Override
    public String toString() {
        return "PensionData{" +
                "firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", productName='" + productName + '\'' +
                ", policyValue2025='" + policyValue2025 + '\'' +
                ", fundName='" + fundName + '\'' +
                '}';
    }
}