"""auto task management to get the expected output"""
import firebase_admin

from Brain.src.model.req_model import ReqModel
from Brain.src.rising_plugin.llm.autogpt_llm import AutoGPTLLM
import time


class AutoTaskService:
    """self task archivement with autogpt based on langchain
    response -> reference_link :str"""

    def ask_task_with_autogpt(
        self, query: str, firebase_app: firebase_admin.App, setting: ReqModel
    ) -> str:
        # init autogpt llm
        autogpt_llm = AutoGPTLLM()

        # generate reference link
        reference_link = self.generate_reference_link(
            llm_name="autogpt", uuid=setting.uuid
        )
        # call autogpt
        autogpt_llm.ask_task(
            query=query, firebase_app=firebase_app, reference_link=reference_link
        )

        return reference_link

    """generate reference link for autoTask
    response type:
    /auto/{llm_name}_{uuid}_{timestamp}"""

    def generate_reference_link(self, llm_name: str, uuid: str) -> str:
        milliseconds = int(time.time() * 1000)
        return f"/auto/{llm_name}_{uuid}_{milliseconds}"
