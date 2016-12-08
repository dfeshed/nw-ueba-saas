import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from utils.mongo import validate_collections_are_empty
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.run import validate_by_polling
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils.mongo import get_collections_size


def validate_entities_synced(host, timeout, polling):
    return validate_collections_are_empty(log_msg='validating entities synced...',
                                          host=host,
                                          timeout=timeout,
                                          polling=polling,
                                          collection_names_regex='^entity_event_meta_data')


def validate_scored_aggr_synced(logger, host, num_of_scored_events, timeout, polling):
    logger.info('validating scored aggregations synced...')
    if validate_by_polling(logger=logger,
                           progress_cb=lambda: get_collections_size(host=host,
                                                                    collection_names_regex='^scored___aggr_event__'),
                           is_done_cb=lambda progress: progress == num_of_scored_events,
                           no_progress_timeout=timeout,
                           polling=polling):
        logger.info('OK')
        return True
    else:
        logger.error('FAIL')
        return False
