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


class TrainService:
    """train (getting embedding) and update pinecone with embeddings by train_id
    train datatype:
    key: id
    values: {id},{data}, {status}"""

    def trainAllDocuments(self) -> None:
        document_service = DocumentService()
        documents = document_service.read()
        result = list()
        pinecone_namespace = self.get_pinecone_index_namespace()
        for item in documents:
            query_result = get_embed(item["page_content"])
            result.append(query_result)
            key = item["id"]
            value = f'{item["page_content"]}, {query_result}'
            # get vectoring data(embedding data)
            vectoring_values = get_embed(value)
            add_pinecone(namespace=pinecone_namespace, key=key, value=vectoring_values)

    def trainOneDocument(self, id: str) -> None:
        pinecone_namespace = self.get_pinecone_index_namespace()
        document_service = DocumentService()
        document = document_service.readOneDocument(id)
        result = list()
        query_result = get_embed(document["page_content"])
        result.append(query_result)
        key = document["id"]
        value = f"{document['page_content']}, {query_result}"
        # get vectoring data(embedding data)
        vectoring_values = get_embed(value)
        add_pinecone(namespace=pinecone_namespace, key=key, value=vectoring_values)

    def delete_all(self) -> Any:
        return delete_all_pinecone(self.get_pinecone_index_namespace())

    def get_pinecone_index_namespace(self) -> str:
        return get_pinecone_index_namespace(f"trains")
