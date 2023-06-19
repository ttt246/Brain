"""Basic Exception in Brain"""
from typing import Any

from Brain.src.common.http_response_codes import responses


class BrainException(Exception):
    def __init__(self, message: str = "Exception occurred in brain"):
        self.message = message
        super().__init__(self.message)

    def get_response_exp(self) -> Any:
        responses[506] = ("Brain Exception", self.message)
        return {"message": responses[506], "result": "", "status_code": 506}
