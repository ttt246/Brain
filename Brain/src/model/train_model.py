"""train model:
{
    "id": "String",
    "data": [{"page_content": "String", "timestamp": 0}],
    "status": "created | updated | deleted",
}"""

from Brain.src.model.requests.request_model import Train


class TrainModel:
    def __init__(self, train_data: Train):
        self.id = train_data.id
        self.data = train_data.data
        self.status = TrainStatus.UPDATED


"""train status: created | updated | deleted"""


class TrainStatus:
    CREATED = "created"
    UPDATED = "updated"
    DELETED = "deleted"
