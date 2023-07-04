# assembler to mapping data into another data type.
from typing import Any, List

from Brain.src.common.http_response_codes import responses
from Brain.src.model.basic_model import BasicModel
from Brain.src.model.contact_model import ContactModel
from Brain.src.model.message_model import MessageModel
from Brain.src.model.req_model import ReqModel
from Brain.src.model.requests.request_model import (
    ChatRising,
    SendSMS,
    TrainContacts,
    BasicReq,
)
from Brain.src.model.sms_model import SMSModel


class Assembler:
    """mapping to BasicModel"""

    def to_basic_model(self, data: Any) -> BasicModel:
        model = BasicModel(data["image_name"], data["message"])
        return model

    """mapping to http response"""

    def to_response(self, code, message, result) -> Any:
        response = {"message": responses[code], "result": result, "status_code": code}
        return response

    """mapping data to a collection of MessageModel"""

    def to_array_message_model(self, data: Any) -> List[MessageModel]:
        result = []
        for item in data:
            result.append(self.to_message_model(item))
        return result

    """mapping data to a MessageModel"""

    def to_message_model(self, data: ChatRising.Format) -> MessageModel:
        return MessageModel(data.role, data.content)

    """mapping data to a SMSModel"""

    def to_sms_model(self, data: SendSMS.Body) -> SMSModel:
        sms_model = SMSModel()
        sms_model.get_sms_model(data)
        return sms_model

    """mapping data to a ContactModel"""

    def to_contact_model(self, data: TrainContacts.ContactReq) -> ContactModel:
        contact_model = ContactModel()
        contact_model.get_contact_model(data)
        return contact_model

    """mapping result type into json
    {
        "program": sms | contacts | browser | selectitemdetailinfo, 
        "content": string
    }
    """

    def to_result_format(self, program: str, content: str) -> Any:
        return {"program": program, "content": content}

    """mapping basic req"""

    def to_req_model(self, basic_req: BasicReq.Confs) -> ReqModel:
        return ReqModel(basic_req.to_json())

    """mapping data from a ContactModel into json
    {
        "contactId": int,
        "displayName": string,
        "phoneNumbers": [string]
    }
    """

    def to_contact_result_format(self, contact: ContactModel) -> Any:
        return {
            "contactId": contact.contact_id,
            "displayName": contact.display_name,
            "phoneNumbers": contact.phone_numbers,
        }
