from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.logger_config import logger
from services.error_handler import bad_request, server_error
from services.validator import validate_incident
import json

recommend_bp = Blueprint("recommend", __name__)

@recommend_bp.route("/api/v1/recommend", methods=["POST"])
def recommend():

    data = request.get_json()

    validation_error = validate_incident(data)

    if validation_error:
        return bad_request(validation_error)

    incident = data["incident"].strip()

    logger.info(
        f"Recommend endpoint called with incident: {incident}"
    )

    try:
        with open("./prompts/recommend_prompt.txt", "r") as f:
            template = f.read()

    except Exception:
        return server_error("Prompt file not found")

    prompt = template.replace("{incident}", incident)

    ai_response = call_groq(prompt)

    try:
        parsed_response = json.loads(ai_response)

    except Exception:
        parsed_response = [
            {
                "action_type": "Fallback",
                "description": ai_response,
                "priority": "Medium"
            }
        ]

    return jsonify({
        "recommendations": parsed_response,
        "status": "success"
    })