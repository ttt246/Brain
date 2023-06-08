# Basic model for querying including prompt and completion both
class BasicModel:
    def __init__(self, image_name: str, message: str):
        self.image_name = image_name
        self.message = message

    def to_json(self):
        return {"image_name": self.image_name, "message": self.message}
