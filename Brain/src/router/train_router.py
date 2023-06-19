from fastapi import APIRouter, Request, Depends

from Brain.src.common.assembler import Assembler
from Brain.src.model.requests.request_model import (
    Document,
    Train,
)
from Brain.src.service.document_service import DocumentService
from Brain.src.service.train_service import TrainService

router = APIRouter()


def construct_blueprint_train_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    # Services
    train_service = TrainService()

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.get("")
    def train_all_documents():
        train_service.trainAllDocuments()

        return assembler.to_response(200, "trained all documents successfully", "")

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.get("/{id}")
    def train_one_document(id: str):
        train_service.trainOneDocument(id)
        return assembler.to_response(200, "trained one document successfully", "")

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test result"}
    )"""

    @router.delete("")
    def delete_all_trains():
        try:
            train_service.delete_all()
        except Exception as e:
            return assembler.to_response(400, "fail to delete one train", "")
        return assembler.to_response(200, "delete all trains successfully", "")

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "id": "string",            
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test result"}
    )"""

    @router.delete("/{id}")
    def delete_one_train(id: str):
        try:
            train_service.delete_one(id)
        except Exception as e:
            return assembler.to_response(400, "fail to delete one train", "")
        return assembler.to_response(200, "delete all trains successfully", "")

    return router
