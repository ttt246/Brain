import os
import json
import datetime
import openai
import replicate
import textwrap

from typing import Any

from nemoguardrails.rails import LLMRails, RailsConfig

from langchain.chat_models import ChatOpenAI

from firebase_admin import storage

from ..common.utils import (
    OPENAI_API_KEY,
    FIREBASE_STORAGE_ROOT,
)
from .image_embedding import (
    query_image_text,
    get_prompt_image_with_message,
)

# Give the path to the folder containing the rails
file_path = os.path.dirname(os.path.abspath(__file__))
config = RailsConfig.from_path(f"{file_path}/guardrails-config")

# set max_chunk_size = 1800 because of adding some string
max_chunk_size = 1800  # recommended max_chunk_size = 2048


def getChunks(query: str):
    return textwrap.wrap(
        query, width=max_chunk_size, break_long_words=False, replace_whitespace=False
    )


def processLargeText(app: any, chunks: any):
    if len(chunks) == 1:
        message = app.generate(
            messages=[
                {
                    "role": "user",
                    "content": chunks[0],
                }
            ]
        )
        try:
            response_text = json.loads(message["content"])["content"]
        except Exception as e:
            # fmt: off
            # message["content"] = message["content"].replace("'", '"')
            message["content"] = message["content"].replace("{'program': 'message', 'content'",
                                                            '{"program": "message", "content"')
            message["content"] = message["content"].replace(message["content"][34], '"')
            message["content"] = message["content"].replace(message["content"][-2], '"')
            # fmt: on
            response_text = json.loads(message["content"])["content"]
        return response_text
    else:
        first_query = "The total length of the content that I want to send you is too large to send in only one piece.\nFor sending you that content, I will follow this rule:\n[START PART 1/10]\nThis is the content of the part 1 out of 10 in total\n[END PART 1/10]\nThen you just answer: 'Received part 1/10'\nAnd when I tell you 'ALL PART SENT', then you can continue processing the data and answering my requests."
        app.generate(messages=[{"role": "user", "content": first_query}])
        for index, chunk in enumerate(chunks):
            # Process each chunk with ChatGPT
            if index + 1 != len(chunks):
                chunk_query = (
                    "Do not answer yet. This is just another part of the text I want to send you. Just receive and acknowledge as 'Part "
                    + str(index + 1)
                    + "/"
                    + str(len(chunks))
                    + "received' and wait for the next part.\n"
                    + "[START PART "
                    + str(index + 1)
                    + "/"
                    + str(len(chunks))
                    + "]\n"
                    + chunk
                    + "\n[END PART "
                    + str(index + 1)
                    + "/"
                    + str(len(chunks))
                    + "]\n"
                    + "Remember not answering yet. Just acknowledge you received this part with the message 'Part 1/10 received' and wait for the next part."
                )
                app.generate(
                    messages=[
                        {
                            "role": "user",
                            "content": chunk_query,
                        }
                    ]
                )
            else:
                last_query = (
                    "[START PART "
                    + str(index + 1)
                    + "/"
                    + str(len(chunks))
                    + chunk
                    + "\n[END PART "
                    + str(index + 1)
                    + "/"
                    + str(len(chunks))
                    + "]\n"
                    + "ALL PART SENT. Now you can continue processing the request."
                )
                message = app.generate(
                    messages=[{"role": "user", "content": last_query}]
                )
                try:
                    response_text = json.loads(message["content"])["content"]
                except Exception as e:
                    # fmt: off
                    # message["content"] = message["content"].replace("'", '"')
                    message["content"] = message["content"].replace("{'program': 'message', 'content'",
                                                                    '{"program": "message", "content"')
                    message["content"] = message["content"].replace(message["content"][34], '"')
                    message["content"] = message["content"].replace(message["content"][-2], '"')
                    # fmt: on
                    response_text = json.loads(message["content"])["content"]
                program = json.loads(message["content"])["program"]
                return {"program": program, "content": response_text}
        # out of for-loop


def getCompletion(
    query,
    model="gpt-3.5-turbo",
    uuid="",
    image_search=True,
):
    llm = ChatOpenAI(model_name=model, temperature=0, openai_api_key=OPENAI_API_KEY)

    # Break input text into chunks
    chunks = getChunks(query)

    app = LLMRails(config, llm)
    return processLargeText(app, chunks)


def query_image_ask(image_content, message, uuid):
    prompt_template = get_prompt_image_with_message(image_content, message)
    try:
        data = getCompletion(query=prompt_template, uuid=uuid, image_search=False)
        # chain_data = json.loads(data.replace("'", '"'))
        # chain_data = json.loads(data)
        if data["program"] == "image":
            return True
    except Exception as e:
        return False
    return False


def getTextFromImage(filename):
    # Create a reference to the image file you want to download
    bucket = storage.bucket()
    blob = bucket.blob(FIREBASE_STORAGE_ROOT.__add__(filename))
    download_url = ""

    try:
        # Download the image to a local file
        download_url = blob.generate_signed_url(
            datetime.timedelta(seconds=300), method="GET", version="v4"
        )

        output = replicate.run(
            "salesforce/blip:2e1dddc8621f72155f24cf2e0adbde548458d3cab9f00c0139eea840d0ac4746",
            input={"image": download_url},
        )

    except Exception as e:
        output = str("Error happend while analyzing your prompt. Please ask me again :")

    return str(output)


"""chat with ai
response: 
{
 'id': 'chatcmpl-6p9XYPYSTTRi0xEviKjjilqrWU2Ve',
 'object': 'chat.completion',
 'created': 1677649420,
 'model': 'gpt-3.5-turbo',
 'usage': {'prompt_tokens': 56, 'completion_tokens': 31, 'total_tokens': 87},
 'choices': [
   {
    'message': {
      'role': 'assistant',
      'content': 'The 2020 World Series was played in Arlington, Texas at the Globe Life Field, which was the new home stadium for the Texas Rangers.'},
    'finish_reason': 'stop',
    'index': 0
   }
  ]
}
"""


# Define a content filter function
def filter_guardrails(model: any, query: str):
    llm = ChatOpenAI(model_name=model, temperature=0, openai_api_key=OPENAI_API_KEY)
    app = LLMRails(config, llm)

    # split query with chunks
    chunks = getChunks(query)

    # get message from guardrails
    message = processLargeText(app, chunks)

    if (
        message
        == "Sorry, I cannot comment on anything which is relevant to the password or pin code."
        or message
        == "I am an Rising AI assistant which helps answer questions based on a given knowledge base."
    ):
        return message
    else:
        return ""


def handle_chat_completion(messages: Any, model: str = "gpt-3.5-turbo") -> Any:
    openai.api_key = OPENAI_API_KEY

    response = openai.ChatCompletion.create(
        model=model,
        messages=messages,
    )

    # Filter the reply using the content filter
    result = filter_guardrails(model, messages[-1]["content"])

    if result == "":
        return response
    else:
        response["choices"][0]["message"]["content"] = result
        return response
    # return response
