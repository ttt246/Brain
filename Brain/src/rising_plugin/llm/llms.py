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

"""maximum auto achievement counter"""
MAX_AUTO_THINKING = 10

"""prompt"""

EMAIL_PROMPT = """
If user is going to read emails, please answer belowing json format. {"program": "reademails", "content": "if user is going to read email in inbox, set to inbox, if not, set to draft. if user did not provide where to read email, set to inbox"}
If user is going to send email to other user, or save email to draft folder, please answer belowing json format. {"program": "sendemail", "content": {"type": "if user is going to send email to other user, set to true, if not, set to false. if user did not provide where to send email, set to true", "title": "make the proper title for email content", "body": "email content that user is going to send"}}
"""

IMAGE_PROMPT = """
If user is going to say about a image with its description to search, please answer belowing json format. {"program": "image", "content": "description of the image that user is going to search"}
If user is going to ask about a image, please answer belowing json format. {"program": "image", "content": "description of the image that user is going to search"}
"""


AUTO_TASK_PROMPT = """
User wants to help organization in achieving its goals, which bridges the gap between the user is (present) and where he/she wants to go (future), or that is deciding in advance what to do, how to do when to do it and by whom it is to be done. Also the description ensures in thoughts and actions, work is carried on smoothly without any confusion and misunderstanding. \n And it can be done for the future and the future is full of uncertainties. \n So it looks like to make a decision as well. It helps make rational decisions by choosing the best most profitable alternative which may bring lower cost, adaptable to the organization and situations. \n If user is going to say about planning strategy, or something like that , please answer belowing json format. {"program": "autotask", "content": ""}
If users are going to know about  real-time capabilities such as News, Weather, Stocks, Booking, Planning, or etc, then please answer belowing json format. {"program": "autotask", "content": ""}
If your answer is not correct with the program type which mentioned in this rules, please answer belowing json format. {"program": "autotask", "content": ""}
"""

NO_MATCH_PROMPT = """
If all of above is not correct, please give the most appropriate answer to the user's question. Please answer belowing json format. {"program":"message", "content":"your answer"}
"""

MOBILE_PROMPT = (
    """
If user said that send sms or text, please answer belowing json format. {"program": "sms", "content": "ask who"}
If user said that compose, write, or create an sms message, please answer belowing json format. {"program": "sms", "content": "ask who"}
If user said that search contact with its description such as display name or phone number, please answer belowing json format. {"program": "contact", "content": "description of the contact that user is going to search"}
If user said that launch a browser or open a browser, please answer belowing json format. {"program": "browser", "content": "https://google.com"}
If user said that open a website using web browsers, please answer belowing json format. The url user is going to open can exist or not. If user doesn\\'t say exact url and want to open some sites, you have to find the best proper url. If user didn\\'t say any url and you can't find proper url, please set website url to "https://www.google.com". {"program": "browser", "content": "website url that user is going to open"}
If user said that open a browser such as chrome, firefox or safari and search something, please answer belowing json format. The url user is going to open can exist or not. If user doesn\\'t say exact url and want to open some sites, you have to find the best proper url. If user didn\\'t say any url and you can't find proper url, please set website url to "https://www.google.com". {"program": "browser", "content": "website url that user is going to open"}
If user is going to set or create alarm with time and label, please answer belowing json format.\n {"program": "alarm", "content": {"type":"create", "time":"please set time as 24-hours format that user is going to set. If user did not provide any alarm time, set "0:0"", "label":"please set label that user is going to set. If user did not provide any label, set "alarm""}}\n This is example data.\n User: Set an alarm.\n AI: {"program":"alarm", "content": {"type":"create", "time":"0:0", "label":"alarm"}}\n User: Set an alarm with label as "wake up".\n AI: {"program":"alarm", "content": {"type":"create", "time":"0:0", "label":"wake up"}}\n User: Set an alarm for 5:00 AM.\n AI: {"program":"alarm", "content": {"type":"create", "time":"5:00", "label":"alarm"}}\n User: Set an alarm for 5:00 PM with label as "wake up".\n AI: {"program":"alarm", "content": {"type":"create", "time":"17:00", "label":"wake up"}}
"""
    + EMAIL_PROMPT
    + IMAGE_PROMPT
    + AUTO_TASK_PROMPT
    + NO_MATCH_PROMPT
)


EXTENSION_PROMPT = (
    """
If user said that search contact with its description such as display name or phone number, please answer belowing json format. {"program": "contact", "content": "description of the contact that user is going to search"}
If user said that open a tab, go to a tab, or open a page, please answer belowing json format. {"program": "opentab", "content": ""}
If user said that open a tab and search, go to a tab and search, or open a page and search, please answer belowing json format. {"program": "opentabsearch", "content": "keyword that user is going to search"}
If user said that close a tab, please answer belowing json format. {"program": "closetab", "content": ""}
If user said that go to a previous page, or open a previous page, please answer belowing json format. {"program": "previouspage", "content": ""}
If user said that go to a next page, or open a next page, please answer belowing json format. {"program": "nextpage", "content": ""}
If user said that scroll up, scroll up page, or page scroll up, please answer belowing json format. {"program": "scrollup", "content": ""}
If user said that scroll down, scroll down page, page scroll down, please answer belowing json format. {"program": "scrolldown", "content": ""}
If user said that scroll top, scroll top page, or scroll top of page, please answer belowing json format. {"program": "scrolltop", "content": ""}
If user said that scroll bottom, scroll bottom page, or scroll bottom of page, please answer belowing json format. {"program": "scrollbottom", "content": ""}
If user is going to select an item, an article or a website with its description in a web browser, please answer belowing json format. {"program": "selectitemdetailinfo", "content": "the description of an item, an article or a website in a browser"}
If user said that ask about the content in website, for example, if users ask something like 'what is #query in this website?' or 'Can you tell me about #query based on this website?', please answer belowing json format. {"program": "askwebsite", "content": ""}
If users are going to ask something based on the data of website, please answer belowing json format. {"program": "askwebsite", "content": ""}
"""
    + EMAIL_PROMPT
    + AUTO_TASK_PROMPT
    + NO_MATCH_PROMPT
)

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
        llm = GptLLM(
            model=model, openai_key=setting.openai_key, temperature=temperature
        )
    elif model == FALCON_7B:
        llm = FalconLLM(temperature=temperature, max_new_tokens=max_new_tokens)
    return llm


"""
generate finish command and response for auto achievement
"""


def get_finish_command_for_auto_task() -> Any:
    return {
        "command": {
            "args": {"response": "I have finished all my objectives."},
            "name": "finish",
        }
    }
