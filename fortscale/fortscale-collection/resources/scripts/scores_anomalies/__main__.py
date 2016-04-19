import argparse
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from automatic_config.common import utils

from data import TableScores
from algo import find_scores_anomalies


def load_data_from_fs(host=None):
    return TableScores(host, 'scores', 'sshscores')


def run(arguments, should_query, should_run_algo):
    table_scores = load_data_from_fs(arguments.host)
    if should_query:
        table_scores.query(utils.time_utils.time_to_epoch(arguments.start_date),
                           utils.time_utils.time_to_epoch(arguments.end_date),
                           should_save_every_day=True)

    if should_run_algo:
        find_scores_anomalies(table_scores,
                              warming_period=int(arguments.warming_period),
                              score_field_names=arguments.score_fields)


def show_info(arguments):
    print load_data_from_fs()


def create_parser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(help='commands')

    load_parent_parser = argparse.ArgumentParser(add_help=False)
    load_parent_parser.add_argument('--start_date',
                                    action='store',
                                    dest='start_date',
                                    help='The start date (including) from which to look for anomalies, e.g. - "23 march 2016"',
                                    required=True)
    load_parent_parser.add_argument('--end_date',
                                    action='store',
                                    dest='end_date',
                                    help='The end date (excluding) from which to look for anomalies, e.g. - "24 march 2016"',
                                    required=True)
    load_parent_parser.add_argument('--host',
                                    action='store',
                                    dest='host',
                                    help='The impala host to which to connect to. Default is localhost',
                                    default='localhost')

    load_parser = subparsers.add_parser('load',
                                        help='Load data from impala',
                                        parents=[load_parent_parser])
    load_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=True, should_run_algo=False))

    algo_parent_parser = argparse.ArgumentParser(add_help=False)
    algo_parent_parser.add_argument('--warming_period',
                                    action='store',
                                    dest='warming_period',
                                    help='The number of days to warm up before starting to look for scores anomalies',
                                    type=int,
                                    default='3')

    algo_parser = subparsers.add_parser('algo',
                                        help='Run the algorithm on already loaded data',
                                        parents=[algo_parent_parser])
    algo_parser.add_argument('--score_fields',
                             nargs='+',
                             action='store',
                             dest='score_fields',
                             help='The name of the score fields to analyze. If not specified - all fields will be analyzed',
                             default=None)
    algo_parser.set_defaults(host=None)
    algo_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=False, should_run_algo=True))

    run_parser = subparsers.add_parser('run',
                                       help='Load data from impala and then run the algorithm on the data',
                                       parents=[load_parent_parser])
    run_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=True, should_run_algo=True))

    info_parser = subparsers.add_parser('info',
                                        help='Show information about the loaded data')
    info_parser.set_defaults(cb=lambda arguments: show_info(arguments))

    return parser


def main():
    args = sys.argv[1:]
    # args = ['info']
    # args = ['load', '--start', '1 july 2015', '--end', '1 august 2015', '--host', '192.168.45.44']
    args = ['algo', '--score_fields', 'normalized_src_machine_score']
    # args = ['run', '--start', '1 july 2015', '--end', '1 august 2015', '--host', '192.168.45.44']
    parser = create_parser()
    arguments = parser.parse_args(args)
    if arguments.cb is None:
        parser.parse_args(args + ['-h'])
    else:
        arguments.cb(arguments)


if __name__ == '__main__':
    main()
