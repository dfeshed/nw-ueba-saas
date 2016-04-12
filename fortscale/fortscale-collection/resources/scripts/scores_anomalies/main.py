import argparse
import copy
import sys

sys.path.append(__file__ + r'\..\..')
from automatic_config.common import utils, visualizations

from data import TableScores



def load_data_from_fs(host = None):
    return TableScores(host, 'scores', 'sshscores')


def run(arguments, should_query, should_run_algo):
    table_scores = load_data_from_fs(arguments.host if hasattr(arguments, 'host') else None)
    if should_query:
        table_scores.query(utils.string_to_epoch(arguments.start_date),
                           utils.string_to_epoch(arguments.end_date),
                           should_save_every_day=True)

    if should_run_algo:
        for field_scores in table_scores:
            print
            print '---------------------------'
            print field_scores.field_name
            print '---------------------------'
            for day, scores_hist in field_scores:
                print day, ':'
                scores_hist = copy.deepcopy(scores_hist)
                scores_hist[0] = 0
                visualizations.show_hist(scores_hist)


def show_info(arguments):
    print load_data_from_fs()


def create_parser():
    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers(help='commands')
    parser.set_defaults(cb=None)

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
                                    help='The impala host to which to connect to. Defaults to localhost',
                                    default='localhost')

    load_parser = subparsers.add_parser('load',
                                        help='Load data from impala',
                                        parents=[load_parent_parser])
    load_parser.set_defaults(cb=lambda arguments: run(arguments, should_query=True, should_run_algo=False))

    algo_parser = subparsers.add_parser('algo',
                                        help='Run the algorithm on already loaded data')
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
    args = ['algo']
    # args = ['run', '--start', '1 july 2015', '--end', '1 august 2015', '--host', '192.168.45.44']
    parser = create_parser()
    arguments = parser.parse_args(args)
    if arguments.cb is None:
        parser.parse_args(args + ['-h'])
    else:
        arguments.cb(arguments)

if __name__ == '__main__':
    main()
