import os
import sys
import json

import traceback
from firebase_admin import messaging, firestore

db = firestore.client()


def get_tokens():
    users_ref = db.collection("users")
    docs = users_ref.stream()
    registeration_tokens = []
    for doc in docs:
        registeration_tokens.append(doc.to_dict()["token"])
    return registeration_tokens


def exception_detail(e):
    error_class = e.__class__.__name__
    detail = e.args[0]
    cl, exc, tb = sys.exc_info()
    lastCallStack = traceback.extract_tb(tb)[-1]
    fileName = lastCallStack[0]
    lineNum = lastCallStack[1]
    funcName = lastCallStack[2]
    errMsg = 'File "{}", line {}, in {}: [{}] {}'.format(
        fileName, lineNum, funcName, error_class, detail
    )
    return errMsg


def send_message(notification, token_list):
    if token_list == []:
        return False, "token_list empty"
    if notification.get("title") not in [None, ""]:
        notify = messaging.Notification(
            title=notification.get("title"), body=notification.get("content", "")
        )
        android_notify = messaging.AndroidNotification(
            title=notification.get("title"),
            body=notification.get("content", ""),
            default_sound=True,
        )
    else:
        notify = messaging.Notification(body=notification.get("content", ""))
        android_notify = messaging.AndroidNotification(
            body=notification.get("content", ""), default_sound=True
        )

    multi_msg = messaging.MulticastMessage(
        notification=notify,
        tokens=token_list,
        data={} if "route" not in notification else {"direct": notification["route"]},
        android=messaging.AndroidConfig(notification=android_notify, priority="high"),
        apns=messaging.APNSConfig(
            payload=messaging.APNSPayload(
                messaging.Aps(sound=messaging.CriticalSound("default", volume=1.0))
            )
        ),
    )
    response = messaging.send_multicast(multi_msg)
    failed_tokens = []
    if response.failure_count > 0:
        responses = response.responses
        for idx, resp in enumerate(responses):
            if not resp.success:
                # The order of responses corresponds to the order of the registration tokens.
                failed_tokens.append(token_list[idx])
        print("List of tokens that caused failures: {0}".format(failed_tokens))
    return True, "send to {} devices, with {} successed, with {} failed.".format(
        len(token_list), response.success_count, response.failure_count
    )
