"""sms message model includes from, to and body"""
from typing import Any

from Brain.src.model.requests.request_model import SendSMS


class SMSModel:
    def __init__(self, _from="", _to="", body=""):
        self._from = _from
        self._to = _to
        self.body = body

    def get_sms_model(self, data: SendSMS.Body) -> None:
        self._from = data._from
        self._to = data.to
        self.body = data.body
