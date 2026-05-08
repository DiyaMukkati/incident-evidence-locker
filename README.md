# Incident Evidence Locker – AI Service

This AI microservice is developed as part of the Incident Evidence Locker project using Flask and Groq API. The service processes incident descriptions and generates structured AI-based responses for analysis, recommendations, and report generation.

---

# Features

- Incident description analysis
- AI-generated recommendations
- Detailed incident report generation
- Health monitoring endpoint
- Request logging and tracking
- Security headers using Flask-Talisman
- API rate limiting protection
- Reusable request validation
- Docker deployment support

---

# Technologies Used

- Python
- Flask
- Groq API
- Flask-Talisman
- Flask-Limiter
- Prompt Engineering
- OWASP ZAP
- Docker

---

# Project Structure

```bash
ai-service/
│
├── prompts/
│   ├── describe_prompt.txt
│   ├── recommend_prompt.txt
│   └── report_prompt.txt
│
├── routes/
│   ├── describe.py
│   ├── recommend.py
│   ├── report.py
│   └── health.py
│
├── services/
│   ├── groq_client.py
│   ├── logger_config.py
│   ├── validator.py
│   └── error_handler.py
│
├── Dockerfile
├── requirements.txt
├── .env.example
├── README.md
└── app.py