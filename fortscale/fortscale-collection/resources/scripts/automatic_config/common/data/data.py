import json
import os

from .. import utils
from ..utils import time_utils
from ..utils.io import print_verbose


class DataMetaData:
    def __init__(self, dir_path, name):
        self._path = os.path.join(dir_path, name)
        self._name = name
        if self.exists():
            self._load()
        else:
            self._intervals_queried = []

    def exists(self):
        return os.path.isfile(self._path)

    def add_interval(self, interval):
        self._intervals_queried.append([interval[0], interval[1] + 1])
        cleaned_intervals = []
        for interval in list(self.iterate_intervals()):
            if len(cleaned_intervals) > 0:
                last_interval = cleaned_intervals[-1]
                if last_interval[1] == interval[0]:
                    interval[0] = last_interval[0]
                    cleaned_intervals.pop()
            cleaned_intervals.append(interval)
        self._intervals_queried = cleaned_intervals

    def iterate_intervals(self):
        for interval in sorted(self._intervals_queried, key = lambda i: i[0]):
            yield interval

    def save(self):
        with utils.io.FileWriter(self._path + '.metadata') as f:
            json.dump(self._intervals_queried, f)

    def _load(self):
        with open(self._path + '.metadata', 'r') as f:
            self._intervals_queried = json.load(f)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        s = self._name + ':\n'
        s += 'Time intervals queried:\n'
        for interval in self.iterate_intervals():
            s += '\t' + time_utils.interval_to_str(interval[0], interval[1]) + '\n'
        return s

    def __eq__(self, other):
        return self._intervals_queried == other._intervals_queried

    def __neq__(self, other):
        return not self.__eq__(other)


class Data:
    def __init__(self, dir_path, name):
        self._metadata = DataMetaData(dir_path, name)
        self._path = os.path.join(dir_path, name)
        self._name = name
        if self._metadata.exists():
            self._do_load()

    def _query(self, start_time, end_time):
        if start_time >= end_time:
            print_verbose('nothing to query - empty interval (starting at' + time_utils.timestamp_to_str(start_time) + ')')
            return False

        for interval in self._metadata.iterate_intervals():
            if start_time >= interval[0] and end_time <= interval[1]:
                print_verbose('nothing to query - interval already queried:', time_utils.interval_to_str(start_time, end_time))
                return False
            if interval[0] <= start_time < interval[1]:
                return self._query(interval[1], end_time)
            if interval[0] < end_time <= interval[1]:
                return self._query(start_time, interval[0])
            if start_time < interval[0] and interval[1] < end_time:
                queried_something = self._query(start_time, interval[0])
                queried_something |= self._query(interval[1], end_time)
                return queried_something

        print_verbose('Querying ' + self._path + ' (interval ' + time_utils.interval_to_str(start_time, end_time) + ')...')

        interval = self._do_query(start_time = start_time, end_time = end_time)
        if interval is None:
            return False

        self._metadata.add_interval(interval)
        return True

    def query(self, start_time, end_time, should_save_every_day = False):
        if start_time is None:
            start_time = self._find_boundary_time(is_start = True)
        if end_time is None:
            end_time = self._find_boundary_time(is_start = False)

        if should_save_every_day:
            day = 60 * 60 * 24
            queried_something = False
            while start_time < end_time:
                if self.query(start_time = start_time, end_time = min(start_time + day, end_time)):
                    queried_something = True
                    self.save()
                start_time += day
            return queried_something
        else:
            print_verbose('Querying interval ' + time_utils.interval_to_str(start_time, end_time) + '...')
            return self._query(start_time, end_time)

    def save(self):
        self._metadata.save()
        self._do_save()

    def _do_load(self):
        raise NotImplementedException()

    def _do_save(self):
        raise NotImplementedException()

    def _do_query(self, start_time, end_time):
        raise NotImplementedException()

    def _find_boundary_time(is_start):
        raise NotImplementedException()

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return self._metadata.__str__()

    def __eq__(self, other):
        return self._metadata == other._metadata

    def __neq__(self, other):
        return not self.__eq__(other)


class DataCollection:
    def __init__(self, dir_path, data_class, *data_ctor_args):
        self._dir_path = dir_path
        self._data_class = data_class
        self._data_ctor_args = data_ctor_args

    def query(self, start_time, end_time, should_save_every_day = False):
        queried_something = False
        for data_name in self._get_all_data_names():
            data = self._data_class(self._dir_path, data_name, *self._data_ctor_args)
            if data.query(start_time, end_time, should_save_every_day):
                queried_something = True
                data.save()
            print_verbose()
        return queried_something

    def _get_loaded_data_file_names(self):
        if os.path.exists(self._dir_path):
            return [os.path.splitext(file_name)[0]
                    for file_name in os.listdir(self._dir_path)
                    if not file_name.endswith('.metadata')]
        else:
            return []

    def __iter__(self):
        for data_file_name in self._get_loaded_data_file_names():
            yield self._data_class(self._dir_path, data_file_name, *self._data_ctor_args)

    def _iterate_metadatas(self):
        for data_file_name in self._get_loaded_data_file_names():
            yield DataMetaData(self._dir_path, data_file_name)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        s = self._dir_path + '\n'
        s += '--------------------------------'
        for data in self._iterate_metadatas():
            s += ('\n\t').join(('\n' + str(data)).split('\n'))
        return s

    def _get_all_data_names(self):
        raise NotImplementedException()
