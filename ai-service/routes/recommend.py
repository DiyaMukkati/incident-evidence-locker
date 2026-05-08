from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from services.logger_config import logger
from services.error_handler import bad_request, server_error
import json

recommend_bp = Blueprint("recommend", __name__)

@recommend_bp.route("/recommend", methods=["POST"])
def recommend():

    data = request.get_json()

    if not data or "incident" not in data:
        return jsonify({
            "error": "Incident input is required"
        }), 400

    incident = data["incident"].strip()
    logger.info(f"Recommend endpoint called with incident: {incident}")

    if not incident:
        return jsonify({
            "error": "Incident cannot be empty"
        }), 400

    try:
       with open("./prompts/recommend_prompt.txt", "r") as f:
            template = f.read()

    except Exception:
        return jsonify({
            "error": "Prompt file not found"
        }), 500

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