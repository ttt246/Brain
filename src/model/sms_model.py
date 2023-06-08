"""sms message model includes from, to and body"""
from typing import Any


class SMSModel:
    def __init__(self, _from="", _to="", body=""):
        self._from = _from
        self._to = _to
        self.body = body

    def get_sms_model(self, data: Any) -> None:
        self._from = data["from"]
        self._to = data["to"]
        self.body = data["body"]
