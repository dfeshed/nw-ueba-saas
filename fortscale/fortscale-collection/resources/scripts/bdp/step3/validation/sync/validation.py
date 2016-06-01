import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.mongo import validate_collections_are_empty


def validate_entities_synced(host, timeout, polling):
    return validate_collections_are_empty(log_msg='validating entities synced...',
                                          host=host,
                                          timeout=timeout,
                                          polling=polling,
                                          collection_names_regex='^entity_event_meta_data')
