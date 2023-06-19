from fastapi import APIRouter, Request, Depends

from src.common.assembler import Assembler
from src.common.program_type import ProgramType
from src.common.utils import parseUrlFromStr
from src.model.requests.request_model import BrowserItem
from src.service.browser_service import BrowserService

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
        item_link = ""
        try:
            token = data.token
            uuid = data.uuid

            # parsing contacts
            # train contact
            item_link = browser_service.query_item(items=data.items, query=data.prompt)
        except Exception as e:
            return assembler.to_response(400, "Failed to get item in a browser", "")
        return assembler.to_response(
            200,
            "Getting an item in a browser successfully",
            assembler.to_result_format(
                ProgramType.BrowserType.SELECT_ITEM,
                parseUrlFromStr(item_link),
            ),
        )

    return router
