from typing import List

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.risingplugin import getTextFromImage
from Brain.src.rising_plugin.image_embedding import embed_image_text

from Brain.src.model.image_model import ImageModel
from Brain.src.model.basic_model import ImageTypes

import firebase_admin
from firebase_admin import storage

"""
    Usage:
    service to manage images
"""


class ImagesService:
    def __init__(self, firebase_app: firebase_admin.App, setting: ReqModel):
        self.firebase_app = firebase_app
        self.setting = setting

    """train images (getting embedding) and update pinecone with embeddings by image_text"""

    def train(self, uuid: str, images: List[ImageModel]) -> None:
        for image in images:
            self.save_image(image=image, firebase_app=self.firebase_app)
            image.uuid = uuid
            image.image_text = getTextFromImage(
                filename=image.image_name, firebase_app=self.firebase_app
            )
            # train image and update pinecone
            embed_result = embed_image_text(image=image, setting=self.setting)
            print(embed_result)

    """create a image into app/photos which name is uuid in storage of firebase"""

    def save_image(self, image: ImageModel, firebase_app: firebase_admin.App) -> None:
        image_folder = "app/{}"
        if image.type == ImageTypes.PHOTOS:
            image_folder = "photos/{}"

        bucket = storage.bucket(app=firebase_app)
        blob = bucket.blob(image_folder.format(image.image_path))
        blob.upload_from_filename(image.image_path)
