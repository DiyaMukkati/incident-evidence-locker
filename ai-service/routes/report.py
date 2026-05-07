from flask import Blueprint, request, jsonify
from services.groq_client import call_groq
import json
import os

report_bp = Blueprint("report", __name__)

@report_bp.route("/generate-report", methods=["POST"])
def generate_report():

    data = request.get_json()

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
        BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

        prompt_path = os.path.join(
            BASE_DIR,
            "prompts",
            "report_prompt.txt"
        )

        with open(prompt_path, "r") as f:
            template = f.read()

    except Exception as e:
        return jsonify({
            "error": "Prompt file not found",
            "details": str(e)
        }), 500

    prompt = template.replace("{incident}", incident)

    ai_response = call_groq(prompt)

    try:
        parsed_response = json.loads(ai_response)

    except Exception:
        parsed_response = {
            "title": "Fallback Report",
            "summary": ai_response,
            "overview": "Parsing failed",
            "recommendations": []
        }

    return jsonify({
        "report": parsed_response,
        "status": "success"
    })