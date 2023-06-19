import json
from typing import Any, Callable, Optional

RISINGBRAIN_COMMAND_IDENTIFIER = "risingbrain_command"


class Command:
    """A class representing a command.

    Attributes:
        name (str): The name of the command.
        description (str): A brief description of what the command does.
    """

    def __init__(
        self,
        name: str,
        description: str,
        prompt: str,
        tags: Any,
        enabled: bool = True,
    ):
        self.name = name
        self.description = description
        self.prompt = prompt
        self.tags = tags
        self.enabled = enabled

    def __call__(self, *args, **kwargs) -> Any:
        if not self.enabled:
            return f"Command '{self.name}' is disabled: {self.disabled_reason}"
        return self.method(*args, **kwargs)

    def __str__(self) -> str:
        return f"'name': {self.name}, 'description': {self.description}"

    def __str_json__(self) -> str:
        return json.dumps(
            {
                "name": self.name,
                "description": self.description,
                "prompt": self.prompt,
                "tags": self.tags,
            }
        )


class CommandRegistry:
    """
    The CommandRegistry class is a manager for a collection of Command objects.
    It allows the registration, modification, and retrieval of Command objects,
    as well as the scanning and loading of command plugins from a specified
    directory.
    """

    def __init__(self):
        """this is default commands for now"""
        self.commands = [
            Command(
                "image",
                "image description",
                "Search a image that #description",
                ["#description"],
                True,
            ).__str_json__(),
            Command(
                "notification",
                "send notification or alert",
                "send that #notification",
                ["#notification"],
                True,
            ).__str_json__(),
            Command(
                "sms",
                "send a sms",
                "",
                [],
                True,
            ).__str_json__(),
            Command(
                "browsing",
                "search browser",
                "Search something that #description",
                ["#description"],
                True,
            ).__str_json__(),
            Command(
                "social",
                "search something in social",
                "Search something in twitter or facebook that #description",
                ["#description"],
                True,
            ).__str_json__(),
        ]

    def get_all_commands(self) -> Any:
        return self.commands

    def add_command(self, command: Command):
        self.commands.append(command)
