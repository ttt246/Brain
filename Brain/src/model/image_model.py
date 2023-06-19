"""Image model to process & handle them"""
from typing import Any

from Brain.src.model.basic_model import DataStatus


class ImageModel:
    def __init__(self):
        self.image_text = ""
        self.image_name = ""
        self.uuid = ""
        self.status = DataStatus.CREATED

    def to_json(self) -> Any:
        return {"image_name": self.image_name, "image_text": self.image_text}
