from langchain.embeddings.openai import OpenAIEmbeddings

from ..common.utils import OPENAI_API_KEY, PINECONE_NAMESPACE, PINECONE_INDEX_NAME
from .pinecone_engine import (
    init_pinecone,
    get_pinecone_index_namespace,
)


def get_embeddings():
    return OpenAIEmbeddings(openai_api_key=OPENAI_API_KEY)


def embed_image_text(image_text, image_name, uuid):
    prompt_template = f"""
        This is the text about the image.
        ###
        {image_text}
        """

    embed_image_text = get_embeddings().embed_query(prompt_template)
    index = init_pinecone(PINECONE_INDEX_NAME)

    upsert_response = index.upsert(
        vectors=[{"id": image_name, "values": embed_image_text}],
        namespace=get_pinecone_index_namespace(uuid),
    )

    if upsert_response == 0:
        return "fail to embed image text"

    return "success to embed image text"


def query_image_text(image_content, message, uuid):
    embed_image_text = get_embeddings().embed_query(
        get_prompt_image_with_message(image_content, message)
    )
    index = init_pinecone(PINECONE_INDEX_NAME)
    relatedness_data = index.query(
        vector=embed_image_text,
        top_k=3,
        include_values=False,
        namespace=get_pinecone_index_namespace(uuid),
    )
    if len(relatedness_data["matches"]) > 0:
        return relatedness_data["matches"][0]["id"]
    return ""


def get_prompt_image_with_message(image_content, message):
    prompt_template = f"""
                This is the text about the image.
                ###
                {image_content}
                ###
                This message is the detailed description of the image.
                ### 
                {message}
                """

    return prompt_template
