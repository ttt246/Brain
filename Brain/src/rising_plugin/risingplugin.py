import os
import json
import datetime

import firebase_admin
import openai
import replicate
import textwrap

from typing import Any

from langchain import LLMChain
from langchain.chains.question_answering import load_qa_chain
from nemoguardrails.rails import LLMRails, RailsConfig

from langchain.chat_models import ChatOpenAI

from firebase_admin import storage

from .csv_embed import get_embed
from .llm.llms import get_llm, GPT_4, FALCON_7B
from .pinecone_engine import init_pinecone
from ..common.brain_exception import BrainException
from ..common.utils import (
    OPENAI_API_KEY,
    FIREBASE_STORAGE_ROOT,
    DEFAULT_GPT_MODEL,
    parseJsonFromCompletion,
    PINECONE_INDEX_NAME,
)
from .image_embedding import (
    query_image_text,
    get_prompt_image_with_message,
)
from ..model.req_model import ReqModel
from ..model.requests.request_model import BasicReq
from ..service.train_service import TrainService

# Give the path to the folder containing the rails
file_path = os.path.dirname(os.path.abspath(__file__))
config = RailsConfig.from_path(f"{file_path}/guardrails-config")

# set max_chunk_size = 1800 because of adding some string
max_chunk_size = 1800  # recommended max_chunk_size = 2048


def getChunks(query: str):
    return textwrap.wrap(
        query, width=max_chunk_size, break_long_words=False, replace_whitespace=False
    )


def llm_rails(
    setting: ReqModel,
    rails_app: any,
    firebase_app: firebase_admin.App,
    query: str,
    image_search: bool = True,
) -> Any:
    """step 0: convert string to json"""
    index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=setting)
    train_service = TrainService(firebase_app=firebase_app, setting=setting)

    """step 1: handle with gpt-4"""

    query_result = get_embed(query)
    relatedness_data = index.query(
        vector=query_result,
        top_k=1,
        include_values=False,
        namespace=train_service.get_pinecone_index_train_namespace(),
    )

    if len(relatedness_data["matches"]) == 0:
        return str({"program": "message", "content": ""})
    document_id = relatedness_data["matches"][0]["id"]

    document = train_service.read_one_document(document_id)
    page_content = document["page_content"]

    message = rails_app.generate(
        messages=[
            {
                "role": "user",
                "content": rails_input_with_args(
                    setting=setting,
                    query=query,
                    image_search=image_search,
                    page_content=page_content,
                    document_id=document_id,
                ),
            }
        ]
    )
    return message


def processLargeText(
    setting: ReqModel,
    app: any,
    chunks: any,
    firebase_app: firebase_admin.App,
    image_search: bool = True,
):
    if len(chunks) == 1:
        message = llm_rails(
            setting=setting,
            rails_app=app,
            firebase_app=firebase_app,
            query=chunks[0],
            image_search=image_search,
        )
        result = json.dumps(message["content"])
        return parseJsonFromCompletion(result)
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
                llm_rails(
                    setting=setting,
                    rails_app=app,
                    firebase_app=firebase_app,
                    query=chunk_query,
                    image_search=image_search,
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
                message = llm_rails(
                    setting=setting,
                    rails_app=app,
                    firebase_app=firebase_app,
                    query=last_query,
                    image_search=image_search,
                )
                result = json.dumps(message["content"])
                return parseJsonFromCompletion(result)
        # out of for-loop


def getCompletion(
    query: str,
    setting: ReqModel,
    firebase_app: firebase_admin.App,
    image_search: bool = True,
):
    llm = get_llm(model=DEFAULT_GPT_MODEL, setting=setting).get_llm()

    # Break input text into chunks
    chunks = getChunks(query)

    app = LLMRails(config, llm)

    return processLargeText(
        setting=setting,
        app=app,
        chunks=chunks,
        image_search=image_search,
        firebase_app=firebase_app,
    )


def getCompletionOnly(
    query: str,
    model: str = "gpt-4",
) -> str:
    llm = ChatOpenAI(model_name=model, temperature=0.5, openai_api_key=OPENAI_API_KEY)
    chain = load_qa_chain(llm, chain_type="stuff")
    chain_data = chain.run(input_documents=[], question=query)
    return chain_data


def query_image_ask(image_content, message, setting: ReqModel):
    prompt_template = get_prompt_image_with_message(image_content, message)
    try:
        data = getCompletion(query=prompt_template, image_search=False, setting=setting)
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
def filter_guardrails(setting: ReqModel, query: str):
    llm = ChatOpenAI(
        model_name=DEFAULT_GPT_MODEL, temperature=0, openai_api_key=setting.openai_key
    )
    app = LLMRails(config, llm)

    # split query with chunks
    chunks = getChunks(query)

    # get message from guardrails
    message = processLargeText(app=app, chunks=chunks, setting=setting)

    if (
        message
        == "Sorry, I cannot comment on anything which is relevant to the password or pin code."
        or message
        == "I am an Rising AI assistant which helps answer questions based on a given knowledge base."
    ):
        return message
    else:
        return ""


"""
compose json_string for rails input with its arguments 
"""


def rails_input_with_args(
    setting: ReqModel,
    query: str,
    image_search: bool,
    page_content: str,
    document_id: str,
) -> str:
    # convert json with params for rails.
    json_query_with_params = {
        "query": query,
        "image_search": image_search,
        "page_content": page_content,
        "document_id": document_id,
        "setting": setting.to_json(),
    }
    return json.dumps(json_query_with_params)


def handle_chat_completion(messages: Any, model: str = "gpt-3.5-turbo") -> Any:
    openai.api_key = OPENAI_API_KEY

    response = openai.ChatCompletion.create(
        model=model,
        messages=messages,
    )

    # Filter the reply using the content filter
    # result = filter_guardrails(model, messages[-1]["content"])
    # comment logic issue with guardrails
    # if result == "":
    #     return response
    # else:
    #     response["choices"][0]["message"]["content"] = result
    #     return response
    return response
