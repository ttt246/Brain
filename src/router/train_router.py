from fastapi import APIRouter, Request, Depends

from src.common.assembler import Assembler
from src.model.requests.request_model import (
    Document,
    Train,
)
from src.service.document_service import DocumentService
from src.service.train_service import TrainService

router = APIRouter()


def construct_blueprint_train_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    # Services
    train_service = TrainService()
    document_service = DocumentService()

    """@generator.response(
            status_code=200, schema={"message": "message", "result": "test_result"}
        )"""

    @router.get("/")
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
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.get("/document")
    def read_document():
        try:
            result = document_service.read()
        except Exception as e:
            return assembler.to_response(400, "failed to get all documents", "")
        return assembler.to_response(200, "Get all documents list successfully", result)

    """@generator.response(
            status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
        )"""

    @router.get("/document/{id}")
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

    @router.put("/document")
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

    @router.post("/document")
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
            "page_content": "string",            
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": {"id": "id", "page_content":"page_content"}}
    )"""

    @router.delete("/document")
    def delete_document(data: Document):
        try:
            document_service.delete(data.id)
        except Exception as e:
            return assembler.to_response(400, "fail to delete one document", "")
        return assembler.to_response(200, "delete one document successfully", "")

    return router
