# Pension Summary Agent Function

This Google Cloud Function processes pension data from Google Cloud Storage and generates personalized PDF statements for each customer based on their product type.

## Overview

The function:
1. Downloads pension data from the `pension-data-summary` bucket (`Pension_Data_With_RAG.csv`)
2. Parses the CSV data into structured pension records
3. Generates customized PDF statements for each customer based on their product type
4. Uploads the generated PDFs to the `pension-data-communication` bucket with naming format: `{customer_name}_summary.pdf`

## Supported Pension Product Types

- **Workplace Pension**: Employer-sponsored pension with matching contributions
- **Personal Pension**: Individual pension with flexible contribution options
- **SIPP (Self-Invested Personal Pension)**: Greater investment control and flexibility
- **Stakeholder Pension**: Low-cost pension with capped charges
- **Generic Pension**: Default template for other pension types

## API Endpoints

### Batch Processing (Default)
```http
POST /
Content-Type: application/json

{
  "sourceBucket": "pension-data-summary",
  "sourceFile": "Pension_Data_With_RAG.csv",
  "destinationBucket": "pension-data-communication"
}
```

### Single Customer Processing
```http
POST /
Content-Type: application/json

{
  "customerData": {
    "customer_name": "John Smith",
    "customer_id": "CUST001",
    "product_type": "Workplace Pension",
    "account_number": "ACC123456",
    "current_value": "50000",
    "annual_contribution": "5000",
    "employer_contribution": "2500",
    "investment_return": "7.5",
    "projected_retirement_value": "250000",
    "retirement_age": "65",
    "risk_profile": "Moderate",
    "fund_allocation": "Balanced Portfolio"
  }
}
```

## Response Format

### Success Response
```json
{
  "status": "success",
  "message": "Pension summary generation completed",
  "totalRecords": 20,
  "successfulPdfs": 20,
  "failedPdfs": 0,
  "destinationBucket": "pension-data-communication",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## CSV Data Format

The input CSV file should contain the following columns:

| Column | Description |
|--------|-------------|
| customer_name | Customer's full name |
| customer_id | Unique customer identifier |
| product_type | Type of pension product |
| account_number | Pension account number |
| current_value | Current pension value (£) |
| annual_contribution | Annual contribution amount (£) |
| employer_contribution | Employer contribution amount (£) |
| investment_return | Investment return percentage |
| projected_retirement_value | Projected value at retirement (£) |
| retirement_age | Target retirement age |
| risk_profile | Investment risk profile |
| fund_allocation | Fund allocation strategy |

## PDF Statement Features

Each generated PDF includes:

- **Header**: Company branding and statement title
- **Customer Information**: Personal details and account information
- **Account Summary**: Current value, contributions, and returns
- **Investment Details**: Risk profile and fund allocation
- **Product-Specific Information**: Tailored content based on pension type
- **Retirement Projections**: Estimated retirement value and timeline
- **Footer**: Company registration and contact information

## Configuration

Environment variables and configuration options:

```properties
# Google Cloud Configuration
google.cloud.project.id=${GOOGLE_CLOUD_PROJECT_ID}
google.cloud.storage.source.bucket=pension-data-summary
google.cloud.storage.source.file=Pension_Data_With_RAG.csv
google.cloud.storage.destination.bucket=pension-data-communication

# PDF Generation Configuration
pdf.company.name=Lloyds Banking Group
pdf.company.address=25 Gresham Street, London EC2V 7HN
pdf.company.phone=0345 300 0000
pdf.company.registration=Registered in England and Wales No. 95000
```

## Dependencies

- Google Cloud Functions Framework
- Google Cloud Storage Client
- iText PDF (for PDF generation)
- OpenCSV (for CSV parsing)
- Gson (for JSON processing)

## Deployment

1. Ensure Google Cloud credentials are configured
2. Set up the required Google Cloud Storage buckets
3. Deploy using the Google Cloud Functions Maven plugin:

```bash
mvn function:deploy
```

## Error Handling

The function includes comprehensive error handling for:
- Missing or corrupted CSV files
- Invalid pension data
- PDF generation failures
- Google Cloud Storage upload errors
- Network connectivity issues

## Logging

All operations are logged with appropriate levels:
- INFO: Successful operations and progress updates
- WARNING: Non-critical issues and fallbacks
- SEVERE: Critical errors and failures

## Security

- Uses Google Cloud IAM for authentication
- Validates input data before processing
- Sanitizes file names to prevent security issues
- Implements proper error handling to prevent information leakage