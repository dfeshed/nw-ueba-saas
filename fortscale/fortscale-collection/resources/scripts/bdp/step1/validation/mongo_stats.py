import pymongo
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from automatic_config.common.utils import mongo


def _get_db(host):
    return pymongo.MongoClient(host, 27017).fortscale


def get_num_of_processed_and_filtered_events(host, job_report_job_name):
    query_res = mongo.aggregate(_get_db(host).job_report, [
        {
            '$match': {
                'jobName': job_report_job_name
            }
        },
        {
            '$unwind': '$dataReceived'
        },
        {
            '$group': {
                '_id': '$dataReceived.dataType',
                'count': {'$sum': '$dataReceived.value'}
            }
        },
        {
            '$project': {
                'dataType': '$_id',
                'count': 1,
                '_id': 0
            }
        }
    ])
    return [entry['count']
            for entry in query_res
            if 'total events' in entry['dataType'].lower()][0]
