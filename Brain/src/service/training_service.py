"""service to manage trains"""
from typing import List, Any

from src.rising_plugin.csv_embed import get_embed
from src.service.document_service import DocumentService
from src.rising_plugin.pinecone_engine import (
    get_pinecone_index_namespace,
    update_pinecone,
    init_pinecone,
    delete_pinecone,
    add_pinecone,
    delete_all_pinecone,
)

from src.model.training_model import TrainingModel, TrainingStatus


class TrainingService:
    """train (getting embedding) and update pinecone with embeddings by train_id
    train datatype:
    key: id
    values: {id},{data}, {status}"""

    def trainingAllDocuments(self) -> None:
        document_service = DocumentService()
        documents = document_service.getAll()
        result = list()
        pinecone_namespace = self.get_pinecone_index_namespace()
        for item in documents:
            query_result = get_embed(item["data"]["page_content"])
            result.append(query_result)
            key = item["id"]
            value = f'{item["data"]["page_content"]}, {query_result}'
            # get vectoring data(embedding data)
            vectoring_values = get_embed(value)
            add_pinecone(
                namespace=pinecone_namespace, key=key, value=vectoring_values
            )

    def trainingOneDocument(self, document: TrainingModel) -> None:
        pinecone_namespace = self.get_pinecone_index_namespace()
        result = list()
        query_result = get_embed(document.data.page_content)
        result.append(query_result)
        key = document.id
        value = f"{document.data.page_content}, {query_result}"
        # get vectoring data(embedding data)
        vectoring_values = get_embed(value)
        # create | update | delete pinecone
        if document.status == TrainingStatus.CREATED:
            add_pinecone(
                namespace=pinecone_namespace, key=key, value=vectoring_values
            )
        elif document.status == TrainingStatus.DELETED:
            delete_pinecone(namespace=pinecone_namespace, key=key)
        elif document.status == TrainingStatus.UPDATED:
            update_pinecone(
                namespace=pinecone_namespace, key=key, value=vectoring_values
            )

    def delete_all(self) -> Any:
        return delete_all_pinecone(self.get_pinecone_index_namespace())

    def get_pinecone_index_namespace(self) -> str:
        return get_pinecone_index_namespace(f"trains")
