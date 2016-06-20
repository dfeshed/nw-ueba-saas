import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.run import validate_by_polling
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import time_utils
from automatic_config.common.utils.mongo import get_collections_size, get_collections_time_boundary

import logging

logger = logging.getLogger('step4.validation')

_ENTITY_EVENT_COLLECTIONS_REGEX = '^entity_event_(?!meta_data_).'
_SCORED_ENTITY_EVENT_COLLECTIONS_REGEX = '^scored___entity_event_'


def validate_no_missing_events(host, timeout, polling, start=None, end=None):
    start = start or get_collections_time_boundary(host=host,
                                                   collection_names_regex=_ENTITY_EVENT_COLLECTIONS_REGEX,
                                                   is_start=True)
    end = end or get_collections_time_boundary(host=host,
                                               collection_names_regex=_ENTITY_EVENT_COLLECTIONS_REGEX,
                                               is_start=False)
    logger.info('validating that there are no missing events...')
    if validate_by_polling(logger=logger,
                           progress_cb=lambda: _validate(host=host, start=start, end=end),
                           is_done_cb=lambda progress: progress[0] == progress[1],
                           no_progress_timeout=timeout,
                           polling=polling):
        logger.info('OK')
        return True
    else:
        logger.error('FAILED')
        return False


def _validate(host, start, end):
    time_interval = {
        '$gte': time_utils.get_epochtime(start),
        '$lt': time_utils.get_epochtime(end)
    }
    entity_events_counter = get_collections_size(host=host,
                                                 collection_names_regex=_ENTITY_EVENT_COLLECTIONS_REGEX,
                                                 find_query={'endTime': time_interval})
    scored_entity_events_counter = get_collections_size(host=host,
                                                        collection_names_regex=_SCORED_ENTITY_EVENT_COLLECTIONS_REGEX,
                                                        find_query={'end_time_unix': time_interval})
    logger.info(str(entity_events_counter) + ' entity events, ' + str(scored_entity_events_counter) + ' scored entity events')
    return scored_entity_events_counter, entity_events_counter
