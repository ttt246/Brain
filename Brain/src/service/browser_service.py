"""service to handle & process the browser"""
from Brain.src.model.requests.request_model import BrowserItem
from Brain.src.rising_plugin.risingplugin import getCompletionOnly


class BrowserService:
    """query to get the link of the item from the list"""

    def query_item(self, items: list[BrowserItem.ItemReq], query: str) -> str:
        prompt_template = f"""
        User is trying to '{query}' and it includes the title of the item.
        Please return the link of best relatedness of the item with the title from the below data.\n {items}"""
        return getCompletionOnly(query=prompt_template)

    def query_ask(self, items: list[str], query: str) -> str:
        prompt_template = f"""
        User is asking question related to website that users visit. The following is the user's question.
        '{query}'.\n
        Also the below data is list of sentence in the website.
        '{items}' \n
        Please provide me the proper answer.\n """
        return getCompletionOnly(query=prompt_template)
