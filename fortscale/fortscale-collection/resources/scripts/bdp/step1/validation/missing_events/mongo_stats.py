import pymongo
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo
from automatic_config.common.utils import time_utils


def _get_db(host):
    return pymongo.MongoClient(host, 27017 if host != 'upload' else 37017).fortscale


def get_job_report(host, job_name, data_type_regex, start, end):
    start, end = map(lambda time: time_utils.get_datetime(time).strftime("%Y-%m-%d %H:%M:%S"), [start, end])
    query_res = mongo.aggregate(_get_db(host).job_report, [
        {
            '$match': {
                'jobName': job_name,
                'dataReceived.1.valueType': {
                    '$gte': start
                },
                'dataReceived.2.valueType': {
                    '$lt': end
                }
            }
        },
        {
            '$unwind': '$dataReceived'
        },
        {
            '$match': {
                'dataReceived.dataType': {
                    '$regex': data_type_regex
                }
            }
        },
        {
            '$group': {
                '_id': '$dataReceived.dataType',
                'count': {'$sum': '$dataReceived.value'}
            }
        }
    ])
    return dict((entry['_id'], entry['count']) for entry in query_res)
