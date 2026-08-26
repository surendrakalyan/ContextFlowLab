from dotenv import load_dotenv
from groq import Groq
import os

load_dotenv()

api_key = os.getenv("GROQ_API_KEY")

if not api_key:
    print("GROQ_API_KEY not found")
    exit()

client = Groq(api_key=api_key)

models = client.models.list()

print("\nModels available to your API key:\n")

for model in models.data:
    if getattr(model, "active", True):
        print(model.id)