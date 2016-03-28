import itertools
import json
import pymongo
import sys
from common import algo_utils
from common import utils
from common import visualizations
from common.data import Data
from common.utils import print_verbose

from .. import hist_utils


class SingleTypeEntities(Data):
    def __init__(self, dir_path, collection):
        self._entities_before_transformation = []
        self._entity_filter = lambda entity: True
        Data.__init__(self, dir_path, collection, start_time_field_name = 'startTime')

    def _do_query(self, start_time, end_time):
        query = [
            {
                '$and': [
                    {
                        'startTime': {
                            '$lt': end_time
                        }
                    },
                    {
                        'startTime': {
                            '$gte': start_time
                        }
                    }
                ]
            }, [
                'startTime',
                'contextId',
                'includedAggrFeatureEvents.aggregated_feature_type',
                'includedAggrFeatureEvents.aggregated_feature_name',
                'includedAggrFeatureEvents.aggregated_feature_value',
                'includedAggrFeatureEvents.score'
            ]
        ]
        query_res = self._collection.find(*query).sort('startTime', pymongo.ASCENDING)
        start_time = sys.maxint
        end_time = 0
        for e in query_res:
            if self._clean_entity_json(e):
                self._entities_before_transformation.append(e)
            start_time = min(start_time, e['startTime'])
            end_time = max(end_time, e['startTime'] + 1)
        if end_time == 0:
            return None
        return start_time, end_time

    def _clean_entity_json(self, e):
        e['entityId'] = str(e['_id'])
        del e['_id']
        e['startTime'] = int(e['startTime'])
        for a in e['includedAggrFeatureEvents']:
            for s in ['type', 'name', 'value']:
                a[s] = a['aggregated_feature_' + s]
                del a['aggregated_feature_' + s]
            if a['type'] == 'P':
                a['score'] = a['value']
        e['includedAggrFeatureEvents'] = [a for a in e['includedAggrFeatureEvents'] if a['score'] > 0]
        return len(e['includedAggrFeatureEvents']) > 0

    def _do_save(self):
        print_verbose('saving...')
        with utils.FileWriter(self._path) as f:
            for e in self._entities_before_transformation:
                f.write(json.dumps(e) + '\n')
        print_verbose('finished saving')

    def _do_load(self):
        print_verbose('loading...')
        self._entities_before_transformation = []
        with open(self._path, 'r') as f:
            for l in f:
                if l.endswith('\n'):
                    l = l[:-1]
                self._entities_before_transformation.append(json.loads(l))
                if len(self._entities_before_transformation) % 100000 == 0:
                    print_verbose('loaded', len(self._entities_before_transformation), 'entities')
        print_verbose('finished loading')

    def set_entities_filter(self, entity_filter):
        self._entity_filter = entity_filter

    def iterate(self):
        return filter(self._entity_filter, self._entities_before_transformation)

    def group_by_day(self):
        return [list(entities_starting_in_same_day)
                for day, entities_starting_in_same_day in itertools.groupby(sorted(self.iterate(), key = lambda entity: entity['startTime']),
                                                                            lambda entity: entity['startTime'] / (60 * 60 * 24))]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        s = Data.__str__(self)
        s += str(len(self._entities_before_transformation)) + ' entity events with positive value\n'
        return s

    def __eq__(self, other):
        return Data.__eq__(self, other) and \
               self._entities_before_transformation == other._entities_before_transformation


class Entities:
    def __init__(self, dir_path, mongo_ip = None):
        self._daily = SingleTypeEntities(dir_path, pymongo.MongoClient(mongo_ip, 27017).fortscale.entity_event_normalized_username_daily)
        self._hourly = SingleTypeEntities(dir_path, pymongo.MongoClient(mongo_ip, 27017).fortscale.entity_event_normalized_username_hourly)

    def query(self, start_time, end_time, is_daily = None, should_save_every_day = False):
        if is_daily or is_daily is None:
            print_verbose('Querying daily...')
            self._daily.query(start_time, end_time, should_save_every_day)
        if not is_daily or is_daily is None:
            print_verbose('Querying hourly...')
            self._hourly.query(start_time, end_time, should_save_every_day)

    def save(self):
        with utils.DelayedKeyboardInterrupt():
            self._daily.save()
            self._hourly.save()

    def set_entities_filter(self, entity_filter):
        self._daily.set_entities_filter(entity_filter)
        self._hourly.set_entities_filter(entity_filter)

    def iterate(self, is_daily):
        return (self._daily if is_daily else self._hourly).iterate()

    def group_by_day(self, is_daily):
        return (self._daily if is_daily else self._hourly).group_by_day()

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        return 'daily:\n' + self._daily.__str__() + '\n\nhourly:' + self._hourly.__str__()

    def __eq__(self, other):
        return self._daily == other._daily and self._hourly == other._hourly

def hist_without_small_scores(hist, min_score):
    return dict((score, count) for score, count in hist.iteritems() if score >= min_score)

class FsAndPs:
    def __init__(self, entities):
        self.daily = {'F': {}, 'P': {}}
        for e in entities.iterate(True):
            for a in e['includedAggrFeatureEvents']:
                self.daily[a['type']][a['name']] = self.daily[a['type']].get(a['name'], {})
                score = algo_utils.get_indicator_score(a)
                self.daily[a['type']][a['name']][score] = self.daily[a['type']][a['name']].get(score, 0) + 1
        self.hourly = {'F': {}, 'P': {}}
        for e in entities.iterate(False):
            for a in e['includedAggrFeatureEvents']:
                self.hourly[a['type']][a['name']] = self.hourly[a['type']].get(a['name'], {})
                score = algo_utils.get_indicator_score(a)
                self.hourly[a['type']][a['name']][score] = self.hourly[a['type']][a['name']].get(score, 0) + 1

    def iterate(self, is_daily, min_score = 0, verbose = False):
        for pf_type in ['F', 'P']:
            if verbose:
                print_verbose()
                print_verbose('-----------------------------')
                print_verbose('-------------', pf_type, '-------------')
                print_verbose('-----------------------------')
                print_verbose()
            for hist_name, hist in sorted((self.daily if is_daily else self.hourly).get(pf_type, {}).iteritems(),
                                          key = lambda hist_name_and_hist : hist_name_and_hist[0]):
                if verbose:
                    print_verbose(hist_name + ':')
                hist = hist_without_small_scores(hist, min_score)
                if len(hist) == 1 and hist.has_key(0):
                    if verbose:
                        print_verbose('That is a weird histogram... You should check it out!\n\n')
                else:
                    yield pf_type, hist_name, hist

    def show(self, is_daily, min_score = 0):
        print_verbose('histograms ( ignoring scores smaller than', min_score, ')')
        for pf_type, name, hist in self.iterate(is_daily, min_score, verbose = True):
            print_verbose()
            visualizations.show_hist(hist_without_small_scores(hist, min_score))

    def calc_median_hist(self, is_daily, k = 4):
        scores_to_counts = {}
        num_of_hists = 0
        for pf_type, name, hist in self.iterate(is_daily):
            num_of_hists += 1
            hist = hist_utils.normalize_hist_by_unreliability(hist)
            for score, count in hist.iteritems():
                scores_to_counts[score] = scores_to_counts.get(score, [])
                scores_to_counts[score].append(count)
        res = {}
        for score, counts in scores_to_counts.iteritems():
            res[score] = sorted(counts + [0] * (num_of_hists - len(counts)))[num_of_hists / k]
        return res
