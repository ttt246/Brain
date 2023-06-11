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

import os
import json
import numpy as np

from langchain.chat_models import ChatOpenAI
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import utils
from langchain.document_loaders.csv_loader import CSVLoader
from langchain.chains.question_answering import load_qa_chain
from langchain.docstore.document import Document

from src.common.utils import (
    OPENAI_API_KEY,
    COMMAND_SMS_INDEXS,
    COMMAND_BROWSER_OPEN,
)
from src.rising_plugin.image_embedding import (
    query_image_text,
)

from nemoguardrails.actions import action


@action()
async def general_question(query, model, uuid, image_search):
    llm = ChatOpenAI(model_name=model, temperature=0, openai_api_key=OPENAI_API_KEY)
    chain = load_qa_chain(llm, chain_type="stuff")
    file_path = os.path.dirname(os.path.abspath(__file__))

    with open(f"{file_path}/phone.json", "r") as infile:
        data = json.load(infile)
    embeddings = OpenAIEmbeddings(openai_api_key=OPENAI_API_KEY)

    query_result = embeddings.embed_query(query)
    doc_list = utils.maximal_marginal_relevance(np.array(query_result), data, k=1)
    loader = CSVLoader(file_path=f"{file_path}/phone.csv", encoding="utf8")
    csv_text = loader.load()

    docs = []

    for res in doc_list:
        docs.append(
            Document(
                page_content=csv_text[res].page_content, metadata=csv_text[res].metadata
            )
        )

    chain_data = chain.run(input_documents=docs, question=query)
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
        return str(result)
    except ValueError as e:
        # Check sms and browser query
        if doc_list[0] in COMMAND_SMS_INDEXS:
            return str({"program": "sms", "content": chain_data})
        elif doc_list[0] in COMMAND_BROWSER_OPEN:
            return str({"program": "browser", "content": "https://google.com"})
        return str({"program": "message", "content": chain_data})
