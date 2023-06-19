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
