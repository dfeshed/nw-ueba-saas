import itertools
import json
import os
import pymongo
from common import utils
from common.data.mongo import MongoData
from common.utils import print_verbose


class F(MongoData):
    _DELIMITER = '--->'

    def __init__(self, dir_path, collection):
        self._users_to_fs = {}
        MongoData.__init__(self, dir_path, collection, start_time_field_name = 'start_time_unix')
        self._interesting_users = self._find_interesting_users()

    def _do_query(self, start_time, end_time):
        users_to_fs = self._find_users_to_fs(self._interesting_users, start_time, end_time)

        for user, fs in users_to_fs.iteritems():
            self._users_to_fs[user] = self._users_to_fs.get(user, []) + fs

        times = sum([[a['start_time_unix'] for a in fs] for fs in users_to_fs.itervalues()], [])
        if len(times) == 0:
            return None
        return min(times), max(times)

    def _find_interesting_users(self):
        return list(set(f['contextId'] for f in self._collection.find(
            {
                'score': {
                    '$gt': 0
                }
            }
        )))

    def _find_users_to_fs(self, users, start_time, end_time):
        print_verbose('querying fs of', len(users), 'users...')
        CHUNK_SIZE = 500
        users_chunks = [users[i:i + CHUNK_SIZE] for i in xrange(0, len(users), CHUNK_SIZE)]
        fs = []
        for i, users_chunk in enumerate(users_chunks):
            print_verbose('querying users chunk %d/%d (%d%%)...' % (i + 1, len(users_chunks), int(100. * i / len(users_chunks))))
            for tries in xrange(3):
                try:
                    fs += list((self._collection.find(
                        {
                            '$and': [
                                {
                                    'contextId': {
                                        '$in': users_chunk
                                    }
                                },
                                {
                                    'start_time_unix': {
                                        '$lt': end_time
                                    }
                                },
                                {
                                    'start_time_unix': {
                                        '$gte': start_time
                                    }
                                }
                            ]
                        },
                        ['contextId', 'start_time_unix', 'aggregated_feature_value', 'score']
                    )))
                    break
                except pymongo.errors.CursorNotFound, e:
                    print_verbose('failed')
            else:
                print_verbose('failed after', tries, 'tries:')
                raise e
            print_verbose('in total queried', len(fs), 'fs')
        print_verbose('finished querying')

        return dict((user, [{'value': f['aggregated_feature_value'], 'score': f['score'], 'start_time_unix': f['start_time_unix']}
                            for f in fs_from_same_user])
                    for user, fs_from_same_user in itertools.groupby(sorted(fs, key = lambda f: f['contextId']),
                                                                     lambda f: f['contextId']))

    def _do_save(self):
        print_verbose('saving...')
        with utils.FileWriter(self._path) as output:
            for user, fs in self._users_to_fs.iteritems():
                output.write(user + F._DELIMITER + json.dumps(fs) + '\n')
        print_verbose('finished saving')

    def _do_load(self):
        print_verbose('loading...')
        self._users_to_fs = {}
        with open(self._path, 'r') as input:
            for l in input.readlines():
                user, fs = l.split(F._DELIMITER)
                self._users_to_fs[user] = json.loads(fs)
                if (len(self._users_to_fs)) % 100000 == 0:
                    print_verbose('loaded', len(self._users_to_fs), 'users')
        print_verbose('finished loading')

    def iter_fs_by_users(self):
        for user_fs in self._users_to_fs.itervalues():
            yield user_fs


class Fs:
    def __init__(self, dir_path, mongo_ip):
        self._dir_path = dir_path
        self._mongo_ip = mongo_ip
        
    def _get_db(self):
        return pymongo.MongoClient(self._mongo_ip, 27017).fortscale

    def query(self, start_time, end_time, should_save_every_day = False):
        queried_something = False
        for collection_name in self._get_collection_names():
            f = F(self._dir_path, self._get_db()[collection_name])
            queried_something |= f.query(start_time, end_time, should_save_every_day)
            f.save()
            print_verbose()
        return queried_something

    def _get_loaded_collection_names(self):
        if os.path.exists(self._dir_path):
            return [os.path.splitext(file_name)[0] for file_name in os.listdir(self._dir_path)]
        else:
            return []

    def _get_collection_names(self):
        db = self._get_db()
        if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
            names = db.collection_names()
        else:
            names = [e['name'] for e in db.command('listCollections')['cursor']['firstBatch']]
        return filter(lambda name : name.startswith('scored___aggr_event'), names)

    def __iter__(self):
        for collection_name in self._get_loaded_collection_names():
            yield F(self._dir_path, self._get_db()[collection_name])

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return 'Queried collections:\n' + '\n'.join(['\t' + collection_name
                                                     for collection_name in self._get_loaded_collection_names()])
