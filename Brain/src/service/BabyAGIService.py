"""BabyAGI Service Interface"""

import firebase_admin

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.llm.babyagi_llm import BabyAGILLM
import time
import threading


class BabyAGIService:

    """
    self task achievement with babyagi based on langchain
    response -> reference_link :str
    """

    def ask_task_with_llm(
        self, query: str, firebase_app: firebase_admin.App, setting: ReqModel
    ) -> str:
        # init autogpt llm
        babyagi_llm = BabyAGILLM()

        # generate reference link
        reference_link = self.generate_reference_link(
            llm_name="babyagi", uuid=setting.uuid
        )
        # call autogpt
        thread = threading.Thread(
            target=babyagi_llm.ask_task, args=(query, firebase_app, reference_link)
        )
        thread.start()

        return reference_link

    """
    generate reference link for autoTask
    response type:
    /auto/{llm_name}_{uuid}_{timestamp}
    """

    def generate_reference_link(self, llm_name: str, uuid: str) -> str:
        milliseconds = int(time.time() * 1000)
        return f"/babyagi/{llm_name}_{uuid}_{milliseconds}"
