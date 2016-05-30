import re
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.mongo import validate_collections_are_empty


def validate_cleanup_complete(host, timeout, polling):
    return validate_collections_are_empty(log_msg='validating cleanup 1/2...',
                                          host=host,
                                          timeout=timeout,
                                          polling=polling,
                                          collection_names_regex='(^scored___aggr_event__|^entity_event_.*_(daily|hourly))') and \
           validate_collections_are_empty(log_msg='validating cleanup 2/2...',
                                          host=host,
                                          find_query={'modelName': re.compile('^aggr', re.IGNORECASE)},
                                          timeout=timeout,
                                          polling=polling,
                                          collection_names_regex='^streaming_models$')
