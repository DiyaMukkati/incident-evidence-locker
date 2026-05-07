import os
from groq import Groq
from dotenv import load_dotenv

load_dotenv(dotenv_path=".env")

api_key = os.getenv("GROQ_API_KEY")

print("API KEY LOADED:", api_key)

client = Groq(api_key=api_key)

def call_groq(prompt):

    try:
        response = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.3,
            max_tokens=300
        )

        return response.choices[0].message.content

    except Exception as e:

        print("GROQ ERROR:", str(e))

        return '''
        {
            "summary": "AI unavailable",
            "key_issue": "Exception",
            "impact": "Unknown"
        }
        '''