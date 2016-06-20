import pymongo
import re
import sys


def get_db(host):
    return pymongo.MongoClient(host, 27017 if host != 'upload' else 37017).fortscale


def get_all_collection_names(mongo_db):
    if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
        names = mongo_db.collection_names()
    else:
        names = [e['name'] for e in mongo_db.command('listCollections')['cursor']['firstBatch']]
    return names


def aggregate(collection, pipeline):
    query_res = collection.aggregate(pipeline)
    if type(query_res) == dict:
        return query_res['result'] # in some versions of pymongo the result is a dict (with  'ok' and 'result' fields) instead of a cursor
    else:
        return list(query_res)


def get_collection_names(host, collection_names_regex):
    mongo_db = get_db(host)
    return filter(lambda name: re.search(collection_names_regex, name) is not None,
                  get_all_collection_names(mongo_db))


def iter_collections(host, collection_names_regex):
    mongo_db = get_db(host)
    return (mongo_db[collection_name]
            for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex))


def get_collections_time_boundary(host, collection_names_regex, is_start):
    time = sys.maxint if is_start else 0
    mongo_db = get_db(host)
    for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex):
        collection = mongo_db[collection_name]
        sample = collection.find_one()
        field_name_options = ['startTime', 'start_time_unix'] if is_start else ['endTime', 'end_time_unix']
        for field_name in field_name_options:
            if field_name in sample:
                break
        else:
            raise Exception('collection ' + collection_name + ' does not have any of these fields: ' +
                            ', '.join(field_name_options) +
                            '. If it has some other field that should be used - please update this script.')
        time = (min if is_start else max)(time, collection
                                          .find({}, [field_name])
                                          .sort(field_name, pymongo.ASCENDING if is_start else pymongo.DESCENDING)
                                          .limit(1)
                                          .next()[field_name])
    return time


def get_collections_size(host, collection_names_regex, find_query={}):
    mongo_db = get_db(host)
    return sum(mongo_db[collection_name].find(find_query).count()
               for collection_name in get_collection_names(host=host, collection_names_regex=collection_names_regex))
