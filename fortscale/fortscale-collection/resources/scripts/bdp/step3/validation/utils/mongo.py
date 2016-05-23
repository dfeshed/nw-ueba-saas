import logging
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.manager import validate_by_polling
from bdp_utils.mongo import get_collections_size


logger = logging.getLogger('step3.validation')


def validate_collections_are_empty(log_msg,
                                   host,
                                   timeout,
                                   polling,
                                   collection_names_regex,
                                   find_query={}):
    logger.info(log_msg)
    if validate_by_polling(progress_cb=lambda: get_collections_size(host=host,
                                                                    collection_names_regex=collection_names_regex,
                                                                    find_query=find_query),
                           is_done_cb=lambda progress: progress == 0,
                           no_progress_timeout=timeout,
                           polling=polling):
        logger.info('OK')
        return True
    else:
        logger.error('FAIL')
        return False
