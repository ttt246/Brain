from fastapi import APIRouter, Request, Depends

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.common.program_type import ProgramType
from Brain.src.common.utils import parseUrlFromStr
from Brain.src.firebase.firebase import firebase_admin_with_setting
from Brain.src.model.requests.request_model import BrowserItem
from Brain.src.model.requests.request_model import BrowserAsk
from Brain.src.service.browser_service import BrowserService

router = APIRouter()


def construct_blueprint_browser_api() -> APIRouter:
    # Assembler
    assembler = Assembler()
    # Services
    browser_service = BrowserService()
    """@generator.request_body(
        {
            "token": "String",
            "uuid": "String",
            "items":["title": "String", "link": "String"],
            "prompt":"String",
        }
    )
    @generator.response(
        status_code=200, schema={"message": "message", "result": "test_result"}
    )"""

    @router.post("/item")
    def get_item(data: BrowserItem):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        item_link = ""
        try:
            token = setting.token
            uuid = setting.uuid

            # parsing contacts
            # train contact
            item_link = browser_service.query_item(items=data.items, query=data.prompt)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to get item in a browser", "")
        return assembler.to_response(
            200,
            "Getting an item in a browser successfully",
            assembler.to_result_format(
                ProgramType.BrowserType.SELECT_ITEM,
                parseUrlFromStr(item_link),
            ),
        )

    @router.post("/ask")
    def get_item(data: BrowserAsk):
        # firebase admin init
        try:
            setting, firebase_app = firebase_admin_with_setting(data)
        except BrainException as ex:
            return ex.get_response_exp()

        try:
            # parsing contacts
            # train contact
            answer = browser_service.query_ask(items=data.items, query=data.prompt)
        except Exception as e:
            if isinstance(e, BrainException):
                return e.get_response_exp()
            return assembler.to_response(400, "Failed to get item in a browser", "")
        return assembler.to_response(
            200,
            "Getting an item in a browser successfully",
            assembler.to_result_format(
                ProgramType.BrowserType.MESSAGE,
                answer,
            ),
        )

    return router
