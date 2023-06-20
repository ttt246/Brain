# SPDX-FileCopyrightText: Copyright (c) 2023 NVIDIA CORPORATION & AFFILIATES. All rights reserved.
# SPDX-License-Identifier: Apache-2.0
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import json

from langchain.embeddings.openai import OpenAIEmbeddings
from Brain.src.service.train_service import TrainService
from langchain.docstore.document import Document

from Brain.src.common.brain_exception import BrainException
from Brain.src.common.utils import (
    OPENAI_API_KEY,
    COMMAND_SMS_INDEXES,
    COMMAND_BROWSER_OPEN,
    PINECONE_INDEX_NAME,
)
from Brain.src.rising_plugin.image_embedding import (
    query_image_text,
)

from nemoguardrails.actions import action

from Brain.src.rising_plugin.llm.falcon_llm import FalconLLM
from Brain.src.rising_plugin.llm.gpt_llm import GptLLM
from Brain.src.rising_plugin.llm.llms import (
    get_llm_chain,
    GPT_3_5_TURBO,
    GPT_4_32K,
    GPT_4,
    FALCON_7B,
)

from Brain.src.rising_plugin.pinecone_engine import (
    get_pinecone_index_namespace,
    init_pinecone,
)


def get_pinecone_index_train_namespace() -> str:
    return get_pinecone_index_namespace(f"trains")


@action()
async def general_question(query):
    """step 0: convert string to json"""
    index = init_pinecone(PINECONE_INDEX_NAME)
    try:
        json_query = json.loads(query)
    except Exception as ex:
        raise BrainException(BrainException.JSON_PARSING_ISSUE_MSG)
    """step 0-->: parsing parms from the json query"""
    query = json_query["query"]
    model = json_query["model"]
    uuid = json_query["uuid"]
    image_search = json_query["image_search"]

    """step 1: handle with gpt-4"""

    embeddings = OpenAIEmbeddings(openai_api_key=OPENAI_API_KEY)

    query_result = embeddings.embed_query(query)
    relatedness_data = index.query(
        vector=query_result,
        top_k=3,
        include_values=False,
        namespace=get_pinecone_index_train_namespace(),
    )
    documentId = ""
    if len(relatedness_data["matches"]) > 0:
        documentId = relatedness_data["matches"][0]["id"]

    docs = []
    train_service = TrainService()
    documents = train_service.read_all_documents()
    for document in documents:
        if document["document_id"] == documentId:
            docs.append(Document(page_content=document["page_content"], metadata=""))

    chain_data = get_llm_chain(model=model).run(input_documents=docs, question=query)
    # test
    # if model == GPT_3_5_TURBO or model == GPT_4 or model == GPT_4_32K:
    #     gpt_llm = GptLLM(model=model)
    #     chain_data = gpt_llm.get_chain().run(input_documents=docs, question=query)
    # elif model == FALCON_7B:
    #     falcon_llm = FalconLLM()
    #     chain_data = falcon_llm.get_chain().run(question=query)
    falcon_llm = FalconLLM()
    try:
        result = json.loads(chain_data)
        # check image query with only its text
        if result["program"] == "image":
            if image_search:
                result["content"] = {
                    "image_name": query_image_text(result["content"], "", uuid)
                }

            # else:
            #     return result
        """check program is message to handle it with falcon llm"""
        if result["program"] == "message":
            result["content"] = falcon_llm.query(question=query)
        return str(result)
    except ValueError as e:
        # Check sms and browser query
        if documentId in COMMAND_SMS_INDEXES:
            return str({"program": "sms", "content": chain_data})
        elif documentId in COMMAND_BROWSER_OPEN:
            return str({"program": "browser", "content": "https://google.com"})
        return str({"program": "message", "content": falcon_llm.query(question=query)})
