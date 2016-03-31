import json
import os
import pymongo
from common import utils
from common.utils import print_verbose


class Data:
    def __init__(self, dir_path, collection, start_time_field_name):
        self._collection = collection
        self._path = os.path.join(dir_path, collection.name)
        self._start_time_field_name = start_time_field_name
        if os.path.isfile(self._path):
            self._load()
        else:
            self._intervals_queried = []

    def _iterate_intervals(self):
        for interval in sorted(self._intervals_queried, key = lambda i: i[0]):
            yield interval

    def _query(self, start_time, end_time):
        if start_time >= end_time:
            print_verbose('nothing to query - empty interval (starting at' + utils.timestamp_to_str(start_time) + ')')
            return False

        for interval in self._iterate_intervals():
            if start_time == interval[0] and end_time == interval[1]:
                print_verbose('nothing to query - interval already queried:', utils.interval_to_str(start_time, end_time))
                return False
            if interval[0] <= start_time < interval[1]:
                queried_something = self._query(start_time, interval[1])
                queried_something |= self._query(interval[1], end_time)
                return queried_something
            if interval[0] < end_time <= interval[1]:
                queried_something = self._query(start_time, interval[0])
                queried_something |= self._query(interval[0], end_time)
                return queried_something
            if start_time < interval[0] and interval[1] < end_time:
                print utils.interval_to_str(start_time, end_time)
                print utils.interval_to_str(interval[0], interval[1])
                print
                queried_something = self._query(start_time, interval[0])
                queried_something |= self._query(interval[0], end_time)
                return queried_something

        print_verbose('Querying ' + self._collection.name + ' (interval ' + utils.interval_to_str(start_time, end_time) + ')...')

        interval = self._do_query(start_time = start_time, end_time = end_time)
        if interval is None:
            return False

        self._intervals_queried.append((interval[0], interval[1] + 1))
        cleaned_intervals = []
        for interval in list(self._iterate_intervals()):
            if len(cleaned_intervals) > 0:
                last_interval = cleaned_intervals[-1]
                if last_interval[1] == interval[0]:
                    interval[0] = last_interval[0]
                    cleaned_intervals.pop()
            cleaned_intervals.append(interval)
        self._intervals_queried = cleaned_intervals

        return True

    def query(self, start_time, end_time, should_save_every_day = False):
        if start_time is None:
            start_time = find_start_or_end_time_in_mongo(collection = self._collection, start_time_field_name = self._start_time_field_name, is_start = True)
        if end_time is None:
            end_time = find_start_or_end_time_in_mongo(collection = self._collection, start_time_field_name = self._start_time_field_name, is_start = False)

        if should_save_every_day:
            day = 60 * 60 * 24
            queried_something = False
            while start_time <= end_time:
                if self.query(start_time = start_time, end_time = min(start_time + day, end_time)):
                    queried_something = True
                    self.save()
                start_time += day
            return queried_something
        else:
            print_verbose('Querying interval ' + utils.interval_to_str(start_time, end_time) + '...')
            return self._query(start_time, end_time)

    def save(self):
        with utils.FileWriter(self._path + '.metadata') as f:
            json.dump(self._intervals_queried, f)
        self._do_save()

    def _load(self):
        with open(self._path + '.metadata', 'r') as f:
            self._intervals_queried = json.load(f)
        self._do_load()

    def _do_load(self):
        raise NotImplementedException()

    def _do_save(self):
        raise NotImplementedException()

    def _do_query(self, start_time, end_time):
        raise NotImplementedException()

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        s = 'Time intervals queried:\n'
        for interval in self._iterate_intervals():
            s += '\t' + utils.interval_to_str(interval[0], interval[1]) + '\n'
        return s

    def __eq__(self, other):
        return self._intervals_queried == other._intervals_queried

    def __neq__(self, other):
        return not self.__eq__(other)

def find_start_or_end_time_in_mongo(collection, start_time_field_name, is_start):
    t = collection.find({}, [start_time_field_name]).sort(start_time_field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING).limit(1).next()[start_time_field_name]
    if not is_start:
        t += 1
    return t
