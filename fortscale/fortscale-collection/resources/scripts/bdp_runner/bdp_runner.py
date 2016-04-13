import argparse
import sys
from dateutil.parser import parse

from data_sources import data_source_to_score_tables
from synchronize import Synchronizer


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


def main():
    args = sys.argv[1:]
    args = ['--host', 'tc-agent9', '--start', '7 april 2016', '--data_sources', 'ssh', '--wait_between_syncs', 0]
    parser = create_parser()
    arguments = parser.parse_args(args)
    start = parse(arguments.start)
    if start.minute + start.second > 0:
        print "start time can't be in the middle of an hour"
        sys.exit(1)
    block_on_tables = [data_source_to_score_tables[data_source] for data_source in arguments.data_sources]
    Synchronizer(host=arguments.host,
                 start=start,
                 block_on_tables=block_on_tables,
                 wait_between_syncs=60 * int(arguments.wait_between_syncs),
                 polling_interval=60 * int(arguments.polling_interval))\
        .run()


if __name__ == '__main__':
    main()
