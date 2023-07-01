"""service to manage trains"""
from typing import Any

import firebase_admin

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.csv_embed import get_embed
from Brain.src.rising_plugin.pinecone_engine import (
    get_pinecone_index_namespace,
    delete_pinecone,
    add_pinecone,
    delete_all_pinecone,
)

from firebase_admin import firestore
import datetime


def to_json(page_content: str):
    return {
        "page_content": page_content,
        "timestamp": datetime.datetime.now().timestamp(),
    }


class TrainService:
    """train (getting embedding) and update pinecone with embeddings by document_id
    train datatype:
    key: document_id
    values: {page_content}"""

    db: Any
    documents_ref: Any

    def __init__(self, firebase_app: firebase_admin.App, setting: ReqModel):
        self.firebase_app = firebase_app
        self.setting = setting

    def init_firestore(self):
        self.db = firestore.client(app=self.firebase_app)
        self.documents_ref = self.db.collection("documents")

    """read all documents from firestore"""

    def read_all_documents(self) -> Any:
        self.init_firestore()
        query = self.documents_ref.order_by("timestamp")
        docs = query.stream()
        result = []
        for item in docs:
            item_data = item.to_dict()
            result.append(
                {"document_id": item.id, "page_content": item_data["page_content"]}
            )
        return result

    """read one document from firestore"""

    def read_one_document(self, document_id: str):
        self.init_firestore()
        doc = self.documents_ref.document(document_id).get()
        if doc.exists:
            return {
                "document_id": document_id,
                "page_content": doc.to_dict()["page_content"],
            }
        else:
            return None

    """create a new document and train it"""

    def create_one_document(self, page_content: str):
        self.init_firestore()
        # Auto-generate document ID
        auto_generated_doc_ref = self.documents_ref.document()
        auto_generated_doc_ref.set(to_json(page_content))
        auto_generated_document_id = auto_generated_doc_ref.id
        self.train_one_document(auto_generated_document_id, page_content)
        return {"document_id": auto_generated_document_id, "page_content": page_content}

    """update a document by using id and train it"""

    def update_one_document(self, document_id: str, page_content: str):
        self.init_firestore()
        self.documents_ref.document(document_id).update(to_json(page_content))
        self.train_one_document(document_id, page_content)
        return {"document_id": document_id, "page_content": page_content}

    """delete a document by using document_id"""

    def delete_one_document(self, document_id: str):
        self.init_firestore()
        self.documents_ref.document(document_id).delete()
        self.delete_one_pinecone(document_id)
        return {"document_id": document_id}

    def train_all_documents(self) -> str:
        self.init_firestore()
        self.delete_all()
        documents = self.read_all_documents()
        result = list()
        pinecone_namespace = self.get_pinecone_index_namespace()
        for item in documents:
            query_result = get_embed(data=item["page_content"], setting=self.setting)
            result.append(query_result)
            key = item["document_id"]
            value = f'{item["page_content"]}'
            # get vectoring data(embedding data)
            vectoring_values = get_embed(data=value, setting=self.setting)
            add_pinecone(
                namespace=pinecone_namespace,
                key=key,
                value=vectoring_values,
                setting=self.setting,
            )

        return "trained all documents successfully"

    def train_one_document(self, document_id: str, page_content: str) -> None:
        self.init_firestore()
        pinecone_namespace = self.get_pinecone_index_namespace()
        result = list()
        query_result = get_embed(data=page_content, setting=self.setting)
        result.append(query_result)
        key = document_id
        value = f"{page_content}, {query_result}"
        # get vectoring data(embedding data)
        vectoring_values = get_embed(data=value, setting=self.setting)
        add_pinecone(
            namespace=pinecone_namespace,
            key=key,
            value=vectoring_values,
            setting=self.setting,
        )

    def delete_all(self) -> Any:
        self.init_firestore()
        return delete_all_pinecone(
            namespace=self.get_pinecone_index_namespace(),
            setting=self.setting,
        )

    def delete_one_pinecone(self, document_id: str) -> Any:
        self.init_firestore()
        return delete_pinecone(
            namespace=self.get_pinecone_index_namespace(),
            key=document_id,
            setting=self.setting,
        )

    def delete_all_training_from_pinecone(self) -> Any:
        self.init_firestore()
        return delete_all_pinecone(
            namespace=self.get_pinecone_index_namespace(), setting=self.setting
        )

    def get_pinecone_index_namespace(self) -> str:
        self.init_firestore()
        return get_pinecone_index_namespace(f"trains")

    def get_pinecone_index_train_namespace(self) -> str:
        self.init_firestore()
        return get_pinecone_index_namespace(f"trains")
