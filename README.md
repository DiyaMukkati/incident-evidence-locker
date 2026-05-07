# Incident Evidence Locker – AI Service

This AI microservice is developed as part of the Incident Evidence Locker project using Flask and Groq API. The service processes incident descriptions and generates structured AI-based responses for analysis, recommendations, and report generation.

## Features

- Incident description analysis
- AI-generated recommendations
- Detailed incident report generation
- Health monitoring endpoint
- Structured JSON responses

## API Endpoints

### 1. Describe Incident

**POST** `/describe`

#### Request
```json
{
  "incident": "A mobile phone was stolen near the bus stand at night."
}