"""Program Type for all commands to categorize"""


class ProgramType:
    BROWSER = "browser"
    ALERT = "alert"
    IMAGE = "image"
    SMS = "sms"
    CONTACT = "contact"
    MESSAGE = "message"
    AUTO_TASK = "autotask"
    RAILS_OFF_TOPIC = "rails_off_topic"

    class BrowserType:
        OPEN_TAB = "opentab"
        OPEN_TAB_SEARCH = "opentabsearch"
        CLOSE_TAB = "closetab"
        PREVIOUS_PAGE = "previouspage"
        NEXT_PAGE = "nextpage"
        SCROLL_UP = "scrollup"
        SCROLL_DOWN = "scrolldown"
        SCROLL_TOP = "scrolltop"
        SCROLL_BOTTOM = "scrollbottom"
        SELECT_ITEM_DETAIL_INFO = "selectitemdetailinfo"
        SELECT_ITEM = "selectitem"
        MESSAGE = "message"
        ASK_WEBSITE = "askwebsite"
