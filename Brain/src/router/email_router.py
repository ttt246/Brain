import json

from fastapi import APIRouter

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.model.requests.request_model import (
    EmailReader,
)
from Brain.src.rising_plugin.gmail.manage_gmail import EmailManager
from Brain.src.firebase.firebase import firebase_admin_with_setting

router = APIRouter()


def construct_blueprint_email_api() -> APIRouter:
    # Assembler
    assembler = Assembler()
    # Services

    """@generator.request_body(
            {
                "token": "String",
                "uuid": "String",
                "data": {
                    "sender": "test@gmail.com",
                    "pwd": "use app password of your google account",
                    "imap_folder": "inbox or drafts"
                },
            }
        )

        @generator.response(
            status_code=200, schema={"message": "message", "result": [{
                "From": "testfrom@test.com",
                "To": "test@gmail.com",
                "Date": "Tue, 04 Jul 2023 12:55:19 +0000",
                "CC": "",
                "Subject": "subject",
                "Message Body": "message"
            }]}
        )

    """

    @router.post("/read_emails")
    def read_emails(data: EmailReader):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

            # if imap_folder is drafts, then search is ALL
            imap_search_cmd = "UNSEEN"
            if data.data.imap_folder not in "inbox":
                imap_search_cmd = "(ALL)"

            # read emails
            email_manager = EmailManager()
            result = email_manager.read_emails(
                sender=data.data.sender,
                pwd=data.data.pwd,
                imap_folder=data.data.imap_folder,
                imap_search_command=imap_search_cmd,
            )
            result = json.loads(result)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to read emails", "")
        return assembler.to_response(200, "", result)

    return router
