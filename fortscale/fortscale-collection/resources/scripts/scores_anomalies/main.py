import copy
import sys
sys.path.append('..')
from automatic_config.common import utils, visualizations

from data import TableScores


if __name__ == '__main__':
    start_date = '1 july 2015'
    end_date = '1 august 2015'
    end_date = '5 july 2015'
    HOST = '192.168.45.44'

    table_scores = TableScores(HOST, 'scores', 'sshscores')
    table_scores.query(utils.string_to_epoch(start_date), utils.string_to_epoch(end_date), should_save_every_day=True)

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
