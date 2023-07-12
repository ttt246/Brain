import json
import os
import re


# env variables
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
PINECONE_KEY = os.getenv("PINECONE_KEY")
PINECONE_ENV = os.getenv("PINECONE_ENV")
FIREBASE_ENV = os.getenv("FIREBASE_SERVICE_ACCOUNT_TEST3_83FFC")

# proxy for imap access from hugging face.
PROXY_IP = os.getenv("PROXY_IP")
PROXY_PORT = os.getenv("PROXY_PORT")

# swagger
swagger_destination_path = "./src/static/swagger.json"
SWAGGER_URL = "/api/docs"  # URL for exposing Swagger UI (without trailing '/')
API_URL = "http://localhost:5000/file/swagger.json"

# firebase
FIREBASE_STORAGE_ROOT = "images/"
FIREBASE_STORAGE_BUCKET = "test3-83ffc.appspot.com"
FIREBASE_REALTIME_DATABASE = "https://test3-83ffc-default-rtdb.firebaseio.com/"

# pinecone
PINECONE_NAMESPACE = "risinglangchain-namespace"
PINECONE_INDEX_NAME = "risinglangchain-index"

# open ai
DEFAULT_GPT_MODEL = "gpt-4"

# AI Agent name
AGENT_NAME = "RisingBrain Assistant"

# indexes of relatedness of embedding
COMMAND_SMS_INDEXES = ["pWDrks5DO1bEPLlUtQ1f", "LEpAhmFi8tAOQUE7LHZZ"]  # 4, 5
COMMAND_BROWSER_OPEN = ["taVNeDINonUqJWXBlESU"]  # 10

# Twilio
ACCOUNT_SID = os.getenv("TWILIO_ACCOUNT_SID")
AUTH_TOKEN = os.getenv("TWILIO_AUTH_TOKEN")
# HuggingFace
HUGGINGFACEHUB_API_TOKEN = os.getenv("HUGGINGFACEHUB_API_TOKEN")


# actions flag :True -> Directly with Category_prompt
ACTION_FLAG = True


class ProgramType:
    BROWSER = "browser"
    ALERT = "alert"
    IMAGE = "image"
    SMS = "sms"
    CONTACT = "contact"
    MESSAGE = "message"
    AUTO_TASK = "autotask"
    RAILS_OFF_TOPIC = "rails_off_topic"


# validate json format
def validateJSON(jsonData):
    try:
        json.loads(jsonData)
    except ValueError as err:
        return False
    return True


def parseJsonFromCompletion(data: str) -> json:
    result = data[1:-1]
    array = result.split('\\"')
    # fmt: off
    if len(array) > 1:
        array[0] = array[0].replace("'", '"')
        result = array[0] + '"' + array[1] + '"' + array[2]
    else:
        result = result.replace("{'", '{"')
        result = result.replace("'}", '"}')
        result = result.replace("': '", '": "')
        result = result.replace("': \\\"", '": \"')
        result = result.replace("', '", '", "')
        result = result.replace("':", '":')

        substring = '\\"}'
        replacement = '\"}'

        index = result.rfind(substring)

        if index == len(result) - 3:
            result = result[:index] + replacement + result[index + len(substring):]
    # fmt: on
    try:
        return json.loads(result)
    except Exception as e:
        return result


def parseUrlFromStr(text: str) -> str:
    # Search for the link using regex
    link = re.search(r"(https?://\S+)", text)

    if link:
        return link.group(0)
    else:
        return ""
