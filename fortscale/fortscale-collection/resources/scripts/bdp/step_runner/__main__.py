import argparse
import logging
import os
import pymongo
import sys
from dateutil.parser import parse

from data_sources import data_source_to_score_tables
from synchronize import Synchronizer

sys.path.append(os.path.sep.join([os.path.dirname(__file__), '..', '..']))
from automatic_config.common.utils import time_utils, mongo
sys.path.append(os.path.sep.join([os.path.dirname(__file__), '..']))
from validation.validation import validate_all_buckets_synced


def create_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('--start',
                        action='store',
                        dest='start',
                        help='The date from which to start , e.g. - "23 march 2016 13:00"',
                        required=True)
    parser.add_argument('--wait_between_syncs',
                        action='store',
                        dest='wait_between_syncs',
                        help='The minimum amount of time (in minutes) between successive syncs. Default is 30',
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
    parser.add_argument('--retro_validation_gap',
                        action='store',
                        dest='retro_validation_gap',
                        help="The time gap (in hours) used when doing validation, i.e. - whenever the i'th hour is "
                             "sent to aggregations, the (i - retro_validation_gap)'th hour is validated. Default is 1",
                        type=int,
                        default='1')
    parser.add_argument('--max_delay',
                        action='store',
                        dest='max_delay',
                        help="The max delay (in hours) that the system should get to. If there's a bigger delay - the "
                             "script will continue to run as usual, but error message will be printed. Default is 3",
                        type=int,
                        default='3')
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to wait for before syncing '
                             '(syncing is done for all of the data sources)',
                        choices=data_source_to_score_tables.keys(),
                        required=True)
    parser.add_argument('--host',
                        action='store',
                        dest='host',
                        help='The host to which to connect to. Default is localhost',
                        default='localhost')
    return parser


def get_all_collection_names(mongo_db):
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return filter(lambda name: name.startswith('aggr_'), names)


def validate_arguments(arguments):
    start = time_utils.time_to_epoch(arguments.start)
    if start % 60*60 != 0:
        print "start time can't be in the middle of an hour"
        sys.exit(1)

    if not validate_all_buckets_synced(host=arguments.host,
                                       start_time_epoch=start,
                                       end_time_epoch=sys.maxint):
        print "there are already some aggregation buckets with startTime greater/equal to the given start time " \
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
    logging.basicConfig(level=logging.INFO)
    parser = create_parser()
    arguments = parser.parse_args()
    validate_arguments(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.data_sources]
    Synchronizer(host=arguments.host,
                 start=parse(arguments.start),
                 block_on_tables=block_on_tables,
                 wait_between_syncs=60 * int(arguments.wait_between_syncs),
                 min_free_memory=1024 ** 3 * int(arguments.min_free_memory),
                 polling_interval=60 * int(arguments.polling_interval),
                 retro_validation_gap=60 * 60 * int(arguments.retro_validation_gap),
                 max_delay=60 * 60 * int(arguments.max_delay)) \
        .run()


if __name__ == '__main__':
    main()
