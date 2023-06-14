"""gpt-open ai llm"""
from typing import Any

from langchain.chat_models import ChatOpenAI
from langchain.chains.question_answering import load_qa_chain
from src.common.utils import (
    OPENAI_API_KEY,
)


class GptLLM:
    def __init__(self, model: str = "gpt-3.5-turbo", temperature: float = 0.6):
        self.llm = self.init_llm(model=model, temperature=temperature)

    def init_llm(self, model: str = "gpt-3.5-turbo", temperature: float = 0.6) -> Any:
        self.llm = ChatOpenAI(
            model_name=model, temperature=temperature, openai_api_key=OPENAI_API_KEY
        )
        return self.llm

    def get_llm(self):
        return self.llm

    def get_chain(self):
        chain = load_qa_chain(self.llm, chain_type="stuff")
        return chain
