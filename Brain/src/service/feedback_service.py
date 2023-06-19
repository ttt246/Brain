from os import remove

from firebase_admin import firestore


from src.model.feedback_model import FeedbackModel

"""service to manage feedback collection"""


class FeedbackService:
    def __init__(self):
        self.db = firestore.client()
        self.feedbacks_ref = self.db.collection("feedbacks")

    """add a new feedback"""

    def add(self, feedback: FeedbackModel):
        return self.feedbacks_ref.document().set(feedback.to_json())

    """get list of feedback"""

    def get(self, search, rating):
        if rating == 0:
            query = self.feedbacks_ref.where("rating", "!=", rating)
        else:
            query = self.feedbacks_ref.where("rating", "==", rating)
        if search.replace(" ", "") != "":
            query = query.where("prompt.message", ">=", search).where(
                "prompt.message", "<=", search + "~"
            )
        docs = query.stream()
        result = []
        for item in docs:
            item_data = item.to_dict()
            result.append({"id": item.id, "data": item_data})
        print(result)
        return result
