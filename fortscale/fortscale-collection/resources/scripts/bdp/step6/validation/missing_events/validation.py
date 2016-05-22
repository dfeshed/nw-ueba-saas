import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_collection_names, get_collections_size

import logging

logger = logging.getLogger('step6.validation')


_SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME = {
    'scored___entity_event__normalized_username_hourly': 'Suspicious Hourly User Activity',
    'scored___entity_event__normalized_username_daily': 'Suspicious Daily User Activity'
}


def validate_no_missing_events(host, start=None):
    for scored_entity_event_collection_name in get_collection_names(host=host,
                                                                    collection_names_regex='^scored___entity_event_'):
        if not _validate_no_missing_events(host=host,
                                           scored_entity_event_collection_name=scored_entity_event_collection_name,
                                           start=start):
            return False
    return True


def _validate_no_missing_events(host, scored_entity_event_collection_name, start):
    logger.info('validating that there are no missing events for ' + scored_entity_event_collection_name + '...')
    if scored_entity_event_collection_name not in _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME:
        raise Exception('This collection is not supported by the script. But fear not! '
                        'just update _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME')
    alerts_count = _count_alerts(host, scored_entity_event_collection_name, start)
    scored_entities_count = _count_scored_entities(host, scored_entity_event_collection_name, start)
    if alerts_count == scored_entities_count:
        logger.info('OK')
        return True
    else:
        logger.error('FAILED')
        return False


def _count_alerts(host, scored_entity_event_collection_name, start):
    find_query = {'name': _SCORED_ENTITY_COLLECTION_NAME_TO_ALERT_NAME[scored_entity_event_collection_name]}
    if start is not None:
        find_query['startDate'] = {'$gte': start * 1000}
    alerts_count = get_collections_size(host=host,
                                        collection_names_regex='^alerts$',
                                        find_query=find_query)
    logger.info('found', alerts_count, 'alerts')
    return alerts_count


def _count_scored_entities(host, scored_entity_event_collection_name, start):
    find_query = {'score': {'$gte': 50}}
    if start is not None:
        find_query['start_time_unix'] = {'$gte': start}
    scored_entities_count = get_collections_size(host=host,
                                                 collection_names_regex='^' + scored_entity_event_collection_name + '$',
                                                 find_query=find_query)
    logger.info('found', scored_entities_count, 'scored entity events')
    return scored_entities_count
