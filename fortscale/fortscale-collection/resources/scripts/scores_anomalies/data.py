import datetime
import itertools
import json
import os
import sys
from impala.dbapi import connect

sys.path.append('..')

from automatic_config.common.data import Data
from automatic_config.common.utils import print_verbose
from automatic_config.common import utils


class CollectionDummy():
    def __init__(self, name):
        self.name = name


class FieldScores(Data):
    def __init__(self, connection, path, table_name, field_name):
        self._connection = connection
        self._table_name = table_name
        self.field_name = field_name
        self._day_to_scores_hist = {}
        Data.__init__(self, path, CollectionDummy(field_name), start_time_field_name = 'TODO-WTF')

    def _do_save(self):
        print_verbose('saving...')
        with utils.FileWriter(self._path) as f:
            json.dump(self._day_to_scores_hist, f)
        print_verbose('finished saving')

    def _do_load(self):
        print_verbose('loading...')
        with open(self._path, 'r') as f:
            self._day_to_scores_hist = json.load(f)
            for scores in self._day_to_scores_hist.itervalues():
                for score, count in scores.items():
                    del scores[score]
                    scores[int(score)] = count
        print_verbose('finished loading')

    def _do_query(self, start_time, end_time):
        print 'Querying ' + self.field_name + '...'
        day_to_scores_hist = self._get_day_to_scores_hist(start_time, end_time)
        queried_something = False
        for day, scores_hist in day_to_scores_hist.iteritems():
            saved_scores_hist = self._day_to_scores_hist.setdefault(day, {})
            for score, count in scores_hist.iteritems():
                queried_something = True
                saved_scores_hist[score] = saved_scores_hist.get(score, 0) + count
        if not queried_something:
            return None
        return start_time, end_time - 1

    def _get_day_to_scores_hist(self, start_time, end_time):
        cursor = self._connection.cursor()
        cursor.execute('select yearmonthday, ' + self.field_name + ', count(*)' +
                       ' from ' + self._table_name + ' where yearmonthday >= ' + self._date_to_partition(start_time) +
                       ' and yearmonthday < ' + self._date_to_partition(end_time) +
                       ' group by yearmonthday, ' + self.field_name)
        return dict([(yearmonthday, dict((int(entry[1]), entry[2]) for entry in entries_with_same_date))
                     for yearmonthday, entries_with_same_date in itertools.groupby(sorted(list(cursor), key = lambda entry: entry[0]),
                                                                                   lambda entry: entry[0])])

    @staticmethod
    def _date_to_partition(time):
        date = datetime.datetime.fromtimestamp(time)
        return ''.join([str(date.year), '%02d' % date.month, '%02d' % date.day])

    def __iter__(self):
        return self._day_to_scores_hist.iteritems()


class TableScores:
    def __init__(self, host, dir_path, table_name):
        self._connection = connect(host=host, port=21050)
        self._table_name = table_name
        self._field_to_scores = {}
        self._dir_path = dir_path

    def query(self, start_time, end_time, should_save_every_day = False):
        queried_something = False
        for score_field_name in self._get_all_score_field_names():
            field_scores = FieldScores(self._connection, self._dir_path, self._table_name, score_field_name)
            queried_something |= field_scores.query(start_time, end_time, should_save_every_day)
            field_scores.save()
            print_verbose()
        return queried_something

    def _get_loaded_score_field_names(self):
        if os.path.exists(self._dir_path):
            return [os.path.splitext(file_name)[0] for file_name in os.listdir(self._dir_path)]
        else:
            return []

    def __iter__(self):
        for field_name in self._get_loaded_score_field_names():
            yield FieldScores(self._connection, self._dir_path, self._table_name, field_name)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return 'Queried fields:\n' + '\n'.join(['\t' + field_name
                                                for field_name in self._get_loaded_score_field_names()])

    def _get_all_score_field_names(self):
        cursor = self._connection.cursor()
        cursor.execute('describe ' + self._table_name)
        return [field[0] for field in cursor if field[0].find('score') >= 0 and field[0] != 'eventscore']
