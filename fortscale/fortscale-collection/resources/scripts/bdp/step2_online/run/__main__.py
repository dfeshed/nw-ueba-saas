import logging
import os
import pymongo
import sys

from manager import Manager
from parse import create_parser

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.validation import validate_all_buckets_synced

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils.run import step_runner_main
from bdp_utils.data_sources import data_source_to_score_tables
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils, mongo

logger = logging.getLogger('step2_online')
init_logging(logger)


def validate_not_running_same_period_twice(arguments):
    if arguments.start is None:
        return

    start = time_utils.get_epochtime(arguments.start)
    really_big_epochtime = time_utils.get_epochtime('29990101')
    if not validate_all_buckets_synced(logger=logger,
                                       host=arguments.host,
                                       start_time_epoch=start,
                                       end_time_epoch=really_big_epochtime,
                                       use_start_time=True):
        print "there are already some aggregations with startTime greater/equal to the given start time " \
              "(they haven't been synced yet but are about to)"
        sys.exit(1)

    mongo_db = mongo.get_db(host=arguments.host)
    for collection_name in filter(lambda name: name.startswith('aggr_'), mongo.get_all_collection_names(mongo_db)):
        data = list(mongo_db[collection_name].find({
            'startTime': {
                '$gte': start
            }
        }).sort('startTime', pymongo.DESCENDING).limit(1))
        if data:
            print 'there are already some aggregations with startTime greater/equal to the given start time (e.g.: ' +\
                  collection_name + ' - ' + time_utils.timestamp_to_str(data[0]['startTime']) + ')'
            sys.exit(1)


@step_runner_main(logger)
def main():
    arguments = create_parser().parse_args()
    if not are_tasks_running(logger=logger,
                             host=arguments.host,
                             task_names=['aggregation-events-streaming']):
        sys.exit(1)

    validate_not_running_same_period_twice(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.block_on_data_sources] \
        if arguments.block_on_data_sources else None
    if Manager(host=arguments.host,
               is_online_mode=True,
               start=arguments.start,
               block_on_tables=block_on_tables,
               calc_block_on_tables_based_on_days=arguments.calc_block_on_tables_based_on_days,
               wait_between_batches=arguments.wait_between_batches * 60 if 'wait_between_batches' in arguments else 0,
               min_free_memory_gb=arguments.min_free_memory_gb if 'min_free_memory_gb' in arguments else 0,
               polling_interval=arguments.polling_interval * 60,
               timeout=arguments.timeout * 60 if 'timeout' in arguments else None,
               validation_batches_delay=arguments.validation_batches_delay,
               max_delay=arguments.max_delay * 60 * 60 if 'max_delay' in arguments else -1,
               batch_size_in_hours=arguments.batch_size,
               build_models_interval=(arguments.build_models_interval_in_hours * 60 * 60)
               if 'build_models_interval_in_hours' in arguments and arguments.build_models_interval_in_hours is not None else None,
               build_entity_models_interval=(arguments.build_entity_models_interval_in_hours * 60 * 60)
               if 'build_entity_models_interval_in_hours' in arguments and arguments.build_entity_models_interval_in_hours is not None else None) \
            .run():
        logger.info('finished successfully')

        return True
    else:
        logger.error('FAILED')
        return False



if __name__ == '__main__':
    main()
