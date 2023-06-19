from fastapi import APIRouter, Request, Depends

from src.common.assembler import Assembler
from src.model.requests.request_model import (
    Document,
    Train,
)
from src.service.document_service import DocumentService
from src.service.train_service import TrainService

router = APIRouter()


def construct_blueprint_document_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    # Services
    document_service = DocumentService()

    @router.get("")
    def read_all_documents():
        try:
            result = document_service.read()
        except Exception as e:
            return assembler.to_response(400, "failed to get all documents", "")
        return assembler.to_response(200, "Get all documents list successfully", result)

    """@generator.response(
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.get("/{id}")
    def read_one_document(id: str):
        try:
            result = document_service.readOneDocument(id)
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
    @generator.response(
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.put("")
    def update_document(data: Document):
        try:
            document_service.update(data.id, data.page_content)
        except Exception as e:
            return assembler.to_response(400, "fail to update one document", "")
        return assembler.to_response(200, "updated one document successfully", "")

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "page_content": "string",            
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.post("")
    def create_document(data: Document):
        try:
            document_service.create(data.page_content)
        except Exception as e:
            return assembler.to_response(400, "faile to create one document", "")
        return assembler.to_response(200, "create one document successfully", "")

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "id": "string",            
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.delete("/{id}")
    def delete_one_document(id: str):
        try:
            document_service.delete(id)
        except Exception as e:
            return assembler.to_response(400, "fail to delete one document", "")
        return assembler.to_response(200, "delete one document successfully", "")

    return router
