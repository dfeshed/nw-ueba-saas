import logging
import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils.mongo import iter_collections

logger = logging.getLogger('stepSAM')


def rename_documents(host, collection_names_regex, name_to_new_name_cb):
    renames = 0
    for collection in iter_collections(host=host, collection_names_regex=collection_names_regex):
        new_name = name_to_new_name_cb(collection.name)
        logger.info('renaming ' + collection.name + ' to ' + new_name + '...')
        collection.rename(new_name)
        renames += 1
    return renames
