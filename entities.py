import itertools
import json
import os
import pymongo
import signal
import sys

import config
import hist_utils
import utils
from algorithm import algo_utils
from utils import print_verbose

if config.show_graphs:
    import matplotlib.pyplot as plt

class DelayedKeyboardInterrupt:
    def __enter__(self):
        self._signal_received = None
        self._old_handler = signal.getsignal(signal.SIGINT)
        signal.signal(signal.SIGINT, self._handler)

    def _handler(self, signal, frame):
        self._signal_received = (signal, frame)

    def __exit__(self, type, value, traceback):
        signal.signal(signal.SIGINT, self._old_handler)
        if self._signal_received is not None:
            self._old_handler(*self._signal_received)

class Entities:
    def __init__(self, path, mongo_ip = None):
        self._db = pymongo.MongoClient(mongo_ip, 27017).fortscale if mongo_ip is not None else None
        self._path = path
        if os.path.isfile(path):
            self._load()
        else:
            self._hourly_before_transformation = []
            self._daily_before_transformation = []
            self._intervals_queried = {'hourly': [], 'daily': []}
        self._entity_filter = lambda entity: True

    def _do_query_from_mongo(self, start_time, end_time, entities_type):
        if self._db is None:
            raise Warning('you can query only if you pass a db to the constructor')

        assert entities_type in ['daily', 'hourly']

        if start_time is None or end_time is None:
            raise Exception("start_time and end_time can't be None")
        query = [
            {
                'startTime': {
                    '$lt': end_time
                },
                '$and': [
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
        collection = self._db['entity_event_normalized_username_' + entities_type]
        return collection.find(*query).sort('startTime', pymongo.ASCENDING)

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

    def _iterate_intervals(self, intervals):
        for interval in sorted(intervals, key = lambda interval: interval[0]):
            yield interval

    def _query(self, start_time, end_time, entities_type):
        if start_time >= end_time:
            print_verbose('nothing to query - empty interval (starting at' + utils.timestamp_to_str(start_time) + ')')
            return False

        for interval in self._iterate_intervals(self._intervals_queried[entities_type]):
            if start_time == interval[0] and end_time == interval[1]:
                print_verbose('nothing to query - interval already queried:', utils.interval_to_str(start_time, end_time))
                return False
            if interval[0] <= start_time < interval[1]:
                queried_something = self._query(start_time, interval[1], entities_type)
                queried_something |= self._query(interval[1], end_time, entities_type)
                return queried_something
            if interval[0] < end_time <= interval[1]:
                queried_something = self._query(start_time, interval[0], entities_type)
                queried_something |= self._query(interval[0], end_time, entities_type)
                return queried_something
            if start_time < interval[0] and interval[1] < end_time:
                queried_something = self._query(start_time, interval[0], entities_type)
                queried_something|= self._query(interval[0], end_time, entities_type)
                return queried_something

        print_verbose('Querying interval ' + utils.interval_to_str(start_time, end_time) + '...')

        if entities_type == 'daily':
            entities = self._daily_before_transformation
        else:
            entities = self._hourly_before_transformation

        queried_something = False
        query_res = self._do_query_from_mongo(start_time = start_time, end_time = end_time, entities_type = entities_type)
        start_time = sys.maxint
        end_time = 0
        for e in query_res:
            queried_something = True
            self._clean_entity_json(e)
            entities.append(e)
            start_time = min(start_time, e['startTime'])
            end_time = max(end_time, e['startTime'] + 1)
        if not queried_something:
            return False

        intervals = self._intervals_queried[entities_type]
        intervals.append([start_time, end_time])
        cleaned_intervals = []
        for interval in list(self._iterate_intervals(intervals)):
            if len(cleaned_intervals) > 0:
                last_interval = cleaned_intervals[-1]
                if last_interval[1] == interval[0]:
                    interval[0] = last_interval[0]
                    cleaned_intervals.pop()
            cleaned_intervals.append(interval)
        self._intervals_queried[entities_type] = cleaned_intervals

        return True

    def query(self, start_time, end_time, is_daily = None, should_save_every_day = False):
        if start_time is None:
            start_time = find_start_or_end_time_in_mongo(db = self._db, is_start = True)
        if end_time is None:
            end_time = find_start_or_end_time_in_mongo(db = self._db, is_start = False)

        if should_save_every_day:
            day = 60 * 60 * 24
            queried_something = False
            while start_time <= end_time:
                if self.query(start_time = start_time, end_time = min(start_time + day, end_time)):
                    queried_something = True
                    self.save()
                start_time += day
            return queried_something
        else:
            interval_str = '(interval ' + utils.interval_to_str(start_time, end_time) + ')'
            queried_something = False
            if is_daily is None or is_daily:
                print_verbose('Querying daily ' + interval_str + '...')
                queried_something |= self._query(start_time, end_time, 'daily')
            if is_daily is None or not is_daily:
                print_verbose('Querying hourly ' + interval_str + '...')
                queried_something |= self._query(start_time, end_time, 'hourly')
            return queried_something

    def save(self):
        print_verbose('Saving...')
        with DelayedKeyboardInterrupt():
            with open(self._path, 'w') as f:
                f.write('_intervals_queried:\n')
                f.write(json.dumps(self._intervals_queried) + '\n')
                f.write('_daily_before_transformation:\n')
                for e in self._daily_before_transformation:
                    f.write(json.dumps(e) + '\n')
                f.write('_hourly_before_transformation:\n')
                for e in self._hourly_before_transformation:
                    f.write(json.dumps(e) + '\n')

    def _load(self):
        self._daily_before_transformation = []
        self._hourly_before_transformation = []
        state = None
        print_verbose('start loading...')
        with open(self._path, 'r') as f:
            for l in f:
                if l.endswith('\n'):
                    l = l[:-1]
                if l in ['_intervals_queried:', '_daily_before_transformation:', '_hourly_before_transformation:']:
                    state = l[:-1]
                else:
                    j = json.loads(l)
                    if state == '_intervals_queried':
                        self._intervals_queried = j
                    elif state == '_daily_before_transformation':
                        self._daily_before_transformation.append(j)
                        if len(self._daily_before_transformation) % 100000 == 0:
                            print_verbose('loaded', len(self._daily_before_transformation), 'daily entities')
                    elif state == '_hourly_before_transformation':
                        self._hourly_before_transformation.append(j)
                        if len(self._hourly_before_transformation) % 100000 == 0:
                            print_verbose('loaded', len(self._hourly_before_transformation), 'hourly entities')
                    else:
                        raise Exception('unknown state - ' + state)
        print_verbose('finished loading')

    def set_entities_filter(self, entity_filter):
        self._entity_filter = entity_filter

    def iterate(self, is_daily):
        for e in filter(self._entity_filter, self._daily_before_transformation if is_daily else self._hourly_before_transformation):
            yield e

    def group_by_day(self, is_daily):
        return [list(entities_starting_in_same_day)
                for day, entities_starting_in_same_day in itertools.groupby(sorted(self.iterate(is_daily), key = lambda entity: entity['startTime']),
                                                                            lambda entity: entity['startTime'] / (60 * 60 * 24))]

    def __repr__(self):
        return self.__str__()

    def __str__(self):
        s = 'Time intervals queried:\n'
        for entities_type in ['daily', 'hourly']:
            s += entities_type + ':\n'
            for interval in self._iterate_intervals(self._intervals_queried[entities_type]):
                s += '\t' + utils.interval_to_str(interval[0], interval[1]) + '\n'
        s += str(len(self._daily_before_transformation)) + ' daily entity events with positive value\n'
        s += str(len(self._hourly_before_transformation)) + ' hourly entity events with positive value\n'
        return s

    def __eq__(self, other):
        return self._intervals_queried == other._intervals_queried and \
               self._hourly_before_transformation == other._hourly_before_transformation and \
               self._daily_before_transformation == other._daily_before_transformation

def hist_without_small_scores(hist, min_score):
    return dict((score, count) for score, count in hist.iteritems() if score >= min_score)

def show_hist(hist, min_score = 0, maxx = 100):
    hist = hist_without_small_scores(hist, min_score)
    print_verbose('Area under histogram:', sum(hist.itervalues()))
    print_verbose(hist)
    if not config.show_graphs:
        return
    if len(hist) < 2:
        # if hist has only one entry, matplotlib will fail to plot it:
        hist[0] = hist.get(0, 0.00001)
        hist[1] = hist.get(1, 0.00001)
    fig, ax = plt.subplots()
    fig.set_figwidth(20)
    fig.set_figheight(3)
    plt.xlim(0, max(maxx, max(hist.iterkeys())))
    plt.hist([val for val in hist],
             weights = list(hist.itervalues()),
             bins = 1000,
             histtype = 'stepfilled')
    plt.xlabel('score', fontsize = 20)
    plt.ylabel('count', fontsize = 20)
    plt.show()

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
            show_hist(hist, min_score)

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

def find_start_or_end_time_in_mongo(db, is_start):
    t = (min if is_start else max)([collection.find({}, ['startTime']).sort('startTime', pymongo.ASCENDING if is_start else pymongo.DESCENDING).limit(1).next()['startTime']
                                    for collection in [db.entity_event_normalized_username_daily, db.entity_event_normalized_username_hourly]])
    if not is_start:
        t += 1
    return t