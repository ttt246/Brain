"""service to manage contacts"""
from typing import List, Any

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.csv_embed import get_embed
from Brain.src.rising_plugin.pinecone_engine import (
    get_pinecone_index_namespace,
    update_pinecone,
    init_pinecone,
    delete_pinecone,
    add_pinecone,
    delete_all_pinecone,
)

from Brain.src.common.utils import PINECONE_INDEX_NAME
from Brain.src.model.contact_model import ContactModel, ContactStatus


class ContactsService:
    def __init__(self, setting: ReqModel):
        self.setting = setting

    """train contacts (getting embedding) and update pinecone with embeddings by contact_id
    train datatype:
    key: contactId
    values: {displayName},{phoneNumber1}, {phoneNumber2}"""

    def train(self, uuid: str, contacts: List[ContactModel]) -> None:
        # getting index namespaceof pinecone
        pinecone_namespace = self.get_pinecone_index_namespace(uuid)
        for contact in contacts:
            # generate key and values for pinecone data
            key = contact.contact_id
            value = f"{contact.display_name}, {contact.get_str_phones()}"
            # get vectoring data(embedding data)
            vectoring_values = get_embed(data=value, setting=self.setting)
            # create | update | delete pinecone
            if contact.status == ContactStatus.CREATED:
                add_pinecone(
                    namespace=pinecone_namespace,
                    key=key,
                    value=vectoring_values,
                    setting=self.setting,
                )
            elif contact.status == ContactStatus.DELETED:
                delete_pinecone(
                    namespace=pinecone_namespace,
                    key=key,
                    setting=self.setting,
                )
            elif contact.status == ContactStatus.UPDATED:
                update_pinecone(
                    namespace=pinecone_namespace,
                    key=key,
                    value=vectoring_values,
                    setting=self.setting,
                )

    """"query contact with search text
    response: list of contactId as index key of pinecone"""

    def query_contacts(self, uuid: str, search: str) -> List[str]:
        vector_data = get_embed(data=search, setting=self.setting)
        index = init_pinecone(index_name=PINECONE_INDEX_NAME, setting=self.setting)
        relatedness_data = index.query(
            vector=vector_data,
            top_k=5,
            include_values=False,
            namespace=self.get_pinecone_index_namespace(uuid),
        )
        result = []
        if len(relatedness_data["matches"]) > 0:
            for match_item in relatedness_data["matches"]:
                result.append(match_item["id"])
        return result

    """delete all items in the specific nanespace"""

    def delete_all(self, uuid: str) -> Any:
        return delete_all_pinecone(
            namespace=self.get_pinecone_index_namespace(uuid), setting=self.setting
        )

    """get pinecone namespace of pinecone"""

    def get_pinecone_index_namespace(self, uuid: str) -> str:
        return get_pinecone_index_namespace(f"{uuid}-contacts")
