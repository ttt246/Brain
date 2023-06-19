"""message model to manage browsering questions as agent"""
from src.service.llm.base import Message


class MessageModel:
    def __init__(self, role: str, content: str):
        self.role = role
        self.content = content

    def to_json(self) -> Message:
        return {"role": self.role, "content": self.content}

    @classmethod
    def create_chat_message(cls, role: str, content: str) -> Message:
        """
        Create a chat message with the given role and content.

        Args:
        role (str): The role of the message sender, e.g., "system", "user", or "assistant".
        content (str): The content of the message.

        Returns:
        dict: A dictionary containing the role and content of the message.
        """
        return {"role": role, "content": content}
