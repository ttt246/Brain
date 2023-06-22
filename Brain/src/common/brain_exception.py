"""Basic Exception in Brain"""
from typing import Any

from Brain.src.common.http_response_codes import responses


class BrainException(Exception):
    JSON_PARSING_ISSUE_MSG = "Exception occurred in json paring."

    def __init__(self, message: str = "Exception occurred in brain", code: int = 506):
        self.message = message
        self.code = code
        super().__init__(self.message)

    def get_response_exp(self) -> Any:
        responses[self.code] = ("Brain Exception", self.message)
        return {"message": responses[self.code], "result": "", "status_code": self.code}
