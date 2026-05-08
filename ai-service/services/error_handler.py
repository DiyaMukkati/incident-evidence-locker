from flask import jsonify

def bad_request(message):

    return jsonify({
        "status": "error",
        "message": message
    }), 400


def server_error(message):

    return jsonify({
        "status": "error",
        "message": message
    }), 500