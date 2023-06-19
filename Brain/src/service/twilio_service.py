"""service for sms using twilio apis"""
import json
from typing import Any

from twilio.rest import Client

from src.common.utils import ACCOUNT_SID, AUTH_TOKEN
from src.logs import logger
from src.model.sms_model import SMSModel


class TwilioService:
    def __init__(self):
        self.client = Client(ACCOUNT_SID, AUTH_TOKEN)

    def send_sms(self, data: SMSModel) -> Any:
        message = self.client.messages.create(
            body=data.body, from_=data._from, to=data._to
        )
        logger.info(
            message=message.sid,
            title="sent twilio sms",
        )
        return message
