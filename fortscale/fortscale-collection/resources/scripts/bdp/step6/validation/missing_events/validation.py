import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.run import validate_by_polling
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils.mongo import get_collection_names, get_collections_size

import logging

logger = logging.getLogger('step6.validation')


_SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME = {
    'scored___entity_event__normalized_username_hourly': 'Suspicious Hourly User Activity',
    'scored___entity_event__normalized_username_daily': 'Suspicious Daily User Activity'
}


def validate_no_missing_events(host, timeout, polling_interval):
    for scored_entity_event_collection_name in get_collection_names(host=host,
                                                                    collection_names_regex='^scored___entity_event_'):
        logger.info('validating that there are no missing events for ' + scored_entity_event_collection_name + '...')
        if not validate_by_polling(logger=logger,
                                   progress_cb=lambda: _get_counts(host=host,
                                                                   scored_entity_event_collection_name=scored_entity_event_collection_name),
                                   is_done_cb=lambda counts: counts[0] == counts[1],
                                   no_progress_timeout=timeout,
                                   polling=polling_interval):
            return False
    return True


def _get_counts(host, scored_entity_event_collection_name):
    if scored_entity_event_collection_name not in _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME:
        raise Exception('This collection is not supported by the script. But fear not! '
                        'just update _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME')
    alerts_count = _count_alerts(host, scored_entity_event_collection_name)
    scored_entities_count = _count_scored_entities(host, scored_entity_event_collection_name)
    return alerts_count, scored_entities_count


def _count_alerts(host, scored_entity_event_collection_name):
    alerts_count = get_collections_size(host=host,
                                        collection_names_regex='^alerts$',
                                        find_query={
                                            'name': _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME[scored_entity_event_collection_name]
                                        })
    logger.info('found ' + str(alerts_count) + ' alerts')
    return alerts_count


def _count_scored_entities(host, scored_entity_event_collection_name):
    scored_entities_count = get_collections_size(host=host,
                                                 collection_names_regex='^' + scored_entity_event_collection_name + '$',
                                                 find_query={
                                                     'score': {
                                                         '$gte': 50
                                                     }
                                                 })
    logger.info('found ' + str(scored_entities_count) + ' scored entity events')
    return scored_entities_count
