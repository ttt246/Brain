"""service to manage contacts"""
from typing import List, Any


from Brain.src.common.assembler import Assembler
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

import firebase_admin
from firebase_admin import firestore


class ContactsService:
    db: Any
    phones_ref: Any

    def __init__(self, firebase_app: firebase_admin.App, setting: ReqModel):
        self.firebase_app = firebase_app
        self.setting = setting
        self.init_firestore()

    """train contacts (getting embedding) and update pinecone with embeddings by contact_id
    train datatype:
    key: contactId
    values: {displayName},{phoneNumber1}, {phoneNumber2}"""

    def train(self, uuid: str, contacts: List[ContactModel]) -> None:
        # getting index namespaceof pinecone
        pinecone_namespace = self.get_pinecone_index_namespace(uuid)

        for contact in contacts:
            # Save the data
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
                self.create_one_contact(uuid=uuid, contact=contact)
            elif contact.status == ContactStatus.DELETED:
                delete_pinecone(
                    namespace=pinecone_namespace,
                    key=key,
                    setting=self.setting,
                )
                self.delete_one_contact(uuid=uuid, contact=contact)
            elif contact.status == ContactStatus.UPDATED:
                update_pinecone(
                    namespace=pinecone_namespace,
                    key=key,
                    value=vectoring_values,
                    setting=self.setting,
                )
                self.update_one_contact(uuid=uuid, contact=contact)

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

    """init firestore to save user's contacts"""

    def init_firestore(self):
        self.db = firestore.client(app=self.firebase_app)
        self.phones_ref = self.db.collection("phones")

    """create a contact into document which name is uuid in phone collections in firestore"""

    def create_one_contact(self, uuid: str, contact: ContactModel):
        assembler = Assembler()
        data = assembler.to_contact_result_format(contact)
        phones_doc_ref = self.phones_ref.document(uuid)
        contacts_doc_ref = phones_doc_ref.collection("contacts").document(
            contact.contact_id
        )
        contacts_doc_ref.set(data)

    """update a contact into document which name is uuid in phone collections in firestore"""

    def update_one_contact(self, uuid: str, contact: ContactModel):
        assembler = Assembler()
        data = assembler.to_contact_result_format(contact)
        phones_doc_ref = self.phones_ref.document(uuid)
        contacts_doc_ref = phones_doc_ref.collection("contacts").document(
            contact.contact_id
        )
        contacts_doc_ref.update(data)

    """delete a contact into document which name is uuid in phone collections in firestore"""

    def delete_one_contact(self, uuid: str, contact: ContactModel):
        phones_doc_ref = self.phones_ref.document(uuid)
        contacts_doc_ref = phones_doc_ref.collection("contacts").document(
            contact.contact_id
        )
        contacts_doc_ref.delete()

    def get_contacts_by_ids(self, uuid: str, contactIds: list[str]) -> []:
        phones_doc_ref = self.phones_ref.document(uuid)
        contacts_ref = phones_doc_ref.collection("contacts")

        # Retrieve all documents in the 'contacts' sub-collection
        contacts = contacts_ref.where("contactId", "in", contactIds).stream()

        result = []
        # Iterate through the documents and print out their data
        for contact in contacts:
            result.append(contact.to_dict())

        return result
