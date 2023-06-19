import datetime


# feedback model to manage user's feedback (thumb up and down)
from src.model.basic_model import BasicModel


class FeedbackModel:
    def __init__(
        self, uuid: str, prompt: BasicModel, completion: BasicModel, rating: int
    ):
        self.uuid = uuid
        self.prompt = prompt
        self.completion = completion
        self.rating = rating
        self.timestamp = datetime.datetime.now().timestamp()

    def to_json(self):
        return {
            "uuid": self.uuid,
            "prompt": self.prompt.to_json(),
            "completion": self.completion.to_json(),
            "rating": self.rating,
            "timestamp": self.timestamp,
        }
