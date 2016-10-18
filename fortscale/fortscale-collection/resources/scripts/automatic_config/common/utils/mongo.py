import getpass
import pymongo
import re
import sys

import time_utils


def get_db(host):
    db = pymongo.MongoClient(host, 27017 if host != 'upload' else 37017).fortscale
    try:
        # check if an authentication is required
        db.collection_names()
    except Exception:
        if not sys.stdin.isatty():
            user = sys.stdin.readline().strip()
            password = sys.stdin.readline().strip()
        else:
            user = raw_input('Please enter mongo username: ')
            password = getpass.getpass('Please enter mongo password: ')
        db.authenticate(user, password)
    return db


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
        if sample is None:
            continue
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


def update_models_time(logger, host, collection_names_regex, time=None, infer_start_from_collection_names_regex=None):
    if not (time is None) ^ (infer_start_from_collection_names_regex is None):
        raise Exception('exactly one of time or infer_start_from_collection_names_regex should be non None')
    if infer_start_from_collection_names_regex is not None:
        time = get_collections_time_boundary(host=host,
                                             collection_names_regex=infer_start_from_collection_names_regex,
                                             is_start=True)
    time -= 1
    logger.info('moving models back in time to ' + str(time) + '...')
    time = time_utils.get_datetime(time)
    for collection in iter_collections(host=host, collection_names_regex=collection_names_regex):
        res = collection.update({}, {'$set': {'startTime': time, 'endTime': time}}, multi=True)
        logger.info('updated ' + str(res['n']) + ' models in ' + collection.name)
        if res['ok'] != 1:
            logger.error('update failed')
            return False
    return True


def rename_documents(logger, host, collection_names_regex, name_to_new_name_cb):
    renames = 0
    for collection in iter_collections(host=host, collection_names_regex=collection_names_regex):
        new_name = name_to_new_name_cb(collection.name)
        logger.info('renaming ' + collection.name + ' to ' + new_name + '...')
        collection.rename(new_name, dropTarget=True)
        renames += 1
    return renames
