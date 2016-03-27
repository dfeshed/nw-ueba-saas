import itertools
import json
import os
import pymongo
from common.utils import print_verbose

from common import utils


class F:
    def __init__(self, dir_path, collection_name):
        self.collection_name = collection_name
        self._path = os.path.join(dir_path, self.collection_name)
        if os.path.isfile(self._path):
            self._load()
        else:
            self._fs_by_users = []

    def query(self, mongo_ip):
        print_verbose('querying ' + self.collection_name + '...')
        self._fs_by_users = self._find_fs_by_users(mongo_ip, self._find_interesting_users(mongo_ip))

    def _get_collection(self, mongo_ip):
        return pymongo.MongoClient(mongo_ip, 27017).fortscale[self.collection_name]

    def _find_interesting_users(self, mongo_ip):
        return list(set(f['contextId']
                        for f in self._get_collection(mongo_ip).find(
            {
                'score': {
                    '$gt': 0
                }
            }
        )))

    def _find_fs_by_users(self, mongo_ip, users):
        print_verbose('querying fs of', len(users), 'users...')
        CHUNK_SIZE = 500
        users_chunks = [users[i:i + CHUNK_SIZE] for i in xrange(0, len(users), CHUNK_SIZE)]
        fs = []
        for i, users_chunk in enumerate(users_chunks):
            print_verbose('querying users chunk %d/%d (%d%%)...' % (i + 1, len(users_chunks), int(100. * i / len(users_chunks))))
            for tries in xrange(3):
                try:
                    fs += list((self._get_collection(mongo_ip).find(
                        {
                            'contextId': {
                                '$in': users_chunk
                            },
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

        return [[{'value': f['aggregated_feature_value'], 'score': f['score'], 'start_time_unix': f['start_time_unix']}
                 for f in fs_from_same_user]
                for user, fs_from_same_user in itertools.groupby(sorted(fs, key = lambda f: f['contextId']),
                                                                 lambda f: f['contextId'])]

    def save(self):
        print_verbose('saving...')
        with utils.FileWriter(self._path) as output:
            for fs_by_user in self._fs_by_users:
                output.write(json.dumps(fs_by_user) + '\n')
        print_verbose('finished saving')

    def _load(self):
        print_verbose('loading...')
        self._fs_by_users = []
        with open(self._path, 'r') as input:
            for i, l in enumerate(input.readlines()):
                if (i + 1) % 100000 == 0:
                    print_verbose('loaded', i + 1, 'users')
                self._fs_by_users.append(json.loads(l))
        print_verbose('finished loading')

    def iter_fs_by_users(self):
        for user_fs in self._fs_by_users:
            yield user_fs


class Fs():
    def __init__(self, dir_path):
        self._dir_path = dir_path

    def query(self, mongo_ip):
        collections_to_query = list(filter(lambda name: not name in self._get_loaded_collection_names(),
                                           self._get_collection_names(mongo_ip)))
        for collection_name in collections_to_query:
            f = F(self._dir_path, collection_name)
            f.query(mongo_ip)
            f.save()
            print_verbose()
        return len(collections_to_query) > 0

    def _get_loaded_collection_names(self):
        if os.path.exists(self._dir_path):
            return [os.path.splitext(file_name)[0] for file_name in os.listdir(self._dir_path)]
        else:
            return []

    def _get_collection_names(self, mongo_ip):
        db = pymongo.MongoClient(mongo_ip, 27017).fortscale
        if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
            names = db.collection_names()
        else:
            names = [e['name'] for e in db.command('listCollections')['cursor']['firstBatch'] if e['name'].startswith('scored___aggr_event')]
        return filter(lambda name : name.startswith('scored___aggr_event'), names)

    def __iter__(self):
        for collection_name in self._get_loaded_collection_names():
            yield F(self._dir_path, collection_name)

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return 'Queried collections:\n' + '\n'.join(['\t' + collection_name
                                                     for collection_name in self._get_loaded_collection_names()])
