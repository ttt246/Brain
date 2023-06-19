"""chat response model"""
from typing import Any

from src.model.message_model import MessageModel


class ChatResponseModel:
    def __init__(self, data: Any):
        self.id = data["id"]
        self.object = data["object"]
        self.model = data["model"]
        self.usage = data["usage"]
        """'choices': [
            {
                'message': {
                    'role': 'assistant',
                    'content': 'The 2020 World Series was played in Arlington, Texas at the Globe Life Field, which was the new home stadium for the Texas Rangers.'},
                'finish_reason': 'stop',
                'index': 0
            }
        ]"""
        self.choices = []
        for choice in data["choices"]:
            self.choices.append(ChoiceModel(choice))

    def get_one_message_item(self) -> MessageModel:
        if len(self.choices) > 0:
            return self.choices.__getitem__(0).get_message()
        else:
            return MessageModel("", "")


class ChoiceModel:
    def __init__(self, data: Any):
        tmp_message = data["message"]
        self.message = MessageModel(tmp_message["role"], tmp_message["content"])
        self.finish_reason = data["finish_reason"]
        self.index = data["index"]

    def get_message(self):
        return self.message
