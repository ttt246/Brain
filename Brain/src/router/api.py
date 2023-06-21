import json
import os

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.common.utils import ProgramType
from Brain.src.firebase.firebase import (
    initialize_app,
    get_firebase_admin_name,
    firebase_admin_with_setting,
)
from Brain.src.model.image_model import ImageModel
from Brain.src.model.requests.request_model import (
    Notification,
    UploadImage,
    ImageRelatedness,
    Feedback,
    ChatRising,
    SendSMS,
    TrainContacts,
    BasicReq,
    ClientInfo,
    get_client_info,
)
from Brain.src.rising_plugin.risingplugin import (
    getCompletion,
    getTextFromImage,
    query_image_ask,
    handle_chat_completion,
)
from Brain.src.firebase.cloudmessage import CloudMessage
from Brain.src.rising_plugin.image_embedding import embed_image_text, query_image_text

from Brain.src.logs import logger
from Brain.src.model.basic_model import BasicModel
from Brain.src.model.feedback_model import FeedbackModel
from Brain.src.service.command_service import CommandService
from Brain.src.service.contact_service import ContactsService
from Brain.src.service.feedback_service import FeedbackService
from Brain.src.service.llm.chat_service import ChatService
from Brain.src.service.twilio_service import TwilioService

from fastapi import APIRouter, Request, Depends

router = APIRouter()


def construct_blueprint_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    # Service
    feedback_service = FeedbackService()
    command_service = CommandService()
    contacts_service = ContactsService()

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )
    @generator.request_body(
        {"message": "this is test message", "token": "test_token", "uuid": "test_uuid"}
    )"""

    @router.post("/sendNotification")
    def send_notification(
        data: Notification, client_info: ClientInfo = Depends(get_client_info)
    ):
        # firebase admin init
        setting, firebase_app = firebase_admin_with_setting(data)

        # cloud message
        cloud_message = CloudMessage(firebase_app=firebase_app)

        # parsing params
        query = data.message
        token = data.token
        uuid = data.uuid
        # check browser endpoint
        if client_info.is_browser():
            query = f"{query} in a web browser"

        result = getCompletion(
            query=query, setting=assembler.to_req_model(data), firebase_app=firebase_app
        )

        # check contact querying
        try:
            if result["program"] == ProgramType.CONTACT:
                # querying contacts to getting its expected results
                contacts_results = contacts_service.query_contacts(
                    uuid=uuid, search=result["content"]
                )
                result["content"] = str(contacts_results)

            notification = {"title": "alert", "content": json.dumps(result)}

            state, value = cloud_message.send_message(notification, [token])
            return assembler.to_response(200, value, result)
        except Exception as e:
            logger.error(
                title="sendNotification", message="json parsing or get completion error"
            )
            if isinstance(e, BrainException):
                return e.get_response_exp()

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )
    @generator.request_body(
        {
            "image_name": "this is test image path",
            "token": "test_token",
            "uuid": "test_uuid",
            "status": "created | updated | deleted",
        }
    )"""

    @router.post("/uploadImage")
    def upload_image(data: UploadImage):
        # cloud message
        cloud_message = CloudMessage()
        # parsing params
        setting = assembler.to_req_model(data)
        # firebase admin init
        initialize_app(setting)

        image_model = ImageModel()
        token = data.token

        image_model.image_name = data.image_name
        image_model.uuid = data.uuid
        image_model.status = data.status

        image_model.image_text = getTextFromImage(image_model.image_name)

        embed_result = embed_image_text(image_model)

        notification = {"title": "alert", "content": embed_result}

        state, value = cloud_message.send_message(notification, [token])
        return assembler.to_response(200, value, image_model.to_json())

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )
    @generator.request_body(
        {
            "image_name": "this is test image path",
            "message": "this is a test message",
            "token": "test_token",
            "uuid": "test_uuid",
        }
    )"""

    @router.post("/image_relatedness")
    def image_relatedness(data: ImageRelatedness):
        # cloud message
        cloud_message = CloudMessage()
        # parsing params
        image_name = data.image_name
        message = data.message
        token = data.token
        uuid = data.uuid

        image_content = getTextFromImage(image_name)
        # check message type
        image_response = {}
        try:
            # check about asking image description with trained data
            if query_image_ask(
                image_content=image_content, message=message, uuid=uuid, setting=data
            ):
                image_response["image_desc"] = image_content
            else:
                relatedness_data = query_image_text(image_content, message, uuid)

                image_response["image_name"] = relatedness_data
        except ValueError as e:
            print("image_relatedness parsing error for message chain data")

        notification = {"title": "alert", "content": json.dumps(image_response)}
        state, value = cloud_message.send_message(notification, [token])

        return assembler.to_response(
            code=200,
            message=value,
            result={"program": "image", "content": image_response},
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "prompt": {"image_name": "test_image", "message": "test_message"},
            "completion": {"image_name": "test_image", "message": "test_message"},
            "rating": 1,
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/feedback")
    def add_feedback(data: Feedback):
        try:
            token = data.token
            uuid = data.uuid

            # parsing feedback payload
            prompt = BasicModel(
                image_name=data.prompt.image_name, message=data.prompt.message
            )
            completion = BasicModel(
                image_name=data.completion.image_name, message=data.completion.message
            )
            rating = data.rating
            feedback = FeedbackModel(uuid, prompt, completion, rating)

            # add the feedback
            feedback_service.add(feedback)
        except Exception as e:
            return assembler.to_response(400, "failed to add", "")
        return assembler.to_response(200, "added successfully", "")

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.get("/feedback/{search}/{rating}")
    def get_feedback(search: str, rating: int):
        result = feedback_service.get(search, rating)
        return assembler.to_response(200, "added successfully", result)

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.get("/commands")
    def get_commands():
        result = command_service.get()
        return assembler.to_response(
            200, "success", {"program": "help_command", "content": result}
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "history": [{"role": "user", "content": "test_message"}],
            "user_input": "user_message",
            "model": "gpt-3.5-turbo",
        }
    )
    @generator.response(
        status_code=200,
        schema={
            "message": "message",
            "result": {
                "program": "agent",
                "message": {"role": "assistant", "content": "content"},
            },
        },
    )"""

    @router.post("/chat_rising")
    def message_agent(data: ChatRising):
        try:
            token = data.token
            uuid = data.uuid
            histories = assembler.to_array_message_model(data.history)  # json array
            user_input = data.user_input
            model = data.model
            """init chat service with model"""
            chat_service = ChatService(ai_name=uuid, llm_model=model)
            # getting chat response from rising ai
            assistant_reply = chat_service.chat_with_ai(
                prompt="",
                user_input=user_input,
                full_message_history=histories,
                permanent_memory=None,
            )
        except Exception as e:
            return assembler.to_response(
                400, json.dumps(e.__getattribute__("error")), ""
            )
        return assembler.to_response(
            200,
            "added successfully",
            {
                "program": "agent",
                "message": assistant_reply.get_one_message_item().to_json(),
            },
        )

    """@generator.request_body(
        {
            "token": "test_token",
            "uuid": "test_uuid",
            "data": {
                "_from": "+15005550006",
                "to": "+12173748105",
                "body": "All in the game, yo",
            },
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/send_sms")
    def send_sms(data: SendSMS):
        try:
            token = data.token
            uuid = data.uuid

            # parsing feedback payload
            sms_model = assembler.to_sms_model(data.data)
            # send sms via twilio
            twilio_service = TwilioService()
            twilio_resp = twilio_service.send_sms(sms_model)
        except Exception as e:
            return assembler.to_response(400, "Failed to send sms", "")
        return assembler.to_response(200, "Sent a sms successfully", twilio_resp.sid)

    """@generator.request_body(
        {
            "token": "String",
            "uuid": "String",
            "contacts": [
                {
                    "contactId": "String",
                    "displayName": "String",
                    "phoneNumbers": ["String"],
                    "status": "created | updated | deleted",
                }
            ],
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/train/contacts")
    def train_contacts(data: TrainContacts):
        try:
            token = data.token
            uuid = data.uuid

            # parsing contacts
            contacts = []
            for contact in data.contacts:
                contacts.append(assembler.to_contact_model(contact))
            # train contact
            contacts_service.train(uuid, contacts)
        except Exception as e:
            return assembler.to_response(400, "Failed to train contacts", "")
        return assembler.to_response(200, "Trained successfully", "")

    """@generator.request_body(
        {
            "token": "String",
            "uuid": "String",
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/train/contacts/delete")
    def delete_all_contacts(data: BasicReq):
        try:
            token = data.token
            uuid = data.uuid

            # parsing contacts
            # train contact
            contacts_service.delete_all(uuid)
        except Exception as e:
            return assembler.to_response(400, "Failed to delete contacts", "")
        return assembler.to_response(
            200, "Deleted all contacts from pinecone successfully", ""
        )

    return router
