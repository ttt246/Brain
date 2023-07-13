"""Image model to process & handle them"""
from typing import Any
from Brain.src.model.requests.request_model import UploadImage
from Brain.src.model.basic_model import DataStatus, ImageTypes


class ImageModel:
    def __init__(self):
        self.image_text = ""
        self.image_name = ""
        self.uuid = ""
        self.status = DataStatus.CREATED
        self.type = ImageTypes.APP

    def to_json(self) -> Any:
        return {
            "image_name": self.image_name,
            "image_text": self.image_text,
            "type": self.type,
        }

    def get_image_model(self, data: UploadImage.ImageReq) -> None:
        self.image_name = data.image_name
        self.image_text = data.image_content
        self.type = data.type
        self.status = data.status
