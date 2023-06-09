import requests
import json
from flask import Flask, request, render_template
from flask_cors import CORS
from flask_swagger_ui import get_swaggerui_blueprint
from src.common.utils import swagger_destination_path, SWAGGER_URL, API_URL
from src.firebase.firebase import initialize_app

initialize_app()

from src.router.api import construct_blueprint_api


def create_app():
    app = Flask(__name__)
    CORS(app)
    app.register_blueprint(construct_blueprint_api())
    return app


app = create_app()


@app.route("/")
def index():
    return render_template("index.html")


@app.route("/send-post", methods=["POST"])
def send_post():
    data = json.loads(request.get_data())
    url = "http://127.0.0.1:7860/sendNotification"
    response = requests.post(url, json=data)
    return response.text


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=7860)
