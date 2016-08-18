import logging
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils.mongo import iter_collections

logger = logging.getLogger('2.6-step4')


def remove_documents(host, collection_names_regex):
    for collection in iter_collections(host=host, collection_names_regex=collection_names_regex):
        res = collection.remove({})
        logger.info('removed ' + str(res['n']) + ' documents from ' + collection.name)
        if res['ok'] != 1:
            logger.error('remove failed')
            return False
    return True
