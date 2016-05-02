import argparse
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from automatic_config.common import utils
from utils.data_sources import data_source_to_score_tables

from data import TableScores
from algo import find_scores_anomalies
from investigate import investigate

def load_data_from_fs(arguments):
    script_path = os.path.dirname(os.path.abspath(__file__))
    return [TableScores(arguments.host if hasattr(arguments, 'host') else None,
                        os.path.abspath(os.path.sep.join([script_path, arguments.path, data_source])),
                        data_source_to_score_tables[data_source])
            for data_source in arguments.data_sources]


def run(arguments, should_query, should_find_anomalies):
    tables_scores = load_data_from_fs(arguments)
    for table_scores in tables_scores:
        print table_scores._table_name + ':'
        print '-----------------------'
        if should_query:
            table_scores.query(start_time=arguments.start,
                               end_time=arguments.end,
                               should_save_every_day=True)

        if should_find_anomalies:
            find_scores_anomalies(table_scores,
                                  warming_period=arguments.warming_period,
                                  score_field_names=arguments.score_fields,
                                  start=arguments.start,
                                  end=arguments.end)


def do_investigate(arguments):
    investigate(host=arguments.host,
                data_source=arguments.data_source,
                score_field_name=arguments.score_field,
                date=arguments.date)


def show_info(arguments):
    for table in load_data_from_fs(arguments):
        print table


def create_parser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(help='commands')

    general_parent_parser = argparse.ArgumentParser(add_help=False)
    general_parent_parser.add_argument('--path',
                                       action='store',
                                       dest='path',
                                       help='The path to the directory to load/save data to',
                                       required=True)
    general_parent_parser.add_argument('--data_sources',
                                       nargs='+',
                                       action='store',
                                       dest='data_sources',
                                       help='The data sources to analyze',
                                       required=True)

    load_parent_parser = argparse.ArgumentParser(add_help=False)
    load_parent_parser.add_argument('--start',
                                    action='store',
                                    dest='start',
                                    help='The start date (including) from which to load data, '
                                         'e.g. - "23 march 2016" / "20160323" / "1458684000"',
                                    required=True,
                                    type=time_type)
    load_parent_parser.add_argument('--end',
                                    action='store',
                                    dest='end',
                                    help='The end date (excluding) from which to load data, '
                                         'e.g. - "24 march 2016" / "20160324" / "1458770400"',
                                    required=True,
                                    type=time_type)
    load_parent_parser.add_argument('--host',
                                    action='store',
                                    dest='host',
                                    help='The impala host to which to connect to. Default is localhost',
                                    default='localhost')

    load_parser = subparsers.add_parser('load',
                                        help='Load data from impala',
                                        parents=[general_parent_parser, load_parent_parser])
    load_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=True, should_find_anomalies=False))

    find_parser = subparsers.add_parser('find',
                                        help='Find anomalous days in already loaded data',
                                        parents=[general_parent_parser])
    find_parser.add_argument('--warming_period',
                             action='store',
                             dest='warming_period',
                             help='The number of days to warm up before starting to look for scores anomalies',
                             type=int,
                             default='7')
    find_parser.add_argument('--score_fields',
                             nargs='+',
                             action='store',
                             dest='score_fields',
                             help='The name of the score fields to analyze. '
                                  'If not specified - all fields will be analyzed',
                             default=None)
    find_parser.add_argument('--start',
                             action='store',
                             dest='start',
                             help='The start date (including) from which to look for anomalies, '
                                  'e.g. - "23 march 2016 13:00" / "20160323" / "1458730800". '
                                  'If not specified, all the already loaded data will be usedSpace',
                             default=None,
                             type=time_type)
    find_parser.add_argument('--end',
                             action='store',
                             dest='end',
                             help='The end date (excluding) from which to look for anomalies, '
                                  'e.g. - "24 march 2016" / "20160324" / "1458770400". '
                                  'If not specified, all the already loaded data will be usedSpace',
                             default=None,
                             type=time_type)
    find_parser.set_defaults(host=None)
    find_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=False, should_find_anomalies=True))

    run_parser = subparsers.add_parser('run',
                                       help='Load data from impala and then find anomalous days in the data',
                                       parents=[load_parent_parser])
    run_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=True, should_find_anomalies=True))

    investigate_parser = subparsers.add_parser('investigate',
                                        help='Investigate anomalies in the data')
    investigate_parser.add_argument('--host',
                                    action='store',
                                    dest='host',
                                    help='The impala host to which to connect to. Default is localhost',
                                    default='localhost')
    investigate_parser.add_argument('--data_source',
                                    action='store',
                                    dest='data_source',
                                    help='The data sources to investigate',
                                    required=True)
    investigate_parser.add_argument('--score_field',
                                    action='store',
                                    dest='score_field',
                                    help='The name of the score field to investigate',
                                    required=True)
    investigate_parser.add_argument('--date',
                                    action='store',
                                    dest='date',
                                    help='The date to analyze, e.g. - '
                                         '"23 march 2016 13:00" / "20160323" / "1458730800"',
                                    required=True,
                                    type=time_type)
    investigate_parser.set_defaults(cb=lambda arguments: do_investigate(arguments))

    info_parser = subparsers.add_parser('info',
                                        help='Show information about the loaded data',
                                        parents=[general_parent_parser])
    info_parser.set_defaults(cb=lambda arguments: show_info(arguments))

    return parser


def time_type(time):
    if time is None:
        return time
    if utils.time_utils.get_epoch(time) % (24*60*60) != 0:
        raise argparse.ArgumentTypeError("time can't be in the middle of a day")
    return utils.time_utils.get_epoch(time)


def main():
    args = sys.argv[1:]
    # args = ['info', '--data_sources', 'ssh', '--path', '../scores_anomalies_data/cisco']
    # args = ['load', '--start', '1 july 2015 ', '--end', '1 august 2015', '--host', '192.168.45.44', '--data_sources', 'ssh']
    # args = ['find',
    #         '--path', '../scores_anomalies_data/cisco',
    #         '--data_sources', 'ssh',
    #         '--score_fields', 'date_time_score']
    args = ['investigate',
            '--host', '192.168.45.44',
            '--data_source', 'ssh',
            '--score_field', 'date_time_score',
            '--date', '20150720']
    # args = ['run', '--start', '1 july 2015', '--end', '1 august 2015', '--host', '192.168.45.44']
    parser = create_parser()
    arguments = parser.parse_args(args)
    if arguments.cb is None:
        parser.parse_args(args + ['-h'])
    else:
        arguments.cb(arguments)


if __name__ == '__main__':
    main()
