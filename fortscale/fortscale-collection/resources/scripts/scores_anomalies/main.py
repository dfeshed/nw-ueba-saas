import copy
import datetime
from automatic_config.common import visualizations
from dateutil.parser import parse

from data import TableScores


def string_to_epoch(time):
    return (parse(str(time)) - datetime.datetime.utcfromtimestamp(0)).total_seconds()


if __name__ == '__main__':
    start_date = '1 july 2015'
    end_date = '1 august 2015'
    end_date = '3 july 2015'
    HOST = '192.168.45.44'

    scores = TableScores(HOST, 'scores', 'sshscores')
    scores.query(string_to_epoch(start_date), string_to_epoch(end_date), should_save_every_day=True)


    for score_field_name, date_to_scores in scores:
        print
        print '---------------------------'
        print score_field_name
        print '---------------------------'
        for date, scores in date_to_scores.iteritems():
            print date, ':'
            scores = copy.deepcopy(scores)
            scores[0] = 0
            visualizations.show_hist(scores)
