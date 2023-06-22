"""lLMs which we offer"""
from typing import Any

from Brain.src.common.brain_exception import BrainException
from Brain.src.model.req_model import ReqModel
from Brain.src.model.requests.request_model import BasicReq
from Brain.src.rising_plugin.llm.falcon_llm import FalconLLM
from Brain.src.rising_plugin.llm.gpt_llm import GptLLM

GPT_3_5_TURBO = "gpt-3.5-turbo"
GPT_4 = "gpt-4"
GPT_4_32K = "gpt-4-32k"
FALCON_7B = "falcon-7b"

"""list of available model we offer you"""
LLM_MODELS = [GPT_3_5_TURBO, GPT_4, GPT_4_32K, FALCON_7B]
GPT_LLM_MODELS = [GPT_3_5_TURBO, GPT_4, GPT_4_32K]


"""exception message"""
EXCEPTION_MSG = f"The model is not correct. It should be in {LLM_MODELS}"

"""validate model"""


def validate_model(model: str) -> bool:
    if model in LLM_MODELS:
        return True
    return False


"""
Args
model: model name of LLM such as 'gpt-3.5-turbo' | 'falcon-7b'
Returns
datatype: LLmChain  
"""


def get_llm_chain(
    setting: ReqModel, model: str, temperature: float = 0.6, max_new_tokens: int = 2000
) -> Any:
    if not validate_model(model):
        raise BrainException(EXCEPTION_MSG)
    """check model"""
    llm = get_llm(
        model=model,
        temperature=temperature,
        max_new_tokens=max_new_tokens,
        setting=setting,
    )

    return llm.get_chain()


def get_llm(
    setting: ReqModel, model: str, temperature: float = 0.6, max_new_tokens: int = 2000
) -> Any:
    if not validate_model(model):
        raise BrainException(EXCEPTION_MSG)
    """check model"""
    llm = GptLLM(openai_key=setting.openai_key)
    if model == GPT_3_5_TURBO or model == GPT_4 or model == GPT_4_32K:
        llm = GptLLM(model=model, openai_key=setting.openai_key)
    elif model == FALCON_7B:
        llm = FalconLLM(temperature=temperature, max_new_tokens=max_new_tokens)
    return llm
