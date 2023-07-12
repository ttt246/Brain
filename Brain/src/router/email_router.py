import json
import shutil
from fastapi import APIRouter

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.model.requests.request_model import EmailReader, EmailSender
from Brain.src.rising_plugin.gmail.email_plugin import EmailPlugin
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
                "from": "testfrom@test.com",
                "to": "test@gmail.com",
                "date": "Tue, 04 Jul 2023 12:55:19 +0000",
                "cc": "",
                "subject": "subject",
                "body": "message"
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
            email_manager = EmailPlugin()
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
            return assembler.to_response(
                400,
                "Failed to read emails",
                "ConnectionResetError occurred for IMAP Server",
            )
        return assembler.to_response(200, "", result)

    """@generator.request_body(
            {
                "token": "String",
                "uuid": "String",
                "data": {
                    "sender": "testsender@gmail.com",
                    "pwd": "use app password of your google account",
                    "to": "testto@gmail.com",
                    "subject": "title of email",
                    "body": "email content",
                    "to_send": "true or false whether send to inbox or drafts",
                    "filename": "test.txt",
                    "file_content": "string encoded with base64"
                },
            }
        )

        @generator.response(
            status_code=200, schema={"message": "message", "result": "result"
        )

    """

    @router.post("/send_email")
    def send_email(data: EmailSender):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return assembler.to_response(ex.code, ex.message, "")
        try:
            token = setting.token
            uuid = setting.uuid

            # send email
            email_manager = EmailPlugin()
            result = ""
            if not data.data.filename:
                result += email_manager.send_email(
                    sender=data.data.sender,
                    pwd=data.data.pwd,
                    to=data.data.to,
                    subject=data.data.subject,
                    body=data.data.body,
                    to_send=data.data.to_send,
                )
            else:
                file_name, file_directory = email_manager.write_attachment(
                    filename=data.data.filename, file_content=data.data.file_content
                )

                result += email_manager.send_email_with_attachment(
                    sender=data.data.sender,
                    pwd=data.data.pwd,
                    to=data.data.to,
                    subject=data.data.subject,
                    body=data.data.body,
                    to_send=data.data.to_send,
                    filename=file_name,
                )

                shutil.rmtree(file_directory)

        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to read emails", "")
        return assembler.to_response(200, "", result)

    return router
