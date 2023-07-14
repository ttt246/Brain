import base64
from typing import Any

from Brain.src.common.utils import (
    OPENAI_API_KEY,
    PINECONE_KEY,
    PINECONE_ENV,
    FIREBASE_ENV,
)


class ReqModel:
    class Settings:
        def __init__(self):
            self.temperature: float = 0.6

    openai_key: str
    pinecone_key: str
    pinecone_env: str
    firebase_key: str
    token: str = ""
    uuid: str = ""
    settings: Settings

    def __init__(self, data: dict):
        self.openai_key = (
            OPENAI_API_KEY if data["openai_key"] == "" else data["openai_key"]
        )
        self.pinecone_key = (
            PINECONE_KEY if data["pinecone_key"] == "" else data["pinecone_key"]
        )
        self.pinecone_env = (
            PINECONE_ENV if data["pinecone_env"] == "" else data["pinecone_env"]
        )
        self.firebase_key = self.decode_firebase_key(data["firebase_key"])
        self.token = data["token"]
        self.uuid = data["uuid"]
        self.settings = self.Settings()
        self.settings.temperature = data["settings"]["temperature"]

    def to_json(self):
        return {
            "openai_key": self.openai_key,
            "pinecone_key": self.pinecone_key,
            "pinecone_env": self.pinecone_env,
            "firebase_key": self.firebase_key,
            "settings": {"temperature": self.settings.temperature},
            "token": self.token,
            "uuid": self.uuid,
        }

    def decode_firebase_key(self, data: str) -> Any:
        decoded_data = base64.b64decode(data)

        # Printing the decoded string
        return decoded_data.decode("utf-8")
