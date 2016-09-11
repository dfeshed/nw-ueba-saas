import os
import sys

import time

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from bdp_utils import log
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo, time_utils

import logging

logger = logging.getLogger('monitoring')


def validate_progress(host, collection_name, polling_interval, max_delay):
    last_collection_end_time = None
    last_progress_time = time.time()
    while True:
        collection_end_time = mongo.get_collections_time_boundary(host=host,
                                                                  collection_names_regex=collection_name,
                                                                  is_start=False)
        logger.info('collection end time: %s' % collection_end_time)
        if collection_end_time != last_collection_end_time:
            last_collection_end_time = collection_end_time
            last_progress_time = time.time()
        if (time.time() - collection_end_time) / 60 > max_delay:
            log.log_and_send_mail('there is a gap in %s of %d minutes. '
                                  'last progress occurred at %s, in which the last scored entity event was %s' %
                                  (
                                      collection_name,
                                      (time.time() - collection_end_time) / 60,
                                      time_utils.timestamp_to_str(last_progress_time),
                                      time_utils.timestamp_to_str(last_collection_end_time))
                                  )
        logger.info('going to sleep for %d minutes...' % polling_interval)
        time.sleep(polling_interval * 60)
