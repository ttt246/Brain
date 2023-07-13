"""Image model to process & handle them"""
from typing import Any
from Brain.src.model.requests.request_model import UploadImage
from Brain.src.model.basic_model import DataStatus, ImageTypes
from Brain.src.common.utils import write_file


class ImageModel:
    def __init__(self):
        self.image_text = ""
        self.image_name = ""
        self.uuid = ""
        self.status = DataStatus.CREATED
        self.type = ImageTypes.APP
        self.image_path = ""
        self.base_directory = ""

    def to_json(self) -> Any:
        return {
            "image_name": self.image_name,
            "image_text": self.image_text,
            "type": self.type,
            "image_path": self.image_path,
        }

    def get_image_model(self, data: UploadImage.ImageReq) -> None:
        # write image content to image file
        file_path, file_directory = write_file(
            filename=data.image_name, file_content=data.image_content
        )

        self.image_name = data.image_name
        self.type = data.type
        self.status = data.status
        self.image_path = file_path
        self.base_directory = file_directory
