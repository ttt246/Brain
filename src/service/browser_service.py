"""service to handle & process the browser"""
from src.model.requests.request_model import BrowserItem
from src.rising_plugin.risingplugin import getCompletionOnly


class BrowserService:
    """query to get the link of the item from the list"""

    def query_item(self, items: list[BrowserItem.ItemReq], query: str) -> str:
        prompt_template = f"Please return the link of best relatedness of item which the title is '{query}' from the below data.\n {items}"
        return getCompletionOnly(query=prompt_template)
