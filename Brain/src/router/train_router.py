from fastapi import APIRouter

from Brain.src.common.assembler import Assembler
from Brain.src.model.requests.request_model import (
    Document,
)
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
    def read_all_documents():
        try:
            result = train_service.read_all_documents()
        except Exception as e:
            return assembler.to_response(400, "failed to get all documents", "")
        return assembler.to_response(200, "Get all documents list successfully", result)

    """@generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id", 
    "page_content":"page_content"}} )"""

    @router.get("/{document_id}")
    def read_one_document(document_id: str):
        try:
            result = train_service.read_one_document(document_id)
        except Exception as e:
            return assembler.to_response(400, "fail to get one document", "")
        return assembler.to_response(200, "Get one document successfully", result)

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "page_content": "string",            
        }
    )
    @generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id", 
    "page_content":"page_content"}} )"""

    @router.post("")
    def create_document_train(data: Document):
        try:
            result = train_service.create_one_document(data.page_content)
        except Exception as e:
            return assembler.to_response(400, "failed to create one document", "")
        return assembler.to_response(
            200, "created one document and trained it successfully", result
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "document_id": "string",
            "page_content": "string",
        }
    )
    @generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id", 
    "page_content":"page_content"}} )"""

    @router.put("")
    def update_one_document(data: Document):
        try:
            result = train_service.update_one_document(
                data.document_id, data.page_content
            )
        except Exception as e:
            return assembler.to_response(400, "fail to update one document", "")
        return assembler.to_response(
            200, "updated one document and trained it successfully", result
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "document_id": "string",            
        }
    )
    @generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id"}} )"""

    @router.delete("/{document_id}")
    def delete_one_document(document_id: str):
        try:
            result = train_service.delete_one_document(document_id)
        except Exception as e:
            return assembler.to_response(400, "fail to delete one train", "")
        return assembler.to_response(
            200, "deleted one document and train data successfully", result
        )

    return router
