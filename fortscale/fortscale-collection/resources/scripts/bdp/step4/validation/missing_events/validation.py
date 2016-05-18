import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_collections_size

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))

import logging

logger = logging.getLogger('step4.validation')


def validate_no_missing_events(host, timeout, start, end):
    logger.info('validating that there are no missing events...')
    entity_events_counter = get_collections_size(host=host, collection_names_regex='^entity_event_(?!meta_data_).')
    scored_entity_events_counter = get_collections_size(host=host, collection_names_regex='^scored___entity_event_')
    logger.info(entity_events_counter, 'entity events', scored_entity_events_counter, 'scored entity events')
    if scored_entity_events_counter == entity_events_counter:
        logger.info('OK')
        return True
    else:
        logger.error('FAILED')
        return False
