import argparse
import datetime
import pymongo
import sys
from dateutil.parser import parse

from data_sources import data_source_to_score_tables
from synchronize import Synchronizer

sys.path.append(__file__ + r'\..\..\..')
from automatic_config.common.utils import time_utils


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
                        default='30')
    parser.add_argument('--polling_interval',
                        action='store',
                        dest='polling_interval',
                        help='The time (in minutes) to wait between successive polling of impala. Default is 3',
                        default='3')
    parser.add_argument('--data_sources',
                        nargs='+',
                        action='store',
                        dest='data_sources',
                        help='The data sources to to wait for before syncing (all of the data sources)',
                        choices=data_source_to_score_tables.keys(),
                        required=True)
    parser.add_argument('--host',
                        action='store',
                        dest='host',
                        help='The impala host to which to connect to. Defaults to localhost',
                        default='localhost')
    return parser


def get_all_collection_names(mongo_db):
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return filter(lambda name: name.startswith('aggr_'), names)


def validate(arguments):
    start = (parse(arguments.start) - datetime.datetime(1970, 1, 1)).total_seconds()
    if start % 60*60 != 0:
        print "start time can't be in the middle of an hour"
        sys.exit(1)

    mongo_db = pymongo.MongoClient(arguments.host, 27017).fortscale
    for collection_name in get_all_collection_names(mongo_db):
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
    args = sys.argv[1:]
    args = ['--host', 'tc-agent9', '--start', '14 april 2016 02:00', '--data_sources', 'ssh', '--wait_between_syncs', 0]
    parser = create_parser()
    arguments = parser.parse_args(args)
    start = parse(arguments.start)
    validate(arguments)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.data_sources]
    Synchronizer(host=arguments.host,
                 start=start,
                 block_on_tables=block_on_tables,
                 wait_between_syncs=60 * int(arguments.wait_between_syncs),
                 polling_interval=60 * int(arguments.polling_interval))\
        .run()


if __name__ == '__main__':
    main()
