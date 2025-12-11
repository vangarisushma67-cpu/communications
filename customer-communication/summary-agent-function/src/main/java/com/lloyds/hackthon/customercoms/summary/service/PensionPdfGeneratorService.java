package com.lloyds.hackthon.customercoms.summary.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.lloyds.hackthon.customercoms.summary.model.PensionData;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

public class PensionPdfGeneratorService {
    private static final Logger logger = Logger.getLogger(PensionPdfGeneratorService.class.getName());
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.UK);
    
    /**
     * Generates a pension statement PDF for the given pension data
     */
    public byte[] generatePensionStatementPdf(PensionData pensionData) {
        try {
            logger.info("Generating pension statement PDF for customer: " + pensionData.getFullName());
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Add header
            addHeader(document, pensionData);
            
            // Add customer information
            addCustomerInformation(document, pensionData);
            
            // Add account summary based on product type
            addAccountSummary(document, pensionData);
            
            // Add projections
            addProjections(document, pensionData);
            
            // Add footer
            addFooter(document);
            
            document.close();
            
            logger.info("PDF generated successfully for customer: " + pensionData.getFullName());
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.severe("Error generating PDF for customer " + pensionData.getFullName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to generate pension statement PDF", e);
        }
    }
    
    private void addHeader(Document document, PensionData pensionData) {
        // Company logo and header
        Paragraph header = new Paragraph("LLOYDS BANKING GROUP")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);
        
        // Statement title based on product type
        String statementTitle = getStatementTitle(pensionData.getProductType());
        Paragraph title = new Paragraph(statementTitle)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);
        
        // Statement date
        Paragraph date = new Paragraph("Statement Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
        document.add(date);
    }
    
    private void addCustomerInformation(Document document, PensionData pensionData) {
        Paragraph sectionTitle = new Paragraph("Customer Information")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(sectionTitle);
        
        Table customerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .setWidth(UnitValue.createPercentValue(100));
        
        customerTable.addCell(createCell("Customer Name:", true));
        customerTable.addCell(createCell(pensionData.getFullName(), false));

        document.add(customerTable);
        document.add(new Paragraph("\n"));
    }
    
    private void addAccountSummary(Document document, PensionData pensionData) {
        Paragraph sectionTitle = new Paragraph("Policy Summary")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(sectionTitle);
        
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));
        
        summaryTable.addCell(createCell("Policy Number:", true));
        summaryTable.addCell(createCell(pensionData.getPolicyNumber(), false));

        summaryTable.addCell(createCell("Product Type:", true));
        summaryTable.addCell(createCell(pensionData.getProductType(), false));

        summaryTable.addCell(createCell("Total Units:", true));
        summaryTable.addCell(createCell(pensionData.getTotalUnits(), false));

        summaryTable.addCell(createCell("Unit Price:", true));
        summaryTable.addCell(createCell(pensionData.getUnitPrice(), false));
        
        summaryTable.addCell(createCell("Policy Value 2025:", true));
        summaryTable.addCell(createCell(pensionData.getPolicyValue2025(), false));

        summaryTable.addCell(createCell("Policy Value 2024:", true));
        summaryTable.addCell(createCell(pensionData.getPolicyValue2024(), false));
        
        document.add(summaryTable);
        document.add(new Paragraph("\n"));
    }
    
    private void addProjections(Document document, PensionData pensionData) {
        Paragraph sectionTitle = new Paragraph("Retirement Projections")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
        document.add(sectionTitle);
        
        Table projectionTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100));
        
        projectionTable.addCell(createCell("Retirement Date:", true));
        projectionTable.addCell(createCell(pensionData.getNormalRetirementDate(), false));
        
        projectionTable.addCell(createHighlightCell("Projected Retirement Value"));
        projectionTable.addCell(createHighlightCell(formatCurrency(pensionData.getFutureValueAtNRD())));

        projectionTable.addCell(createHighlightCell("RAG Trigger"));
        projectionTable.addCell(createHighlightCell(formatCurrency(pensionData.getRagTrigger())));
        
        document.add(projectionTable);
        document.add(new Paragraph("\n"));
        
        // Add disclaimer
        Paragraph disclaimer = new Paragraph("Important: These projections are estimates based on current contributions and market assumptions. " +
                "Actual results may vary depending on market performance, contribution changes, and other factors.")
                .setFontSize(8)
                .setItalic()
                .setMarginTop(20);
        document.add(disclaimer);
    }
    
    private void addProductSpecificInformation(Document document, PensionData pensionData) {
        String productType = pensionData.getProductType();
        
        if (productType == null) return;
        
        switch (productType.toLowerCase()) {
            case "workplace pension":
                addWorkplacePensionInfo(document, pensionData);
                break;
            case "personal pension":
                addPersonalPensionInfo(document, pensionData);
                break;
            case "sipp":
                addSippInfo(document, pensionData);
                break;
            case "stakeholder pension":
                addStakeholderPensionInfo(document, pensionData);
                break;
            default:
                addGenericPensionInfo(document, pensionData);
                break;
        }
    }
    
    private void addWorkplacePensionInfo(Document document, PensionData pensionData) {
        Paragraph info = new Paragraph("Workplace Pension Benefits:")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
        document.add(info);
        
        List benefits = new List()
                .setMarginLeft(20);
        benefits.add(new ListItem("Employer contributions matched to your contributions"));
        benefits.add(new ListItem("Tax relief on contributions"));
        benefits.add(new ListItem("Professional fund management"));
        benefits.add(new ListItem("Automatic enrollment benefits"));
        
        document.add(benefits);
        document.add(new Paragraph("\n"));
    }
    
    private void addPersonalPensionInfo(Document document, PensionData pensionData) {
        Paragraph info = new Paragraph("Personal Pension Features:")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
        document.add(info);
        
        List features = new List()
                .setMarginLeft(20);
        features.add(new ListItem("Flexible contribution options"));
        features.add(new ListItem("Wide range of investment choices"));
        features.add(new ListItem("Tax-efficient savings"));
        features.add(new ListItem("Portable between employers"));
        
        document.add(features);
        document.add(new Paragraph("\n"));
    }
    
    private void addSippInfo(Document document, PensionData pensionData) {
        Paragraph info = new Paragraph("SIPP (Self-Invested Personal Pension) Features:")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
        document.add(info);
        
        List features = new List()
                .setMarginLeft(20);
        features.add(new ListItem("Greater investment control and flexibility"));
        features.add(new ListItem("Access to wider range of investments"));
        features.add(new ListItem("Ability to invest in commercial property"));
        features.add(new ListItem("Professional investment management available"));
        
        document.add(features);
        document.add(new Paragraph("\n"));
    }
    
    private void addStakeholderPensionInfo(Document document, PensionData pensionData) {
        Paragraph info = new Paragraph("Stakeholder Pension Benefits:")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
        document.add(info);
        
        List benefits = new List()
                .setMarginLeft(20);
        benefits.add(new ListItem("Low charges capped at 1.5% annually"));
        benefits.add(new ListItem("Flexible contributions from £20"));
        benefits.add(new ListItem("No penalties for stopping or changing contributions"));
        benefits.add(new ListItem("Default investment strategy available"));
        
        document.add(benefits);
        document.add(new Paragraph("\n"));
    }
    
    private void addGenericPensionInfo(Document document, PensionData pensionData) {
        Paragraph info = new Paragraph("Pension Benefits:")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
        document.add(info);
        
        List benefits = new List()
                .setMarginLeft(20);
        benefits.add(new ListItem("Tax-efficient retirement savings"));
        benefits.add(new ListItem("Professional investment management"));
        benefits.add(new ListItem("Flexible retirement options"));
        benefits.add(new ListItem("Death benefit protection"));
        
        document.add(benefits);
        document.add(new Paragraph("\n"));
    }
    
    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("Lloyds Banking Group plc. Registered Office: 25 Gresham Street, London EC2V 7HN. " +
                "Registered in England and Wales No. 95000. Telephone: 0345 300 0000")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30);
        document.add(footer);
    }
    
    private Cell createCell(String content, boolean bold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (bold) {
            cell.setBold();
        }
        cell.setPadding(5);
        return cell;
    }
    
    private Cell createHighlightCell(String content) {
        Cell cell = new Cell().add(new Paragraph(content));
        cell.setBold();
        cell.setBackgroundColor(new DeviceRgb(240, 248, 255));
        cell.setPadding(8);
        return cell;
    }
    
    private String formatCurrency(String amount) {
        try {
            if (amount == null || amount.trim().isEmpty()) {
                return "£0.00";
            }
            
            // Remove any existing currency symbols and parse
            String cleanAmount = amount.replaceAll("[£,]", "").trim();
            double value = Double.parseDouble(cleanAmount);
            return CURRENCY_FORMAT.format(value);
        } catch (NumberFormatException e) {
            logger.warning("Could not format currency: " + amount);
            return amount;
        }
    }
    
    private String getStatementTitle(String productType) {
        if (productType == null) {
            return "Pension Statement";
        }
        
        switch (productType.toLowerCase()) {
            case "workplace pension":
                return "Workplace Pension Statement";
            case "personal pension":
                return "Personal Pension Statement";
            case "sipp":
                return "Self-Invested Personal Pension (SIPP) Statement";
            case "stakeholder pension":
                return "Stakeholder Pension Statement";
            default:
                return "Pension Statement";
        }
    }
}