from datetime import datetime
import json
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import mongo

import logging

logger = logging.getLogger('step6.validation')


def serialize_datetime(obj):
    if isinstance(obj, datetime):
        return obj.isoformat()
    raise TypeError('Type not serializable')


def validate_alerts_distribution(host):
    pipeline = [
        {
            '$project': {
                'startDate': 1,
                'score': 1,
                'name': 1
            }
        },
        {
            '$project': {
                'name': 1,
                'startDate': {
                    '$subtract': [
                        '$startDate',
                        {
                            '$mod': [
                                '$startDate',
                                60 * 60 * 24 * 1000
                            ]
                        }
                    ]
                },
                'score': {
                    '$subtract': [
                        '$score',
                        {
                            '$mod': [
                                '$score',
                                5
                            ]
                        }
                    ]
                }
            }
        },
        {
            '$group': {
                '_id': {
                    'name': '$name',
                    'time': '$startDate',
                    'score': '$score'
                },
                'count': {
                    '$sum': 1
                }
            }
        },
        {
            '$sort': {
                '_id.time': 1,
                '_id.score': 1
            }
        },
        {
            '$group': {
                '_id': '$_id.name',
                'hist': {
                    '$push': {
                        'time': {
                            '$add': [
                                datetime.utcfromtimestamp(0),
                                '$_id.time'
                            ]
                        },
                        'score': '$_id.score',
                        'count': '$count'
                    }
                }
            }
        }
    ]
    logger.info(json.dumps(mongo.aggregate(mongo.get_db(host).alerts, pipeline),
                           default=serialize_datetime,
                           indent=4))
