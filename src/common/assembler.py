# assembler to mapping data into another data type.
from typing import Any, List

from flask import jsonify

from src.model.basic_model import BasicModel
from src.model.contact_model import ContactModel
from src.model.message_model import MessageModel
from src.model.sms_model import SMSModel


class Assembler:
    """mapping to BasicModel"""

    def to_basic_model(self, data: Any) -> BasicModel:
        model = BasicModel(data["image_name"], data["message"])
        return model

    """mapping to http response"""

    def to_response(self, code, message, result) -> Any:
        response = jsonify({"message": message, "result": result})
        response.status_code = code
        return response

    """mapping data to a collection of MessageModel"""

    def to_array_message_model(self, data: Any) -> List[MessageModel]:
        result = []
        for item in data:
            result.append(self.to_message_model(item))
        return result

    """mapping data to a MessageModel"""

    def to_message_model(self, data: Any) -> MessageModel:
        return MessageModel(data["role"], data["content"])

    """mapping data to a SMSModel"""

    def to_sms_model(self, data: Any) -> SMSModel:
        sms_model = SMSModel()
        sms_model.get_sms_model(data)
        return sms_model

    """mapping data to a ContactModel"""

    def to_contact_model(self, data: Any) -> ContactModel:
        contact_model = ContactModel()
        contact_model.get_contact_model(data)
        return contact_model
