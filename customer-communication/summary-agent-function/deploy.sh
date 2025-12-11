#!/bin/bash

# Pension Summary Agent Function Deployment Script
# This script deploys the enhanced summary agent function to Google Cloud

set -e

echo "🚀 Starting deployment of Pension Summary Agent Function..."

# Configuration
PROJECT_ID=${GOOGLE_CLOUD_PROJECT_ID:-"your-project-id"}
FUNCTION_NAME="pension-summary-agent"
REGION=${GOOGLE_CLOUD_REGION:-"europe-west2"}
MEMORY=${FUNCTION_MEMORY:-"512MB"}
TIMEOUT=${FUNCTION_TIMEOUT:-"540s"}
RUNTIME="java17"

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    echo "❌ Error: gcloud CLI is not installed. Please install it first."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven is not installed. Please install it first."
    exit 1
fi

echo "📦 Building the function..."
mvn clean package -DskipTests

echo "☁️ Deploying to Google Cloud Functions..."
gcloud functions deploy $FUNCTION_NAME \
    --gen2 \
    --runtime=$RUNTIME \
    --region=$REGION \
    --source=. \
    --entry-point=com.lloyds.hackthon.customercoms.summary.function.SummaryFunction \
    --memory=$MEMORY \
    --timeout=$TIMEOUT \
    --trigger-http \
    --allow-unauthenticated \
    --set-env-vars="GOOGLE_CLOUD_PROJECT_ID=$PROJECT_ID" \
    --project=$PROJECT_ID

echo "🔧 Setting up IAM permissions..."
# Grant the function access to Cloud Storage
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:$PROJECT_ID@appspot.gserviceaccount.com" \
    --role="roles/storage.objectAdmin"

echo "📋 Function deployment completed!"
echo ""
echo "Function Details:"
echo "  Name: $FUNCTION_NAME"
echo "  Region: $REGION"
echo "  Runtime: $RUNTIME"
echo "  Memory: $MEMORY"
echo "  Timeout: $TIMEOUT"
echo ""
echo "Function URL:"
gcloud functions describe $FUNCTION_NAME --region=$REGION --format="value(serviceConfig.uri)"
echo ""
echo "📝 Test the function with:"
echo "curl -X POST \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{}' \\"
echo "  \$(gcloud functions describe $FUNCTION_NAME --region=$REGION --format=\"value(serviceConfig.uri)\")"
echo ""
echo "✅ Deployment completed successfully!"