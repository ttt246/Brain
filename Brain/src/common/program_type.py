"""Program Type for all commands to categorize"""


class ProgramType:
    BROWSER = "browser"
    ALERT = "alert"
    IMAGE = "image"
    SMS = "sms"
    CONTACT = "contact"
    MESSAGE = "message"

    class BrowserType:
        OPEN_TAB = "open_tab"
        OPEN_TAB_SEARCH = "open_tab_search"
        CLOSE_TAB = "close_tab"
        PREVIOUS_PAGE = "previous_page"
        NEXT_PAGE = "next_page"
        SCROLL_UP = "scroll_up"
        SCROLL_DOWN = "scroll_down"
        SCROLL_TOP = "scroll_top"
        SCROLL_BOTTOM = "scroll_bottom"
        SELECT_ITEM_DETAIL_INFO = "select_item_detail_info"
        SELECT_ITEM = "select_item"
