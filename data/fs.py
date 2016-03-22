import itertools
import json
import os
import pymongo

import utils
from utils import print_verbose


class F:
    def __init__(self, collection_name):
        self.collection_name = collection_name

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
        fs = list(self._get_collection(mongo_ip).find(
            {
                'contextId': {
                    '$in': users
                }
            }
        ))

        return [[{'value': f['aggregated_feature_value'], 'score': f['score'], 'start_time_unix': f['start_time_unix']}
                 for f in fs_from_same_user]
                for user, fs_from_same_user in itertools.groupby(sorted(fs, key = lambda f: f['contextId']),
                                                                 lambda f: f['contextId'])]

    def _from_strings(self, strings):
        self._fs_by_users = [json.loads(s) for s in strings]

    def _to_strings(self):
        return [json.dumps(fs_by_user) for fs_by_user in self._fs_by_users]

    def iter_fs_by_users(self):
        for user_fs in self._fs_by_users:
            yield user_fs


class Fs():
    def __init__(self, path):
        self._path = path
        if os.path.isfile(path):
            self._load()
        else:
            self._fs = {}

    def query(self, mongo_ip, save_intermediate = False):
        collections_to_query = list(filter(lambda name: not name in self._fs.iterkeys(), self._get_collection_names(mongo_ip)))
        for collection_name in collections_to_query:
            f = F(collection_name)
            f.query(mongo_ip)
            self._fs[collection_name] = f
            if save_intermediate:
                self.save()
        return len(collections_to_query) > 0

    def _get_collection_names(self, mongo_ip):
        db = pymongo.MongoClient(mongo_ip, 27017).fortscale
        if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
            names = db.collection_names()
        else:
            names = [e['name'] for e in db.command('listCollections')['cursor']['firstBatch'] if e['name'].startswith('scored___aggr_event')]
        return filter(lambda name : name.startswith('scored___aggr_event'), names)

    def save(self):
        print_verbose('Saving...')
        with utils.DelayedKeyboardInterrupt():
            with open(self._path, 'w') as output:
                for collection_name, f in self._fs.iteritems():
                    output.write(collection_name + ':\n')
                    for s in f._to_strings():
                        output.write(s + '\n')
                    output.write('\n')
        print_verbose('finished saving')

    def _load(self):
        print_verbose('lodaing...')
        self._fs = {}
        current_collection_name = None
        current_collection_strings = None
        with open(self._path, 'r') as input:
            for i, l in enumerate(input.readlines()):
                if (i + 1) % 100000 == 0:
                    print_verbose('loaded', i + 1, 'users')
                if l.endswith('\n'):
                    l = l[:-1]
                if l.endswith(':'):
                    current_collection_name = l[:-1]
                    current_collection_strings = []
                elif l == '':
                    f = F(current_collection_name)
                    f._from_strings(current_collection_strings)
                    self._fs[current_collection_name] = f
                else:
                    current_collection_strings.append(l)
        print_verbose('finished loading')
        print_verbose(self)

    def __iter__(self):
        return self._fs.itervalues()

    def __repr__(self):
        return seld.__str__()

    def __str__(self):
        return 'Queried collections:' + '\n'.join(['\t' + collection_name for collection_name in self._fs.iterkeys()])
