from pydantic import BaseModel


class BasicReq(BaseModel):
    token: str
    uuid: str
    model: str = "gpt-3.5-turbo"


"""endpoint: /sendNotification"""


class Notification(BasicReq):
    message: str


"""endpoint: /uploadImage"""


class UploadImage(BasicReq):
    image_name: str
    status: str


"""endpoint: /image_relatedness"""


class ImageRelatedness(BasicReq):
    image_name: str
    message: str


"""endpoint: /feedback"""


class Feedback(BasicReq):
    class Prompt(BaseModel):
        image_name: str
        message: str

    prompt: Prompt
    completion: Prompt
    rating: int


"""endpoint: /chat_rising"""


class ChatRising(BasicReq):
    class Format(BaseModel):
        role: str
        content: str

    history: list[Format]
    user_input: str
    model: str


"""endpoint: /send_sms"""


class SendSMS(BasicReq):
    class Body(BaseModel):
        _from: str
        to: str
        body: str

    data: Body


"""endpoint : /train/contacts"""


class TrainContacts(BasicReq):
    class ContactReq(BaseModel):
        contactId: str
        displayName: str
        phoneNumbers: list[str]
        status: str

    contacts: list[ContactReq]
