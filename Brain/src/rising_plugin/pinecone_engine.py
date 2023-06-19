# initialize pinecone
import pinecone
from typing import Any
from ..common.utils import (
    PINECONE_KEY,
    PINECONE_ENV,
    PINECONE_INDEX_NAME,
    PINECONE_NAMESPACE,
)

DIMENSION = 1536
METRIC = "cosine"
POD_TYPE = "p1.x1"


# get the existing index in pinecone or create a new one
def init_pinecone(index_name, flag=True):
    pinecone.init(api_key=PINECONE_KEY, environment=PINECONE_ENV)
    if flag:
        return pinecone.Index(index_name)
    else:
        # create a new index in pinecone
        return pinecone.create_index(
            index_name, dimension=DIMENSION, metric=METRIC, pod_type=POD_TYPE
        )


"""add item in pinecone"""


def add_pinecone(namespace: str, key: str, value: list[float]) -> Any:
    index = init_pinecone(PINECONE_INDEX_NAME)

    upsert_response = index.upsert(
        vectors=[{"id": key, "values": value}],
        namespace=namespace,
    )
    return upsert_response


"""update item in pinecone"""


def update_pinecone(namespace: str, key: str, value: list[float]) -> Any:
    index = init_pinecone(PINECONE_INDEX_NAME)

    upsert_response = index.update(
        id=key,
        values=value,
        namespace=namespace,
    )
    return upsert_response


"""delete item in pinecone"""


def delete_pinecone(namespace: str, key: str) -> Any:
    index = init_pinecone(PINECONE_INDEX_NAME)
    delete_response = index.delete(ids=[key], namespace=namespace)
    return delete_response


"""delete all item in the namespace"""


def delete_all_pinecone(namespace: str) -> Any:
    index = init_pinecone(PINECONE_INDEX_NAME)
    delete_response = index.delete(delete_all=True, namespace=namespace)
    return delete_response


"""generate index name of pinecone"""


def get_pinecone_index_name(uuid):
    return PINECONE_INDEX_NAME + "-" + uuid


"""generate a namespace of pinecone"""


def get_pinecone_index_namespace(uuid):
    return PINECONE_NAMESPACE + "-" + uuid
