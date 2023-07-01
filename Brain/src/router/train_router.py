from fastapi import APIRouter

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.firebase.firebase import firebase_admin_with_setting
from Brain.src.model.requests.request_model import (
    Document,
    BasicReq,
)
from Brain.src.service.train_service import TrainService

router = APIRouter()


def construct_blueprint_train_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("")
    def read_all_documents(data: BasicReq):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
        try:
            result = train_service.read_all_documents()
        except Exception as e:
            return assembler.to_response(400, "failed to get all documents", "")
        return assembler.to_response(200, "Get all documents list successfully", result)

    """@generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id", 
    "page_content":"page_content"}} )"""

    @router.post("/all")
    def train_all_documents(data: BasicReq):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
        try:
            result = train_service.train_all_documents()
        except Exception as e:
            return assembler.to_response(400, "failed to get all documents", "")
        return assembler.to_response(200, "Get all documents list successfully", result)

    """@generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id", 
    "page_content":"page_content"}} )"""

    @router.post("/read/{document_id}")
    def read_one_document(document_id: str, data: BasicReq):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
        if document_id != "all":
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

    @router.post("/create")
    def create_document_train(data: Document):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
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
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
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

    @router.post("/delete/{document_id}")
    def delete_one_document(document_id: str, data: BasicReq):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
        try:
            result = train_service.delete_one_document(document_id)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "fail to delete one train", "")
        return assembler.to_response(
            200, "deleted one document and train data successfully", result
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",            
        }
    )
    @generator.response( status_code=200, schema={"message": "message", "result": {"document_id": "document_id"}} )"""

    @router.post("/delete/all/vectors")
    def delete_all_pinecone(data: BasicReq):
        # parsing params
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # Services
        train_service = TrainService(firebase_app=firebase_app, setting=setting)
        try:
            result = train_service.delete_all_training_from_pinecone()
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "fail to delete one train", "")
        return assembler.to_response(
            200, "deleted one document and train data successfully", result
        )

    return router
