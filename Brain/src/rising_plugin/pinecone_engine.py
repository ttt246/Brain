# initialize pinecone
import pinecone
from typing import Any

from ..common.brain_exception import BrainException
from ..common.http_response_codes import responses
from ..common.utils import (
    PINECONE_INDEX_NAME,
    PINECONE_NAMESPACE,
)
from ..model.req_model import ReqModel

DIMENSION = 1536
METRIC = "cosine"
POD_TYPE = "p1.x1"


# get the existing index in pinecone or create a new one
def init_pinecone(index_name, setting: ReqModel, flag=True):
    try:
        pinecone.init(api_key=setting.pinecone_key, environment=setting.pinecone_env)
        if flag:
            return pinecone.Index(index_name)
        else:
            # create a new index in pinecone
            return pinecone.create_index(
                index_name, dimension=DIMENSION, metric=METRIC, pod_type=POD_TYPE
            )
    except Exception as ex:
        raise BrainException(code=508, message=responses[508])


"""add item in pinecone"""


def add_pinecone(
    namespace: str, key: str, setting: ReqModel, value: list[float]
) -> Any:
    index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=setting)

    upsert_response = index.upsert(
        vectors=[{"id": key, "values": value}],
        namespace=namespace,
    )
    return upsert_response


"""update item in pinecone"""


def update_pinecone(
    setting: ReqModel, namespace: str, key: str, value: list[float]
) -> Any:
    index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=setting)

    upsert_response = index.update(
        id=key,
        values=value,
        namespace=namespace,
    )
    return upsert_response


"""delete item in pinecone"""


def delete_pinecone(setting: ReqModel, namespace: str, key: str) -> Any:
    index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=setting)
    delete_response = index.delete(ids=[key], namespace=namespace)
    return delete_response


"""delete all item in the namespace"""


def delete_all_pinecone(setting: ReqModel, namespace: str) -> Any:
    index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=setting)
    delete_response = index.delete(delete_all=True, namespace=namespace)
    return delete_response


"""generate index name of pinecone"""


def get_pinecone_index_name(uuid):
    return PINECONE_INDEX_NAME + "-" + uuid


"""generate a namespace of pinecone"""


def get_pinecone_index_namespace(uuid):
    return PINECONE_NAMESPACE + "-" + uuid
