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
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from utils.data_sources import data_source_to_score_tables
from automatic_config.common.utils import time_utils, mongo


def positive_int_type(i):
    if type(i) != int or i < 1:
        raise argparse.ArgumentTypeError('must be positive integer')
    return i


def create_parser():
    parser = argparse.ArgumentParser(parents=[parsers.host, parsers.start])
    parser.add_argument('--online',
                        action='store_const',
                        dest='is_online_mode',
                        const=True,
                        help='pass this flag if running this step should never end: '
                             'whenever there is no more data, just wait until more data arrives',)
    parser.add_argument('--batch_size',
                        action='store',
                        dest='batch_size',
                        help='The batch size (in hours) to pass to the step. Default is 24',
                        type=int,
                        default='24')
    parser.add_argument('--wait_between_batches',
                        action='store',
                        dest='wait_between_batches',
                        help='The minimum amount of time (in minutes) between successive batch runs. Default is 30',
                        type=int,
                        default='30')
    parser.add_argument('--min_free_memory',
                        action='store',
                        dest='min_free_memory',
                        help='Whenever the amount of free memory in the system is below the given number (in GB), '
                             'the script will block. Default is 20',
                        type=int,
                        default='20')
    parser.add_argument('--polling_interval',
                        action='store',
                        dest='polling_interval',
                        help='The time (in minutes) to wait between successive polling of impala. Default is 3',
                        type=int,
                        default='3')
    parser.add_argument('--validation_batches_delay',
                        action='store',
                        dest='validation_batches_delay',
                        help="The delay (in batches) used when validating, i.e. - whenever the n'th batch was sent "
                             "to aggregations, the (n - validation_batches_delay)'th batch is validated. Default is 1",
                        type=positive_int_type,
                        default='1')
    parser.add_argument('--max_delay',
                        action='store',
                        dest='max_delay',
                        help="The max delay (in hours) that the system should get to. If there's a bigger delay - the "
                             "script will continue to run as usual, but error message will be printed. Default is 3",
                        type=int,
                        default='3')
    parser.add_argument('--block_on_data_sources',
                        nargs='+',
                        action='store',
                        dest='block_on_data_sources',
                        help='The data sources to wait for before starting to run a batch '
                             '(the batch is done for all of the data sources though)',
                        choices=data_source_to_score_tables.keys(),
                        required=True)
    return parser


def validate_not_running_same_period_twice(arguments):
    start = time_utils.get_epoch(arguments.start)
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
    validate_not_running_same_period_twice(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.block_on_data_sources]
    Manager(host=arguments.host,
            is_online_mode=arguments.is_online_mode,
            start=arguments.start,
            block_on_tables=block_on_tables,
            wait_between_batches=60 * arguments.wait_between_batches,
            min_free_memory=1024 ** 3 * arguments.min_free_memory,
            polling_interval=60 * arguments.polling_interval,
            validation_batches_delay=60 * 60 * arguments.validation_batches_delay,
            max_delay=60 * 60 * arguments.max_delay,
            batch_size_in_hours=arguments.batch_size) \
        .run()


if __name__ == '__main__':
    main()
