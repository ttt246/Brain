"""train model:
{
    "id": "String",
    "data": [{"page_content": "String", "timestamp": 0}],
    "status": "created | updated | deleted",
}"""

from src.model.requests.request_model import Training


class TrainingModel:
    def __init__(self, TrainingData: Training):
        self.id = TrainingData.id
        self.data = TrainingData.data
        self.status = TrainingStatus.UPDATED


"""train status: created | updated | deleted"""


class TrainingStatus:
    CREATED = "created"
    UPDATED = "updated"
    DELETED = "deleted"
