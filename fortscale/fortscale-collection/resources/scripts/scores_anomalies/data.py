import datetime
import itertools
import json
import sys
from impala.dbapi import connect

sys.path.append('..')

from automatic_config.common.data import Data
from automatic_config.common.utils import print_verbose
from automatic_config.common import utils


class CollectionDummy():
    def __init__(self):
        self.name = 'TODO-WTF'


class TableScores(Data):
    def __init__(self, host, path, table_name):
        self._connection = connect(host=host, port=21050)
        self._table_name = table_name
        self._scores = {}
        Data.__init__(self, path, CollectionDummy(), start_time_field_name = 'TODO-WTF')

    def _do_save(self):
        print_verbose('saving...')
        with utils.FileWriter(self._path) as f:
            json.dump(self._scores, f)
        print_verbose('finished saving')

    def _do_load(self):
        print_verbose('loading...')
        with open(self._path, 'r') as f:
            self._scores = json.load(f)
            for day_to_scores in self._scores.itervalues():
                for scores in day_to_scores.itervalues():
                    for score, count in scores.items():
                        del scores[score]
                        scores[int(score)] = count
        print_verbose('finished loading')

    def _do_query(self, start_time, end_time):
        score_field_name_to_day_to_scores = {}
        for score_field_name in self._get_score_field_names(self._table_name)[:2]:
            print 'Querying ' + score_field_name + '...'
            score_field_name_to_day_to_scores[score_field_name] = self._get_day_to_scores_distribution(score_field_name, start_time, end_time)
        queried_something = False
        for score_field_name, day_to_scores in score_field_name_to_day_to_scores.iteritems():
            saved_scores = self._scores.setdefault(score_field_name, {})
            for day, scores in day_to_scores.iteritems():
                saved_day_scores = saved_scores.setdefault(day, {})
                for score, count in scores.iteritems():
                    queried_something = True
                    saved_day_scores[score] = saved_day_scores.get(score, 0) + count
        if not queried_something:
            return None
        return start_time, end_time - 1

    def _get_score_field_names(self, table_name):
        cursor = self._connection.cursor()
        cursor.execute('describe ' + table_name)
        return [field[0] for field in cursor if field[0].find('score') >= 0 and field[0] != 'eventscore']


    def _get_day_to_scores_distribution(self, score_field_name, start_time, end_time):
        cursor = self._connection.cursor()
        cursor.execute('select yearmonthday, ' + score_field_name + ', count(*)' +
                       ' from ' + self._table_name + ' where yearmonthday >= ' + self._date_to_partition(start_time) +
                       ' and yearmonthday < ' + self._date_to_partition(end_time) +
                       ' group by yearmonthday, ' + score_field_name)
        return dict([(yearmonthday, dict((int(entry[1]), entry[2]) for entry in entries_with_same_date))
                     for yearmonthday, entries_with_same_date in itertools.groupby(sorted(list(cursor), key = lambda entry: entry[0]),
                                                                                   lambda entry: entry[0])])

    def _date_to_partition(self, time):
        date = datetime.datetime.fromtimestamp(time)
        return ''.join([str(date.year), '%02d' % date.month, '%02d' % date.day])

    def __iter__(self):
        return self._scores.iteritems()
