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
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from automatic_config.common.utils import time_utils, mongo

logger = logging.getLogger('step2')


def positive_int_type(i):
    if type(i) != int or i < 1:
        raise argparse.ArgumentTypeError('must be positive integer')
    return i


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host,
                                              parsers.start,
                                              parsers.validation_timeout,
                                              parsers.online_manager],
                                     formatter_class=argparse.RawDescriptionHelpFormatter,
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
    1. Offline (which is the default): all of the data (starting from the
       time specified by --start) will be processed by batches (the size
       is determined by the --batch_size argument) until there's no more
       data available in impala.
       The processing is done by calling fortscale-collection-1.1.0-SNAPSHOT.jar
       directly (without using BDP).
       Once the script finishes successfully it's promised that all data
       has been validated (read more about validations below).
    2. Online (can be turned on by using the --online switch): the data
       will be processed by batches the same way as in offline mode, with
       the exception that once there's no more data available the script
       will wait until there's more data in the tables specified by the
       --block_on_data_sources argument (once these tables have the data,
       a new batch will run on all of the data sources).
       Because in online mode the script never finishes, and because we
       don't want to validate every batch once it ends (because we don't
       want the script to wait for the validations - we want to start the
       next batch as soon as possible) - it's only promised that all the
       data up until some delay has been validated. This delay is
       controlled by the --validation_batches_delay argument.
    In both modes the validations include making sure all events have
    been processed.

Usage example:
    python step2/run --start "8 may 1987" --block_on_data_sources ssh ntlm --timeout 5 --batch_size 24 --wait_between_batches 0 --min_free_memory 16''')
    parser.add_argument('--validation_batches_delay',
                        action='store',
                        dest='validation_batches_delay',
                        help="The delay (in batches) used when validating, i.e. - whenever the n'th batch was sent "
                             "to aggregations, the (n - validation_batches_delay)'th batch is validated. Default is 1",
                        type=positive_int_type,
                        default=1)
    parser.add_argument('--batch_size',
                        action='store',
                        dest='batch_size',
                        help='The batch size (in hours) to pass to the step',
                        type=int,
                        required=True)
    parser.add_argument('--block_on_data_sources',
                        nargs='+',
                        action='store',
                        dest='block_on_data_sources',
                        help='The data sources to wait for before starting to run a batch',
                        choices=set(data_source_to_score_tables.keys()),
                        required=True)
    return parser


def validate_not_running_same_period_twice(arguments):
    start = time_utils.get_epochtime(arguments.start)
    if not validate_all_buckets_synced(host=arguments.host,
                                       start_time_epoch=start,
                                       end_time_epoch=sys.maxint,
                                       use_start_time=True):
        print "there are already some aggregations with startTime greater/equal to the given start time " \
              "(they haven't been synced yet but are about to)"
        sys.exit(1)

    mongo_db = pymongo.MongoClient(arguments.host, 27017).fortscale
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
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s %(levelname)s %(name)s: %(message)s',
                        datefmt="%d/%m/%Y %H:%M:%S")
    parser = create_parser()
    arguments = parser.parse_args()
    if not are_tasks_running(logger=logger,
                             task_names=['aggregation-events-streaming']):
        sys.exit(1)

    validate_not_running_same_period_twice(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.block_on_data_sources]
    Manager(host=arguments.host,
            is_online_mode=arguments.is_online_mode,
            start=arguments.start,
            block_on_tables=block_on_tables,
            wait_between_batches=arguments.wait_between_batches * 60,
            min_free_memory=arguments.min_free_memory * (1024 ** 3),
            polling_interval=arguments.polling_interval * 60,
            timeout=arguments.timeout * 60,
            validation_batches_delay=arguments.validation_batches_delay,
            max_delay=arguments.max_delay * 60 * 60,
            batch_size_in_hours=arguments.batch_size) \
        .run()


if __name__ == '__main__':
    main()
