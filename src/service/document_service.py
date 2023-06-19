from firebase_admin import firestore
import datetime


"""service to manage documents collection"""


def to_json(page_content: str):
    return {
        "page_content": page_content,
        "timestamp": datetime.datetime.now().timestamp(),
    }


class DocumentService:
    def __init__(self):
        self.db = firestore.client()
        self.documents_ref = self.db.collection("documents")

    """read all documents"""

    def read(self):
        query = self.documents_ref.order_by("timestamp")
        docs = query.stream()
        result = []
        for item in docs:
            item_data = item.to_dict()
            result.append({"id": item.id, "page_content": item_data["page_content"]})
        return result

    """read one document"""

    def readOneDocument(self, id: str):
        doc = self.documents_ref.document(id).get()
        return {"id": id, "page_content": doc.to_dict()["page_content"]}

    """create a new document"""

    def create(self, page_content: str):
        return self.documents_ref.document().set(to_json(page_content))

    """update a document by using id"""

    def update(self, id: str, page_content: str):
        return self.documents_ref.document(id).update(to_json(page_content))

    """delete a document by using id"""

    def delete(self, id: str):
        return self.documents_ref.document(id).delete()
