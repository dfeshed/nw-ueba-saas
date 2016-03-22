import itertools
import os
import pymongo
from bson import json_util

import config
import hist_utils
import utils
from algorithm import algo_utils
from utils import print_verbose

if config.show_graphs:
    import matplotlib.pyplot as plt


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

        for f in fs:
            f['name'] = f['aggregated_feature_name']
            del f['aggregated_feature_name']
            f['value'] = f['aggregated_feature_value']
            del f['aggregated_feature_value']

        return [list(fs_from_same_user)
                for user, fs_from_same_user in itertools.groupby(sorted(fs, key = lambda f: f['contextId']),
                                                                 lambda f: f['contextId'])]

    def find_positive_values_hists(self, max_bad_value_diff, score_to_weight):
        false_positives_values_hist = {}
        true_positives_values_hist = {}
        for user_fs in self._fs_by_users:
            user_history = []
            for f in sorted(user_fs, key = lambda f: f['start_time_unix']):
                weight = score_to_weight(algo_utils.get_indicator_score(f))
                if len(user_history) > 0 and weight > 0:
                    if abs(sum(user_history) / len(user_history) - f['value']) <= max_bad_value_diff:
                        hist = false_positives_values_hist
                    else:
                        hist = true_positives_values_hist
                    hist[f['value']] = hist.get(f['value'], 0) + weight
                user_history.append(f['value'])
        return {
            False: false_positives_values_hist,
            True: true_positives_values_hist
        }


class Fs():
    def __init__(self, path):
        self._path = path
        if os.path.isfile(path):
            self._load()
        else:
            self._fs = {}

    def query(self, mongo_ip, save_intermediate = False):
        collections_to_query = list(filter(lambda name: not name in self._fs.iterkeys(), self._get_all_f_collection_names(mongo_ip)[:1]))
        for collection_name in collections_to_query:
            f = F(collection_name)
            f.query(mongo_ip)
            self._fs[collection_name] = f
            if save_intermediate:
                self.save()
        return len(collections_to_query) > 0

    def _get_all_f_collection_names(self, mongo_ip):
        db = pymongo.MongoClient(mongo_ip, 27017).fortscale
        if pymongo.version_tuple[0] > 2 or (pymongo.version_tuple[0] == 2 and pymongo.version_tuple[1] > 7):
            names = db.collection_names()
        else:
            names = [e['name'] for e in db.command('listCollections')['cursor']['firstBatch'] if e['name'].startswith('scored___aggr_event')]
        return filter(lambda name : name.startswith('scored___aggr_event'), names)

    def save(self):
        print_verbose('Saving...')
        with utils.DelayedKeyboardInterrupt():
            s = dict((collection_name, f._fs_by_users) for collection_name, f in self._fs.iteritems())
            with open(self._path, 'w') as f:
                f.write(json_util.dumps(s, f))
        print_verbose('finished saving')

    def _load(self):
        print_verbose('loading...')
        with open(self._path, 'r') as f:
            s = json_util.loads(f.read())
        self._fs = {}
        for collection_name, f_str in s.iteritems():
            f = F(collection_name)
            f._fs_by_users = f_str
            self._fs[collection_name] = f
        print_verbose('finished loading')

    def __iter__(self):
        return self._fs.itervalues()

def plot_roc_curve():
    plt.figure()
    plt.plot(fpr[2], tpr[2], label='ROC curve (area = %0.2f)' % roc_auc[2])
    plt.plot([0, 1], [0, 1], 'k--')
    plt.xlim([0.0, 1.0])
    plt.ylim([0.0, 1.05])
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('Receiver operating characteristic example')
    plt.legend(loc="lower right")
    plt.show()


def calc_min_value_for_not_reduce(f, score_to_weight):
    hists = f.find_positive_values_hists(max_bad_value_diff = 1, score_to_weight = score_to_weight)
    print_verbose(f.collection_name + ':')
    print_verbose('true positives:')
    hist_utils.show_hist(hists[True])
    print_verbose('false positives:')
    hist_utils.show_hist(hists[False])

    total_count = sum(hist.itervalues())
    if total_count == 0:
        return None
    cumsum = 0
    prev_count = 0
    max_count_seen = 0
    peek_start = None
    min_value_for_not_reduce = None
    for value, count in sorted(hist.iteritems(), key = lambda value_and_count: value_and_count[0]):
        # don't overdo it (we don't want to reduce everything)
        if 1. * cumsum / total_count > 0.5 and (peek_start is None or value - peek_start > 1):
            break

        # don't bother if there are not enough candidates to be considered as noise:
        is_enough_noise_absolutely = count > 10

        if peek_start is None and is_enough_noise_absolutely and 1. * (count - max_count_seen) / total_count > 0.15:
            peek_start = value
        if peek_start is not None and 1. * count / (prev_count + 1) < 0.85:
            min_value_for_not_reduce = value
            break

        cumsum += count
        prev_count = count
        max_count_seen = max(max_count_seen, count)

    return min_value_for_not_reduce

def calc_min_value_for_not_reduce_for_hists(score_to_weight):
    print
    print '----------------------------------------------------------------------'
    print '--------------------- min_value_for_not_reduce  ----------------------'
    print '----------------------------------------------------------------------'
    fs = Fs('fs.json')
    if fs.query(config.mongo_ip):
        fs.save()
    for f in fs:
        print_verbose()
        min_value_for_not_reduce = calc_min_value_for_not_reduce(f, score_to_weight = score_to_weight)
        if min_value_for_not_reduce is not None:
            print f.collection_name + ':', min_value_for_not_reduce
