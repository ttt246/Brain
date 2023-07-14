import re

from Brain.src.common.utils import (
    OPENAI_API_KEY,
    PINECONE_KEY,
    PINECONE_ENV,
    FIREBASE_ENV,
)
from Brain.src.firebase.cloudmessage import CloudMessage
from Brain.src.firebase.firebase import initialize_app
from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.risingplugin import getCompletion, getTextFromImage
from Brain.src.rising_plugin.image_embedding import query_image_text

TEST_IAMGE_NAME = "0ddffe51-3763-48d9-ab74-2086de529217"
TEST_UUID = "TEST_UUID"
test_setting = ReqModel(
    data={
        "token": "test-token",
        "uuid": TEST_UUID,
        "openai_key": OPENAI_API_KEY,
        "pinecone_key": PINECONE_KEY,
        "pinecone_env": PINECONE_ENV,
        "firebase_key": FIREBASE_ENV,
        "settings": {"temperature": 0.6},
    }
)

firebase_app = initialize_app(test_setting)


def test_langchain():
    error = "Error happened while analyzing your prompt. Please ask me again"
    result = getCompletion(
        query="open three.js website", setting=test_setting, firebase_app=firebase_app
    )
    print(result)
    assert result != error


def test_image2text():
    error = "Error happened while analyzing your prompt. Please ask me again"
    result = getTextFromImage(filename=TEST_IAMGE_NAME, firebase_app=firebase_app)
    print(result)
    assert result != error


def test_firebase_cloud_message():
    cloud_message = CloudMessage(firebase_app=firebase_app)
    token_list = cloud_message.get_tokens()
    notification = {"title": "alert", "content": "test"}

    pattern = r"send to \d+ devices, with \d+ successed, with \d+ failed."

    state, value = cloud_message.send_message(notification, token_list)
    assert re.match(pattern, value)


def test_query_image_text():
    error = "Error happened in querying image & text"
    result = query_image_text(
        image_content=getTextFromImage(
            filename=TEST_IAMGE_NAME, firebase_app=firebase_app
        ),
        message="",
        setting=test_setting,
    )
    print(result)
    assert result != error
