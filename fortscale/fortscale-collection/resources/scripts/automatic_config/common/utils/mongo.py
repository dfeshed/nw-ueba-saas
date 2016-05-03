import pymongo


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
