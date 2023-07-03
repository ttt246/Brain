"""validate rails result:
checking with program whether is it message or rails_off_topic"""
import json

from Brain.src.common.program_type import ProgramType


def validate_rails(data: str) -> bool:
    try:
        json_obj = json.loads(data["content"])
        if json_obj["program"] == ProgramType.RAILS_OFF_TOPIC:
            return False
        return True
    except Exception as ex:
        return False
