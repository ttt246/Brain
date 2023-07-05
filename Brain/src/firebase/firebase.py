import json
import os
from typing import Any

import firebase_admin
from firebase_admin import db
from firebase_admin import credentials

from Brain.src.common.assembler import Assembler
from Brain.src.common.brain_exception import BrainException
from Brain.src.common.http_response_codes import responses
from Brain.src.common.utils import FIREBASE_STORAGE_BUCKET, FIREBASE_REALTIME_DATABASE
from Brain.src.logs import logger
from Brain.src.model.req_model import ReqModel
from Brain.src.model.requests.request_model import BasicReq


def initialize_app(setting: ReqModel) -> firebase_admin.App:
    app_name = get_firebase_admin_name(setting.uuid)
    # Check if the app is already initialized
    try:
        app = firebase_admin.get_app(app_name)
        return app
        # if app is not None:
        #     # Delete the existing app
        #     firebase_admin.delete_app(app)
    except Exception as ex:
        logger.warn(
            title="firebase init",
            message=f"this app name: {app_name} does not exist",
        )
    return firebase_admin.initialize_app(
        get_firebase_cred(setting),
        {
            "storageBucket": FIREBASE_STORAGE_BUCKET,
            "databaseURL": FIREBASE_REALTIME_DATABASE,
        },
        name=app_name,
    )


def get_firebase_admin_name(uuid: str = ""):
    return f"firebase_admin_{uuid}"


def firebase_admin_with_setting(data: BasicReq):
    # firebase admin init
    assembler = Assembler()
    setting = assembler.to_req_model(data.confs)
    try:
        firebase_app = initialize_app(setting)
    except Exception as ex:
        raise BrainException(code=507, message=responses[507])
    return setting, firebase_app


def get_firebase_cred(setting: ReqModel):
    if os.path.exists("Brain/firebase_cred.json"):
        file = open("Brain/firebase_cred.json")
        cred = json.load(file)
        file.close()
        return credentials.Certificate(cred)
    else:
        cred = json.loads(setting.firebase_key)
        return credentials.Certificate(cred)


"""
delete data from real time database of firebase using reference link    
"""


def delete_data_from_realtime(
    reference_link: str, firebase_app: firebase_admin.App
) -> None:
    ref = db.reference(reference_link, app=firebase_app)
    ref.delete()
