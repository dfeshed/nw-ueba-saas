import argparse
import logging
import os
import pymongo
import sys

from manager import Manager

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from validation.validation import validate_all_buckets_synced

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from bdp_utils import parsers
from bdp_utils.data_sources import data_source_to_score_tables
from bdp_utils.samza import are_tasks_running
from bdp_utils.log import init_logging
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils, mongo

logger = logging.getLogger('step2')


def positive_int_type(i):
    if type(i) != int or i < 1:
        raise argparse.ArgumentTypeError('must be positive integer')
    return i


def create_parser():
    parser = argparse.ArgumentParser(formatter_class=argparse.RawDescriptionHelpFormatter,
                                     prog='step2/run',
                                     description=
'''Scoring to aggregation step
---------------------------
Step prerequisites:
    Data should be provided in impala scores tables. The step will run on
    all of the available tables (there's no option to specify a subset of
    data sources).

Step results:
    Raw scored events will be aggregated, and the results will appear in
    mongo collections (all those starting with "aggr_").

Inner workings:
    This step supports two operation modes:
    1. Offline: all of the data (starting from the time specified by --start)
       will be processed by batches (the size is determined by the --batch_size
       argument) until there's no more data available in impala.
       The processing is done by calling fortscale-collection-1.1.0-SNAPSHOT.jar
       directly (without using BDP).
       Once the script finishes successfully it's promised that all data
       has been validated (read more about validations below).
    2. Online: the data will be processed by batches the same way as in offline
       mode, with the exception that once there's no more data available the
       script will wait until there's more data in the tables specified by the
       --block_on_data_sources argument (once these tables have the data, a new
       batch will run on all of the data sources).
       Because in online mode the script never finishes, and because we
       don't want to validate every batch once it ends (because we don't
       want the script to wait for the validations - we want to start the
       next batch as soon as possible) - it's only promised that all the
       data up until some delay has been validated. This delay is
       controlled by the --validation_batches_delay argument.
    In both modes the validations include making sure all events have
    been processed.

Usage examples:
    python step2/run online --start "8 may 1987" --block_on_data_sources ssh ntlm --batch_size 1 --polling_interval 3 --wait_between_batches 0 --min_free_memory_gb 16
    python step2/run offline --start "8 may 1987" --block_on_data_sources ssh ntlm --timeout 5 --batch_size 24 --polling_interval 3
    ''')
    more_args_parent = argparse.ArgumentParser(add_help=False)
    more_args_parent.add_argument('--validation_batches_delay',
                                  action='store',
                                  dest='validation_batches_delay',
                                  help="The delay (in batches) used when validating, i.e. - whenever the n'th batch was sent "
                                       "to aggregations, the (n - validation_batches_delay)'th batch is validated. Default is 1",
                                  type=positive_int_type,
                                  default=1)
    more_args_parent.add_argument('--batch_size',
                                  action='store',
                                  dest='batch_size',
                                  help='The batch size (in hours) to pass to the step',
                                  type=int,
                                  required=True)
    more_args_parent.add_argument('--block_on_data_sources',
                                  nargs='+',
                                  action='store',
                                  dest='block_on_data_sources',
                                  help='The data sources to wait for before starting to run a batch. If not specified, '
                                       'the data sources which are active all the time (in every single hour) will be '
                                       'used',
                                  choices=set(data_source_to_score_tables.keys()))
    more_args_parent.add_argument('--calc_block_on_tables_based_on_days',
                                  action='store',
                                  dest='calc_block_on_tables_based_on_days',
                                  help='If --block_on_data_sources is not specified, you should specify how many days '
                                       'back should be analyzed in order to find what tables to block on',
                                  type=int)

    models_scheduler_parent = argparse.ArgumentParser(add_help=False)
    models_scheduler_parent.add_argument('--build_models_interval_in_hours',
                                         action='store',
                                         dest='build_models_interval_in_hours',
                                         help='The logic time interval (in hours) for building models. '
                                              'If not specified, no models will be built '
                                              '(they can, however be built by an external entity).',
                                         type=int)
    models_scheduler_parent.add_argument('--build_entity_models_interval_in_hours',
                                         action='store',
                                         dest='build_entity_models_interval_in_hours',
                                         help='The logic time interval (in hours) for building entity models. '
                                              'If not specified, no entity models will be built '
                                              '(they can, however be built by an external entity).',
                                         type=int)

    subparsers = parser.add_subparsers(help='commands')
    common_parents = [more_args_parent,
                      parsers.host,
                      parsers.start]
    online_parser = subparsers.add_parser('online',
                                          help='Run the step in online mode',
                                          parents=common_parents + [parsers.online_manager, models_scheduler_parent])
    online_parser.set_defaults(is_online_mode=True)
    offline_parser = subparsers.add_parser('offline',
                                           help='Run the step in offline mode',
                                           parents=common_parents + [parsers.validation_polling_interval, parsers.validation_timeout])
    offline_parser.set_defaults(is_online_mode=False)
    return parser


def validate_not_running_same_period_twice(arguments):
    start = time_utils.get_epochtime(arguments.start)
    really_big_epochtime = time_utils.get_epochtime('29990101')
    if not validate_all_buckets_synced(host=arguments.host,
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


def main():
    arguments = create_parser().parse_args()
    init_logging(logger)
    if not are_tasks_running(logger=logger,
                             host=arguments.host,
                             task_names=['aggregation-events-streaming']):
        sys.exit(1)

    validate_not_running_same_period_twice(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.block_on_data_sources] \
        if arguments.block_on_data_sources else None
    if Manager(host=arguments.host,
               is_online_mode=arguments.is_online_mode,
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
    else:
        logger.error('FAILED')


if __name__ == '__main__':
    main()
