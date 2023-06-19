"""service to manage command"""
from typing import Any

from src.commands.command import CommandRegistry


class CommandService:
    """get commands"""

    def __init__(self):
        self.command_registry = CommandRegistry()

    def get(self) -> Any:
        return self.command_registry.get_all_commands()
