# Customer Communication System

A Maven multi-module project for customer communication processing using Google Cloud Functions and OpenAI LLM integration.

## Project Structure

```
customer-communication/
├── validation-agent-function/     # Validates customer communication data
├── rules-agent-function/          # Applies business rules and compliance checks
├── summary-agent-function/        # Generates summaries of communications
└── communication-agent-function/  # Generates customer communications
```

## Modules

### 1. Validation Agent Function
- **Purpose**: Validates incoming customer data and communication requests
- **Package**: `com.lloyds.hackthon.customercoms.validation`
- **Function Target**: `ValidationFunction`
- **Features**: Data completeness, format validation, compliance checks

### 2. Rules Agent Function
- **Purpose**: Applies business rules and policies to customer communications
- **Package**: `com.lloyds.hackthon.customercoms.rules`
- **Function Target**: `RulesFunction`
- **Features**: Business logic, compliance rules, risk assessment

### 3. Summary Agent Function
- **Purpose**: Creates concise summaries of customer communications
- **Package**: `com.lloyds.hackthon.customercoms.summary`
- **Function Target**: `SummaryFunction`
- **Features**: Content analysis, key points extraction, sentiment analysis

### 4. Communication Agent Function
- **Purpose**: Generates appropriate customer communications
- **Package**: `com.lloyds.hackthon.customercoms.communication`
- **Function Target**: `CommunicationFunction`
- **Features**: Multi-channel support, tone management, personalization

## Dependencies

- **Google Cloud Functions Framework**: 1.1.0
- **Google Cloud AI Platform**: 26.1.4
- **JUnit Jupiter**: 5.9.2
- **Java**: 11

## Configuration

Each module includes:
- `application.properties`: OpenAI API configuration and module-specific settings
- `{module}-prompt.txt`: LLM prompt templates for each agent

### Environment Variables

Set the following environment variable:
```bash
export OPENAI_API_KEY=your-openai-api-key-here
```

## Building the Project

```bash
# Build all modules
mvn clean compile

# Package all modules
mvn clean package

# Deploy individual function (example for validation-agent)
cd validation-agent-function
mvn function:deploy
```

## Local Development

```bash
# Run a specific function locally (example for validation-agent)
cd validation-agent-function
mvn function:run
```

## Package Structure

Each module follows the same package structure:
- `function/`: HTTP function entry points
- `agent/`: Business logic and orchestration
- `tool/`: LLM integration and utility classes

## API Endpoints

Each function exposes an HTTP endpoint that accepts POST requests with JSON payloads:

- **Validation Agent**: Validates input data and returns validation results
- **Rules Agent**: Applies business rules and returns compliance status
- **Summary Agent**: Generates summaries and returns structured output
- **Communication Agent**: Creates communications and returns formatted content

## Example Usage

```bash
# Validation Agent
curl -X POST https://your-function-url/validation \
  -H "Content-Type: application/json" \
  -d '{"data": "customer data to validate"}'

# Rules Agent
curl -X POST https://your-function-url/rules \
  -H "Content-Type: application/json" \
  -d '{"request": "communication request"}'

# Summary Agent
curl -X POST https://your-function-url/summary \
  -H "Content-Type: application/json" \
  -d '{"content": "content to summarize"}'

# Communication Agent
curl -X POST https://your-function-url/communication \
  -H "Content-Type: application/json" \
  -d '{"context": "communication context"}'
```

## Development Notes

- All functions are configured as HTTP functions
- OpenAI integration is implemented using Google Cloud AI Platform
- Each module includes placeholder implementations that can be extended
- Configuration is externalized through properties files
- Comprehensive logging is implemented throughout