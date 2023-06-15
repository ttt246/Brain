"""train model:
{
    "trainId": "String",
    "displayName": "String",
    "phoneNumbers": ["String"],
    "status": "created | updated | deleted",
}"""
from typing import Any

# from src.model.requests.request_model import TrainTrains


class TrainModel:
    def __init__(self):
        self.train_id = ""
        self.page_content = ""
        self.status = TrainStatus.UPDATED



"""train status: created | updated | deleted"""


class TrainStatus:
    CREATED = "created"
    UPDATED = "updated"
    DELETED = "deleted"
