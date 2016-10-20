import argparse
import logging
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', 'bdp', '2.6', 'step4', 'run']))
from mongo_stats import remove_documents
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', 'bdp']))
from bdp_utils.kafka import send
from bdp_utils import parsers
from bdp_utils.log import init_logging
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from automatic_config.common.utils.mongo import get_collection_names, get_collections_time_boundary


logger = logging.getLogger('2.7-upgrade')


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='2.7.2_upgrade',
                                     description=
                                     '''Upgrade to 2.7.2 script
                                     -----------------------
                                     This script does the following:
                                     1. All of the documents inside the collections
                                        model_entity_event.*global.normalized_username.* in mongo are removed.
                                     2. A model building job is triggered in order to re-build the removed models.''')
    return parser


if __name__ == '__main__':
    arguments = create_parser().parse_args()
    init_logging(logger)
    models_collection_names_regex = 'model_entity_event.*global.normalized_username.*'
    logger.info('removing models in ' +
                ', '.join(get_collection_names(host=arguments.host,
                                               collection_names_regex=models_collection_names_regex))
                + '...')
    if not remove_documents(host=arguments.host, collection_names_regex=models_collection_names_regex):
        sys.exit(1)
    end_time_in_seconds = get_collections_time_boundary(host=arguments.host,
                                                        collection_names_regex='^entity_event_.*',
                                                        is_start=False)
    logger.info('building models...')
    for collection_name in get_collection_names(host=arguments.host,
                                                collection_names_regex=models_collection_names_regex):
        model_conf_name = collection_name.replace('model_', '')
        message = '{\"sessionId\":\"2.7.2_upgrade\",\"modelConfName\":\"' + \
                  model_conf_name + '\",\"endTimeInSeconds\":' + str(end_time_in_seconds) + '}'
        send(logger=logger,
             host=arguments.host,
             topic='fortscale-entity-events-model-building-control-input',
             message=message)
    logger.info('now go get some coffee! models are being built...')
