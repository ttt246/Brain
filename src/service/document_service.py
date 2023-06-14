from firebase_admin import firestore
import datetime


"""service to manage documents collection"""


def to_json(page_content: str):
    return {
        "page_content": page_content,
        "timestamp": datetime.datetime.now().timestamp()
    }


class DocumentService:
    def __init__(self):
        self.db = firestore.client()
        self.documents_ref = self.db.collection("documents")

    """add a new document"""

    def add(self, page_content: str):
        return self.documents_ref.document().set(to_json(page_content))

    """get list of document"""

    def getAll(self):
        query = self.documents_ref.order_by("timestamp")
        docs = query.stream()
        result = []
        for item in docs:
            item_data = item.to_dict()
            result.append({"id": item.id, "data": item_data})
        print(result)
        return result
