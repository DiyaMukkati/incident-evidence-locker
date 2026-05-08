from flask import Blueprint, jsonify
from datetime import datetime

health_bp = Blueprint("health", __name__)

@health_bp.route("/api/v1/health", methods=["GET"])
def health():

    return jsonify({
        "status": "healthy",
        "service": "AI Incident Service",
        "timestamp": datetime.utcnow().isoformat()
    })