import itertools
import json
import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.data.impala import ImpalaData, ImpalaDataCollection
from automatic_config.common.utils.io import print_verbose
from automatic_config.common import utils
from automatic_config.common.utils import time_utils, impala_utils


class FieldScores(ImpalaData):
    def __init__(self, dir_path, field_name, table_name, connection):
        self.field_name = field_name
        self._table_name = table_name
        self._connection = connection
        self._day_to_scores_hist = {}
        ImpalaData.__init__(self, dir_path, table_name, field_name, connection)

    def _do_save(self):
        print_verbose('saving...')
        with utils.io.FileWriter(self._path) as f:
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
                       ' from ' + self._table_name +
                       ' where yearmonthday >= ' + time_utils.get_impala_partition(start_time) +
                       ' and yearmonthday < ' + time_utils.get_impala_partition(end_time) +
                       ' group by yearmonthday, ' + self.field_name)
        res = dict([(yearmonthday, dict((int(entry[1] if entry[1] is not None else 0), entry[2])
                                        for entry in entries_with_same_date))
                    for yearmonthday, entries_with_same_date in itertools.groupby(sorted(list(cursor),
                                                                                         key=lambda entry: entry[0]),
                                                                                  lambda entry: entry[0])])
        cursor.close()
        return res

    def __iter__(self):
        return ((day, scores) for day, scores in sorted(self._day_to_scores_hist.iteritems(),
                                                        key=lambda day_and_scores: day_and_scores[0]))

    def __getitem__(self, item):
        time = time_utils.get_impala_partition(item)
        return self._day_to_scores_hist[time]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return super(FieldScores, self).__str__() + str(self._day_to_scores_hist) + '\n'


class TableScores(ImpalaDataCollection):
    def __init__(self, host, dir_path, table_name):
        ImpalaDataCollection.__init__(self,
                                      dir_path,
                                      FieldScores,
                                      table_name,
                                      None if host is None else impala_utils.connect(host=host))

    def _get_all_data_names(self):
        cursor = self._connection.cursor()
        cursor.execute('describe ' + self._table_name)
        res = [field[0] for field in cursor if field[0].find('score') >= 0 and field[0] != 'eventscore']
        cursor.close()
        return res

    def __repr__(self):
        return self.__str__()
