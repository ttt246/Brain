from os import remove
from typing import Any

import firebase_admin
from firebase_admin import firestore


from Brain.src.model.feedback_model import FeedbackModel

"""service to manage feedback collection"""


class FeedbackService:
    db: Any
    feedbacks_ref: Any

    def __init__(self, firebase_app: firebase_admin.App):
        self.firebase_app = firebase_app

    def init_firestore(self):
        self.db = firestore.client(app=self.firebase_app)
        self.feedbacks_ref = self.db.collection("feedbacks")

    """add a new feedback"""

    def add(self, feedback: FeedbackModel):
        self.init_firestore()
        return self.feedbacks_ref.document().set(feedback.to_json())

    """get list of feedback"""

    def get(self, search, rating):
        self.init_firestore()
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
