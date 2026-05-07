from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
from datetime import datetime
import json

describe_bp = Blueprint("describe", __name__)

@describe_bp.route("/describe", methods=["POST"])
def describe():

    data = request.get_json()

    print(data)

    if not data or "incident" not in data:
        return jsonify({
            "error": "Incident input is required"
        }), 400

    incident = data["incident"].strip()

    if not incident:
        return jsonify({
            "error": "Incident cannot be empty"
        }), 400

    try:
        with open("prompts/describe_prompt.txt", "r") as f:
            template = f.read()

    except Exception as e:
        return jsonify({
            "error": "Prompt file not found"
        }), 500

    prompt = template.replace("{incident}", incident)

    ai_response = call_groq(prompt)

    try:
        parsed_response = json.loads(ai_response)

    except Exception:
        parsed_response = {
            "summary": ai_response,
            "key_issue": "Parsing failed",
            "impact": "Unknown"
        }

    return jsonify({
        "data": parsed_response,
        "generated_at": datetime.utcnow().isoformat(),
        "status": "success"
    })