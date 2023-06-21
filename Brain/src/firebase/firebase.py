from typing import Any

import firebase_admin

from Brain.src.common.assembler import Assembler
from Brain.src.common.utils import get_firebase_cred, FIREBASE_STORAGE_BUCKET
from Brain.src.model.req_model import ReqModel
from Brain.src.model.requests.request_model import BasicReq


def initialize_app(setting: ReqModel) -> firebase_admin.App:
    return firebase_admin.initialize_app(
        get_firebase_cred(setting),
        {"storageBucket": FIREBASE_STORAGE_BUCKET},
        name=get_firebase_admin_name(setting.uuid),
    )


def get_firebase_admin_name(uuid: str = ""):
    return f"firebase_admin_{uuid}"


def firebase_admin_with_setting(data: BasicReq):
    # firebase admin init
    assembler = Assembler()
    setting = assembler.to_req_model(data)

    firebase_app = initialize_app(setting)
    return setting, firebase_app
