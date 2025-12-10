# Customer Communication System - Project Overview

## 📁 Project Structure

```
customer-communication/
├── pom.xml                                    # Parent Maven POM
├── README.md                                  # Project documentation
├── .gitignore                                 # Git ignore rules
├── PROJECT_OVERVIEW.md                        # This file
│
├── validation-agent-function/                 # Data validation module
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/lloyds/hackthon/customercoms/validation/
│       │   ├── function/ValidationFunction.java    # HTTP Function entry point
│       │   ├── agent/ValidationAgent.java          # Business logic orchestrator
│       │   └── tool/ValidationTool.java            # LLM integration utilities
│       └── resources/
│           ├── application.properties               # Configuration
│           └── validation-prompt.txt               # LLM prompt template
│
├── rules-agent-function/                      # Business rules module
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/lloyds/hackthon/customercoms/rules/
│       │   ├── function/RulesFunction.java         # HTTP Function entry point
│       │   ├── agent/RulesAgent.java               # Business logic orchestrator
│       │   └── tool/RulesTool.java                 # LLM integration utilities
│       └── resources/
│           ├── application.properties               # Configuration
│           └── rules-prompt.txt                    # LLM prompt template
│
├── summary-agent-function/                    # Content summarization module
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/lloyds/hackthon/customercoms/summary/
│       │   ├── function/SummaryFunction.java       # HTTP Function entry point
│       │   ├── agent/SummaryAgent.java             # Business logic orchestrator
│       │   └── tool/SummaryTool.java               # LLM integration utilities
│       └── resources/
│           ├── application.properties               # Configuration
│           └── summary-prompt.txt                  # LLM prompt template
│
└── communication-agent-function/              # Communication generation module
    ├── pom.xml
    └── src/main/
        ├── java/com/lloyds/hackthon/customercoms/communication/
        │   ├── function/CommunicationFunction.java # HTTP Function entry point
        │   ├── agent/CommunicationAgent.java       # Business logic orchestrator
        │   └── tool/CommunicationTool.java         # LLM integration utilities
        └── resources/
            ├── application.properties               # Configuration
            └── communication-prompt.txt            # LLM prompt template
```

## 🎯 Module Purposes

### 1. Validation Agent Function
- **Purpose**: Validates customer data and communication requests
- **Features**: Data completeness, format validation, compliance checks
- **HTTP Endpoint**: Accepts validation requests and returns validation results

### 2. Rules Agent Function
- **Purpose**: Applies business rules and policies to customer communications
- **Features**: Business logic enforcement, compliance rules, risk assessment
- **HTTP Endpoint**: Processes rule evaluation requests

### 3. Summary Agent Function
- **Purpose**: Creates concise summaries of customer communications
- **Features**: Content analysis, key points extraction, sentiment analysis
- **HTTP Endpoint**: Generates summaries from provided content

### 4. Communication Agent Function
- **Purpose**: Generates appropriate customer communications
- **Features**: Multi-channel support, tone management, personalization
- **HTTP Endpoint**: Creates communications based on context and requirements

## 🔧 Technical Stack

- **Framework**: Google Cloud Functions (HTTP Functions)
- **Language**: Java 11
- **Build Tool**: Maven 3.9.9
- **Dependencies**:
  - Google Cloud Functions Framework 1.1.0
  - Google Cloud AI Platform 26.1.4
  - JUnit Jupiter 5.9.2

## 📦 Package Structure

Each module follows the same package structure:
```
com.lloyds.hackthon.customercoms.{module}.{layer}
```

Where:
- `{module}` = validation | rules | summary | communication
- `{layer}` = function | agent | tool

### Layer Responsibilities:
- **Function Layer**: HTTP function entry points, request/response handling
- **Agent Layer**: Business logic orchestration, configuration management
- **Tool Layer**: LLM integration, utility functions, external service calls

## ⚙️ Configuration

Each module includes:
- `application.properties`: OpenAI API configuration and module-specific settings
- `{module}-prompt.txt`: LLM prompt templates for each agent

### Required Environment Variables:
```bash
export OPENAI_API_KEY=your-openai-api-key-here
```

## 🚀 Build & Deploy

### Build All Modules:
```bash
mvn clean compile
mvn clean package
```

### Deploy Individual Function:
```bash
cd {module-name}
mvn function:deploy
```

### Local Development:
```bash
cd {module-name}
mvn function:run
```

## 📡 API Usage

Each function exposes an HTTP endpoint that accepts POST requests:

```bash
# Example: Validation Agent
curl -X POST https://your-function-url/validation \
  -H "Content-Type: application/json" \
  -d '{"data": "customer data to validate"}'

# Example: Rules Agent
curl -X POST https://your-function-url/rules \
  -H "Content-Type: application/json" \
  -d '{"request": "communication request"}'

# Example: Summary Agent
curl -X POST https://your-function-url/summary \
  -H "Content-Type: application/json" \
  -d '{"content": "content to summarize"}'

# Example: Communication Agent
curl -X POST https://your-function-url/communication \
  -H "Content-Type: application/json" \
  -d '{"context": "communication context"}'
```

## 🔄 Development Workflow

1. **Setup**: Configure OpenAI API key in environment
2. **Build**: Use Maven to compile and package modules
3. **Test**: Deploy functions locally for testing
4. **Deploy**: Deploy to Google Cloud Functions
5. **Monitor**: Use Google Cloud Console for monitoring and logs

## 📝 Next Steps

1. Implement actual OpenAI API integration in Tool classes
2. Add comprehensive error handling and validation
3. Implement unit and integration tests
4. Add monitoring and logging enhancements
5. Configure CI/CD pipeline for automated deployment
6. Add authentication and authorization mechanisms
7. Implement rate limiting and quota management

## 🔐 Security Considerations

- Store API keys securely using Google Secret Manager
- Implement proper authentication for function endpoints
- Add input validation and sanitization
- Configure appropriate IAM roles and permissions
- Enable audit logging for compliance requirements