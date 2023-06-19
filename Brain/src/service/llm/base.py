from typing import TypedDict


class Message(TypedDict):
    """OpenAI Message object containing a role and the message content"""

    role: str
    content: str
