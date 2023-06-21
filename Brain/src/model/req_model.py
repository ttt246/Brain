from typing import Any

DEFAULT_HOST_NAME = "test3-83ffc.appspot.com"


class ReqModel:
    class Settings:
        def __init__(self):
            self.temperature: float = 0.6

    host_name: str
    openai_key: str
    pinecone_key: str
    pinecone_env: str
    firebase_key: str
    token: str = ""
    uuid: str = ""
    settings: Settings

    def __init__(self, data: dict):
        self.host_name = (
            DEFAULT_HOST_NAME if data["host_name"] == "" else data["host_name"]
        )
        self.openai_key = (
            DEFAULT_HOST_NAME if data["openai_key"] == "" else data["openai_key"]
        )
        self.pinecone_key = (
            DEFAULT_HOST_NAME if data["pinecone_key"] == "" else data["pinecone_key"]
        )
        self.pinecone_env = (
            DEFAULT_HOST_NAME if data["pinecone_env"] == "" else data["pinecone_env"]
        )
        self.firebase_key = (
            DEFAULT_HOST_NAME if data["firebase_key"] == "" else data["firebase_key"]
        )
        self.token = data["token"]
        self.uuid = data["uuid"]
        self.settings = self.Settings()
        self.settings.temperature = data["settings"]["temperature"]

    def to_json(self):
        return {
            "host_name": self.host_name,
            "openai_key": self.openai_key,
            "pinecone_key": self.pinecone_key,
            "pinecone_env": self.pinecone_env,
            "firebase_key": self.firebase_key,
            "settings": {"temperature": self.settings.temperature},
            "token": self.token,
            "uuid": self.uuid,
        }
