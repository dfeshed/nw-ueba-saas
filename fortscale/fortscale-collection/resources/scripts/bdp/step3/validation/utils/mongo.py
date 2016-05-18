import logging
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.run import validate_by_polling
from bdp_utils.mongo import get_collections_size


logger = logging.getLogger('step3.validation')


def validate_collections_are_empty(log_msg,
                                   host,
                                   validation_timeout,
                                   validation_polling,
                                   collection_names_regex,
                                   find_query={}):
    logger.info(log_msg)
    if validate_by_polling(status_cb=lambda: get_collections_size(host=host,
                                                                  collection_names_regex=collection_names_regex,
                                                                  find_query=find_query),
                           status_target=0,
                           no_progress_timeout=validation_timeout,
                           polling=validation_polling):
        logger.info('OK')
        return True
    else:
        logger.error('FAIL')
        return False
