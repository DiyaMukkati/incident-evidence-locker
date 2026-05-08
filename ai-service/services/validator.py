def validate_incident(data):

    if not data:
        return "Request body is missing"

    if "incident" not in data:
        return "Incident field is required"

    incident = data["incident"]

    if not isinstance(incident, str):
        return "Incident must be a string"

    if not incident.strip():
        return "Incident cannot be empty"

    return None