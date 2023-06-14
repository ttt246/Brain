import json
import os

from firebase_admin import credentials

# env variables
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
PINECONE_KEY = os.getenv("PINECONE_KEY")
PINECONE_ENV = os.getenv("PINECONE_ENV")
FIREBASE_ENV = os.getenv("FIREBASE_SERVICE_ACCOUNT_TEST3_83FFC")

# swagger
swagger_destination_path = "./src/static/swagger.json"
SWAGGER_URL = "/api/docs"  # URL for exposing Swagger UI (without trailing '/')
API_URL = "http://localhost:5000/file/swagger.json"

# firebase
FIREBASE_STORAGE_ROOT = "images/"
FIREBASE_STORAGE_BUCKET = "test3-83ffc.appspot.com"

# pinecone
PINECONE_NAMESPACE = "risinglangchain-namespace"
PINECONE_INDEX_NAME = "risinglangchain-index"

# open ai
GPT_MODEL = "gpt-3.5-turbo"

# AI Agent name
AGENT_NAME = "RisingBrain Assistant"

# indexes of relatedness of embedding
COMMAND_SMS_INDEXS = [4, 5]
COMMAND_BROWSER_OPEN = [10]

# Twilio
ACCOUNT_SID = os.getenv("TWILIO_ACCOUNT_SID")
AUTH_TOKEN = os.getenv("TWILIO_AUTH_TOKEN")


def get_firebase_cred():
    if os.path.exists("firebase_cred.json"):
        file = open("firebase_cred.json")
        cred = json.load(file)
        file.close()
        return credentials.Certificate(cred)
    else:
        cred = json.loads(FIREBASE_ENV)
        return credentials.Certificate(cred)


class ProgramType:
    BROWSER = "browser"
    ALERT = "alert"
    IMAGE = "image"
    SMS = "sms"
    CONTACT = "contact"
    MESSAGE = "message"


# validate json format
def validateJSON(jsonData):
    try:
        json.loads(jsonData)
    except ValueError as err:
        return False
    return True


def parseJsonFromCompletion(data: str) -> json:
    result = data[1:-1]
    # fmt: off
    result = result.replace("{'", '{"')
    result = result.replace("'}", '"}')
    result = result.replace("': '", '": "')
    result = result.replace("': \\\"", '": \"')
    result = result.replace("', '", '", "')

    substring = '\\"}'
    replacement = '\"}'

    index = result.rfind(substring)

    if index == len(result) - 3:
        result = result[:index] + replacement + result[index + len(substring):]
    # fmt: on
    result = json.loads(result.replace("':", '":'))
    return result
