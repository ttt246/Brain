from fastapi.testclient import TestClient
import pytest

from app import app

client = TestClient(app)


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "confs": {
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                    "token": "eSyP3i7ITZuq8hWn2qutTl:APA91bH1FtWkaTSJwuX4WKWAl3Q-ZFyrOw4UtMP4IfwuvNrHOThH7EvEGIhtguilLRyQNlLiXatEN0xntHAc8bbKobSGjge3wxIHlspbIWY_855CzONqaVdl3y3zOmgKZNnuhYi4gwbh",
                    "uuid": "c40a09075d11940f",
                },
                "message": "Please search an image that shows Brown Teddy Bear",
            }
        )
    ],
)
def test_send_notificatoin(body):
    response = client.post("/sendNotification", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "image_name": "0ddffe51-3763-48d9-ab74-2086de529217",
                "confs": {
                    "token": "eSyP3i7ITZuq8hWn2qutTl:APA91bH1FtWkaTSJwuX4WKWAl3Q-ZFyrOw4UtMP4IfwuvNrHOThH7EvEGIhtguilLRyQNlLiXatEN0xntHAc8bbKobSGjge3wxIHlspbIWY_855CzONqaVdl3y3zOmgKZNnuhYi4gwbh",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
                "status": "updated",
            }
        )
    ],
)
def test_upload_image(body):
    response = client.post("/uploadImage", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "image_name": "0ddffe51-3763-48d9-ab74-2086de529217",
                "message": "This is the text about the image",
                "confs": {
                    "token": "eSyP3i7ITZuq8hWn2qutTl:APA91bH1FtWkaTSJwuX4WKWAl3Q-ZFyrOw4UtMP4IfwuvNrHOThH7EvEGIhtguilLRyQNlLiXatEN0xntHAc8bbKobSGjge3wxIHlspbIWY_855CzONqaVdl3y3zOmgKZNnuhYi4gwbh",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_image_relatedness(body):
    response = client.post("/image_relatedness", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "prompt": {"image_name": "test_image", "message": "test_message"},
                "completion": {"image_name": "test_image", "message": "test_message"},
                "rating": 1,
                "confs": {
                    "token": "eSyP3i7ITZuq8hWn2qutTl:APA91bH1FtWkaTSJwuX4WKWAl3Q-ZFyrOw4UtMP4IfwuvNrHOThH7EvEGIhtguilLRyQNlLiXatEN0xntHAc8bbKobSGjge3wxIHlspbIWY_855CzONqaVdl3y3zOmgKZNnuhYi4gwbh",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_feedback(body):
    response = client.post("/feedback", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "confs": {
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                }
            }
        )
    ],
)
def test_feedback(body):
    response = client.post("/feedback/test/1", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "confs": {
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                }
            }
        )
    ],
)
def test_commands(body):
    response = client.post("/commands", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "history": [
                    {"role": "system", "content": "You are a helpful assistant."},
                    {"role": "user", "content": "Who won the world series in 2020?"},
                    {
                        "role": "assistant",
                        "content": "The Los Angeles Dodgers won the World Series in 2020.",
                    },
                ],
                "user_input": "Where was it played?",
                "confs": {
                    "token": "test_token",
                    "uuid": "test_uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_chat_rising(body):
    response = client.post("/chat_rising", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "contacts": [
                    {
                        "contactId": "1",
                        "displayName": "Thomas",
                        "phoneNumbers": ["217 374 8105"],
                        "status": "updated",
                    }
                ],
                "confs": {
                    "token": "test_token",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_train_contacts(body):
    response = client.post("/train/contacts", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "data": {
                    "reference_link": "test link",
                },
                "confs": {
                    "token": "test_token",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_delete_data(body):
    response = client.post("/auto_task/delete", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "data": {
                    "sender": "test@gmail.com",
                    "pwd": "password",
                    "imap_folder": "inbox",
                },
                "confs": {
                    "token": "test_token",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_read_emails(body):
    response = client.post("/email/read_emails", json=body)
    assert response.status_code == 200


@pytest.mark.parametrize(
    "body",
    [
        (
            {
                "data": {
                    "sender": "testsender@gmail.com",
                    "pwd": "use app password of your google account",
                    "to": "testto@gmail.com",
                    "subject": "Test Send",
                    "body": "Hi, This is test email.",
                    "to_send": True,
                    "filename": "test.txt",
                    "file_content": "SGVsbG8sIFdvcmxkIQ==",
                },
                "confs": {
                    "token": "test_token",
                    "uuid": "test-uuid",
                    "openai_key": "",
                    "pinecone_key": "",
                    "pinecone_env": "",
                    "firebase_key": "",
                    "settings": {"temperature": 0.6},
                },
            }
        )
    ],
)
def test_send_email(body):
    response = client.post("/email/send_email", json=body)
    assert response.status_code == 200
