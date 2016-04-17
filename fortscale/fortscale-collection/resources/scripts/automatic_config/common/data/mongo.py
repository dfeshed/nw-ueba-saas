import pymongo

from data import Data, DataCollection


class MongoData(Data):
    def __init__(self, dir_path, collection_name, db, start_time_field_name):
        self._collection = db[collection_name]
        self._start_time_field_name = start_time_field_name
        Data.__init__(self, dir_path, self._collection.name)

    def _find_boundary_time(self, is_start):
        time = self._collection \
            .find({}, [self._start_time_field_name]) \
            .sort(self._start_time_field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING) \
            .limit(1) \
            .next()[self._start_time_field_name]
        if not is_start:
            time += 1
        return time


class MongoDataCollection(DataCollection):
    def __init__(self, dir_path, data_class, db):
        self._db = db
        DataCollection.__init__(self, dir_path, data_class, db)

    def _get_all_data_names(self):
        return filter(lambda name: name.startswith('scored___aggr_event'), mongo.get_all_collection_names(self._db))
