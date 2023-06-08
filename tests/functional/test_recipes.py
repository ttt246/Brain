import re

from rising_plugin.risingplugin import getCompletion, getTextFromImage
from src.firebase.cloudmessage import send_message
from src.firebase.cloudmessage import get_tokens
from rising_plugin.image_embedding import query_image_text

TEST_IAMGE_NAME = "0ddffe51-3763-48d9-ab74-2086de529217"
TEST_UUID = "TEST_UUID"


def test_langchain():
    error = "Error happened while analyzing your prompt. Please ask me again"
    result = getCompletion(query="open three.js website", uuid=TEST_UUID)
    print(result)
    assert result != error


def test_image2text():
    error = "Error happened while analyzing your prompt. Please ask me again"
    result = getTextFromImage(TEST_IAMGE_NAME)
    print(result)
    assert result != error


def test_firebase_cloud_message():
    token_list = get_tokens()
    notification = {"title": "alert", "content": "test"}

    pattern = r"send to \d+ devices, with \d+ successed, with \d+ failed."

    state, value = send_message(notification, token_list)
    assert re.match(pattern, value)


def test_query_image_text():
    error = "Error happened in querying image & text"
    result = query_image_text(getTextFromImage(TEST_IAMGE_NAME), "", TEST_UUID)
    print(result)
    assert result != error
