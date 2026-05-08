from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.logger_config import logger
from services.error_handler import bad_request, server_error
from services.validator import validate_incident
import json
import os

report_bp = Blueprint("report", __name__)

@report_bp.route("/api/v1/generate-report", methods=["POST"])
def generate_report():

    data = request.get_json()

    validation_error = validate_incident(data)

    if validation_error:
        return bad_request(validation_error)

    incident = data["incident"].strip()

    logger.info(
        f"Report endpoint called with incident: {incident}"
    )

    try:

        BASE_DIR = os.path.dirname(
            os.path.dirname(os.path.abspath(__file__))
        )

        prompt_path = os.path.join(
            BASE_DIR,
            "prompts",
            "report_prompt.txt"
        )

        with open(prompt_path, "r") as f:
            template = f.read()

    except Exception:
        return server_error("Prompt file not found")

    prompt = template.replace("{incident}", incident)

    ai_response = call_groq(prompt)

    try:
        parsed_response = json.loads(ai_response)

    except Exception:
        parsed_response = {
            "title": "Fallback Report",
            "summary": ai_response,
            "overview": "Unable to parse AI response",
            "recommendations": []
        }

    return jsonify({
        "report": parsed_response,
        "status": "success"
    })