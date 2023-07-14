import json

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.common.utils import ProgramType, DEFAULT_GPT_MODEL
from Brain.src.firebase.firebase import (
    firebase_admin_with_setting,
    delete_data_from_realtime,
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
    AutoTaskDelete,
    GetContactsByIds,
)
from Brain.src.rising_plugin.risingplugin import (
    getCompletion,
    getTextFromImage,
    query_image_ask,
)
from Brain.src.firebase.cloudmessage import CloudMessage
from Brain.src.rising_plugin.image_embedding import embed_image_text, query_image_text

from Brain.src.logs import logger
from Brain.src.model.basic_model import BasicModel
from Brain.src.model.feedback_model import FeedbackModel
from Brain.src.service.BabyAGIService import BabyAGIService
from Brain.src.service.auto_task_service import AutoTaskService
from Brain.src.service.command_service import CommandService
from Brain.src.service.contact_service import ContactsService
from Brain.src.service.feedback_service import FeedbackService
from Brain.src.service.llm.chat_service import ChatService
from Brain.src.service.twilio_service import TwilioService

from fastapi import APIRouter, Depends

router = APIRouter()


def construct_blueprint_api() -> APIRouter:
    # Assembler
    assembler = Assembler()

    # Service

    command_service = CommandService()

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
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        # cloud message
        cloud_message = CloudMessage(firebase_app=firebase_app)

        # parsing params
        query = data.message
        token = setting.token
        uuid = setting.uuid
        # check browser endpoint
        # if client_info.is_browser():
        # query = f"{query} in a web browser"
        is_browser = client_info.is_browser()

        result = getCompletion(
            query=query,
            setting=setting,
            firebase_app=firebase_app,
            is_browser=is_browser,
        )

        # check contact querying
        try:
            contacts_service = ContactsService(
                firebase_app=firebase_app, setting=setting
            )
            if result["program"] == ProgramType.AUTO_TASK:
                auto_task_service = AutoTaskService()
                result["content"] = auto_task_service.ask_task_with_llm(
                    query=query, firebase_app=firebase_app, setting=setting
                )
                return assembler.to_response(200, "", result)

            if result["program"] == ProgramType.CONTACT:
                # querying contacts to getting its expected results
                contacts_results = contacts_service.query_contacts(
                    uuid=uuid, search=result["content"]
                )
                result["content"] = str(contacts_results)
            value = ""
            if not client_info.is_browser():
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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()
        # cloud message
        try:
            cloud_message = CloudMessage(firebase_app=firebase_app)

            image_model = ImageModel()
            token = setting.token

            image_model.image_name = data.image_name
            image_model.uuid = setting.uuid
            image_model.status = data.status

            image_model.image_text = getTextFromImage(
                filename=image_model.image_name, firebase_app=firebase_app
            )

            embed_result = embed_image_text(image=image_model, setting=setting)

            notification = {"title": "alert", "content": embed_result}

            state, value = cloud_message.send_message(notification, [token])
            return assembler.to_response(200, value, image_model.to_json())
        except BrainException as ex:
            return ex.get_response_exp()

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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        # cloud message
        cloud_message = CloudMessage(firebase_app=firebase_app)
        # parsing params
        image_name = data.image_name
        message = data.message
        token = setting.token
        uuid = setting.uuid

        image_content = getTextFromImage(filename=image_name, firebase_app=firebase_app)
        # check message type
        image_response = {}
        try:
            # check about asking image description with trained data
            if query_image_ask(
                image_content=image_content, message=message, setting=setting
            ):
                image_response["image_desc"] = image_content
            else:
                relatedness_data = query_image_text(image_content, message, setting)

                image_response["image_name"] = relatedness_data
        except ValueError as e:
            print("image_relatedness parsing error for message chain data")
            if isinstance(e, BrainException):
                return e.get_response_exp()

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
            # firebase admin init
            setting, firebase_app = firebase_admin_with_setting(data)

            # cloud message
            cloud_message = CloudMessage(firebase_app=firebase_app)
            feedback_service = FeedbackService(firebase_app=firebase_app)
            token = setting.token
            uuid = setting.uuid

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
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "failed to add", "")
        return assembler.to_response(200, "added successfully", "")

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/feedback/{search}/{rating}")
    def get_feedback(search: str, rating: int, data: BasicReq):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        # cloud message
        cloud_message = CloudMessage(firebase_app=firebase_app)
        feedback_service = FeedbackService(firebase_app=firebase_app)
        result = feedback_service.get(search, rating)
        return assembler.to_response(200, "added successfully", result)

    """@generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/commands")
    def get_commands(data: BasicReq):
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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        # cloud message
        cloud_message = CloudMessage(firebase_app=firebase_app)
        try:
            token = setting.token
            uuid = setting.uuid
            histories = assembler.to_array_message_model(data.history)  # json array
            user_input = data.user_input
            """init chat service with model"""
            chat_service = ChatService(ai_name=uuid, llm_model=DEFAULT_GPT_MODEL)
            # getting chat response from rising ai
            assistant_reply = chat_service.chat_with_ai(
                prompt="",
                user_input=user_input,
                full_message_history=histories,
                permanent_memory=None,
                setting=setting,
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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

            # parsing contacts
            contacts = []
            for contact in data.contacts:
                contacts.append(assembler.to_contact_model(contact))
            # train contact
            contacts_service = ContactsService(
                firebase_app=firebase_app, setting=setting
            )
            contacts_service.train(uuid, contacts)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
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
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

            # parsing contacts
            # train contact
            contacts_service = ContactsService(
                firebase_app=firebase_app, setting=setting
            )
            contacts_service.delete_all(uuid)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to delete contacts", "")
        return assembler.to_response(
            200, "Deleted all contacts from pinecone successfully", ""
        )

    """@generator.request_body(
        {
            "token": "String",
            "uuid": "String",
            "data": {
                "reference_link": "test link",
            },
        }
    )

    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )

    """

    @router.post("/auto_task/delete")
    def delete_data(data: AutoTaskDelete):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

            # parsing contacts
            # train contact
            delete_data_from_realtime(data.data.reference_link, firebase_app)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to delete data", "")
        return assembler.to_response(
            200, "Deleted data from real-time database of firebase", ""
        )

    @router.post("/auto_task/babyagi")
    def autotask_babyagi(
        data: Notification, client_info: ClientInfo = Depends(get_client_info)
    ):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            babyagi_service = BabyAGIService()
            reference_link = babyagi_service.ask_task_with_llm(
                query=data.message, firebase_app=firebase_app, setting=setting
            )
            return assembler.to_response(200, "", reference_link)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to handle it with BabyAGI", "")

    """@generator.request_body(
            {
                "token": "String",
                "uuid": "String",
                "contactIds": [
                    "String"
                ]
            }
        )

        @generator.response(
            status_code=200, schema={"message": "message", "result": "test_result"}
        )

    """

    @router.post("/contacts/get_by_ids")
    def get_contacts_by_ids(data: GetContactsByIds):
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        token: str = setting.token
        uuid: str = setting.uuid

        result = ContactsService(
            firebase_app=firebase_app, setting=setting
        ).get_contacts_by_ids(uuid=uuid, contactIds=data.contactIds)

        return assembler.to_response(200, "Success to get contacts by uuid", result)

    return router
