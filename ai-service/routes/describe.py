from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from datetime import datetime
from services.logger_config import logger
from services.error_handler import bad_request, server_error
from services.validator import validate_incident
import json

describe_bp = Blueprint("describe", __name__)

@describe_bp.route("/api/v1/describe", methods=["POST"])
def describe():

    data = request.get_json()

    print(data)

    validation_error = validate_incident(data)

    if validation_error:
        return bad_request(validation_error)

    incident = data["incident"].strip()

    logger.info(
        f"Describe endpoint called with incident: {incident}"
    )

    try:
        with open("./prompts/describe_prompt.txt", "r") as f:
            template = f.read()

    except Exception:
        return server_error("Prompt file not found")

    prompt = template.replace("{incident}", incident)

    ai_response = call_groq(prompt)

    try:
        parsed_response = json.loads(ai_response)

    except Exception:
        parsed_response = {
            "summary": ai_response,
            "key_issue": "Unable to parse AI response",
            "impact": "Unknown"
        }

    return jsonify({
        "data": parsed_response,
        "generated_at": datetime.utcnow().isoformat(),
        "status": "success"
    })