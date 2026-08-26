from flask import Flask, request, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
from groq import Groq
import os

load_dotenv()

app = Flask(__name__)
CORS(app)

api_key = os.getenv("GROQ_API_KEY")

if not api_key:
    raise ValueError("GROQ_API_KEY not found in .env")

client = Groq(api_key=api_key)

conversation_history = []


@app.route("/", methods=["GET"])
def home():
    return jsonify({
        "status": "success",
        "message": "Context Flow Lab Groq backend is running"
    })


@app.route("/chat", methods=["POST"])
def chat():

    global conversation_history

    data = request.get_json()

    if not data or "message" not in data:
        return jsonify({
            "error": "Message is required"
        }), 400

    user_message = data["message"].strip()

    if not user_message:
        return jsonify({
            "error": "Message cannot be empty"
        }), 400

    conversation_history.append({
        "role": "user",
        "content": user_message
    })

    try:

        response = client.chat.completions.create(
            model="openai/gpt-oss-120b",
            messages=conversation_history,
            temperature=0.7
        )

        ai_response = response.choices[0].message.content

        conversation_history.append({
            "role": "assistant",
            "content": ai_response
        })

        return jsonify({
            "response": ai_response
        })

    except Exception as e:

        conversation_history.pop()

        return jsonify({
            "error": str(e)
        }), 500


@app.route("/clear", methods=["POST"])
def clear_conversation():

    global conversation_history

    conversation_history = []

    return jsonify({
        "message": "Conversation cleared"
    })


if __name__ == "__main__":
    app.run(
        host="0.0.0.0",
        port=5000,
        debug=True
    )