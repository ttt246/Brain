"""train model:
{
    "id": "String",
    "data": [{"page_content": "String", "timestamp": 0}],
    "status": "created | updated | deleted",
}"""

from src.model.requests.request_model import Train


class TrainModel:
    def __init__(self, TrainData: Train):
        self.id = TrainData.id
        self.data = TrainData.data
        self.status = TrainStatus.UPDATED


"""train status: created | updated | deleted"""


class TrainStatus:
    CREATED = "created"
    UPDATED = "updated"
    DELETED = "deleted"
