import sys
import os

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))
from common.utils.mongo import get_collections_time_boundary


def init():
    import os
    import sys
    sys.path.append(__path__[0])

    configs = [__import__(os.path.splitext(filename)[0])
               for filename in os.listdir(__path__[0])
               if filename.endswith('.py') and not filename.startswith('__')]
    for config in sorted(filter(lambda config: config.order is not None, configs), key = lambda config: config.order):
        for property in filter(lambda p: p != 'order' and not p.startswith('__'), dir(config)):
            globals()[property] = getattr(config, property)

init()
del init


def get_start_time():
    return START_TIME or get_collections_time_boundary(host=mongo_ip,
                                                       collection_names_regex='^aggr_',
                                                       is_start=True)


def get_end_time():
    return END_TIME or get_collections_time_boundary(host=mongo_ip,
                                                     collection_names_regex='^aggr_',
                                                     is_start=False)

