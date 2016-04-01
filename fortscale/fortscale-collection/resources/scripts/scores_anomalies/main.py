import argparse
import copy
import sys

sys.path.append('..')
from automatic_config.common import utils, visualizations

from data import TableScores


def create_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('--start_date',
                        action='store',
                        dest='start_date',
                        help='The start date (including) from which to look for anomalies, e.g. - "23 march 2016"',
                        required=True)
    parser.add_argument('--end_date',
                        action='store',
                        dest='end_date',
                        help='The end date (excluding) from which to look for anomalies, e.g. - "24 march 2016"',
                        required=True)
    parser.add_argument('--host',
                        action='store',
                        dest='host',
                        help='The impala host to which to connect to',
                        default='localhost')

    return parser


if __name__ == '__main__':
    parser = create_parser()
    arguments = parser.parse_args()

    table_scores = TableScores(arguments.host, 'scores', 'sshscores')
    table_scores.query(utils.string_to_epoch(arguments.start_date), utils.string_to_epoch(arguments.end_date), should_save_every_day=True)

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
