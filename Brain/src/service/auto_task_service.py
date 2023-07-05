"""auto task management to get the expected output"""
import firebase_admin

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.llm.autogpt_llm import AutoGPTLLM
import time
import threading


class AutoTaskService:
    """self task achievement with autogpt based on langchain
    response -> reference_link :str"""

    def ask_task_with_llm(
        self, query: str, firebase_app: firebase_admin.App, setting: ReqModel
    ) -> str:
        # init autogpt llm
        autogpt_llm = AutoGPTLLM()

        # generate reference link
        reference_link = self.generate_reference_link(
            llm_name="autogpt", uuid=setting.uuid
        )
        # call autogpt
        thread = threading.Thread(
            target=autogpt_llm.ask_task, args=(query, firebase_app, reference_link)
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
        return f"/auto/{llm_name}_{uuid}_{milliseconds}"
