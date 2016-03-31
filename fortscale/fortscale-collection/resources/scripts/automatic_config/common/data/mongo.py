import os
import pymongo

from data import Data


class MongoData(Data):
    def __init__(self, dir_path, collection, start_time_field_name):
        self._collection = collection
        self._start_time_field_name = start_time_field_name
        Data.__init__(self, os.path.join(dir_path, collection.name))

    def _find_boundary_time(self, is_start):
        time = self._collection \
            .find({}, [self._start_time_field_name]) \
            .sort(self._start_time_field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING) \
            .limit(1) \
            .next()[self._start_time_field_name]
        if not is_start:
            time += 1
        return time
