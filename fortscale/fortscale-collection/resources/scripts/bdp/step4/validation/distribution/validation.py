import json
from datetime import datetime
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from bdp_utils.mongo import get_collection_names
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils import mongo

import logging

logger = logging.getLogger('step4.validation')


def serialize_datetime(obj):
    if isinstance(obj, datetime):
        return obj.isoformat()
    raise TypeError('Type not serializable')


def _validate_distribution(host, collection_name):
    pipeline = [
        {
            '$match': {
                'entity_event_value': {
                    '$gt': 0
                },
                'score': {
                    '$gte': 50
                }
            }
        },
        {
            '$project': {
                'end_time_unix': {
                    '$subtract': [
                        '$end_time_unix',
                        {
                            '$mod': [
                                '$end_time_unix',
                                60 * 60 * 24
                            ]
                        }
                    ]
                },
                'entity_event_value': {
                    '$subtract': [
                        '$entity_event_value',
                        {
                            '$mod': [
                                '$entity_event_value',
                                0.01
                            ]
                        }
                    ]
                }
            }
        },

        {
            '$group': {
                '_id': {
                    'value': '$entity_event_value',
                    'end_time_unix': '$end_time_unix'
                },
                'count': {
                    '$sum': 1
                }
            }
        },

        {
            '$project': {
                'value': '$_id.value',
                'end_time': {
                    '$add': [
                        datetime.utcfromtimestamp(0),
                        {
                            '$multiply': [
                                '$_id.end_time_unix',
                                1000
                            ]
                        }
                    ]
                },
                'count': 1,
                '_id': 0
            }
        },

        {
            '$sort': {
                'time': 1,
                'value': 1
            }
        }
    ]
    logger.info(json.dumps(mongo.aggregate(mongo.get_db(host)[collection_name], pipeline),
                           default=serialize_datetime,
                           indent=4))


def validate_distribution(host):
    for collection_name in get_collection_names(host=host, collection_names_regex='^scored___entity_event_'):
        logger.info(collection_name + ':')
        _validate_distribution(host=host, collection_name=collection_name)
        logger.info('')
