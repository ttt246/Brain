"""contact model:
{
    "contactId": "String",
    "displayName": "String",
    "phoneNumbers": ["String"],
    "status": "created | updated | deleted",
}"""
from typing import Any


class ContactModel:
    def __init__(self):
        self.contact_id = ""
        self.display_name = ""
        self.phone_numbers = []
        self.status = ContactStatus.UPDATED

    def get_contact_model(self, data: Any) -> None:
        self.contact_id = data["contactId"]
        self.display_name = data["displayName"]
        self.phone_numbers = []
        for phone in data["phoneNumbers"]:
            self.phone_numbers.append(phone)
        self.status = data["status"]

    def get_str_phones(self):
        return "".join(self.phone_numbers)


"""contact status: created | updated | deleted"""


class ContactStatus:
    CREATED = "created"
    UPDATED = "updated"
    DELETED = "deleted"
