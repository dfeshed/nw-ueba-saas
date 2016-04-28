import copy
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from automatic_config.common import visualizations
from automatic_config.common.utils.score import score_to_weight_filter_below_10 as score_to_weight
from automatic_config.common import utils
ANOMALIES_STRENGTH_THRESHOLD = 0.025


def show_hists(name_and_hists):
    name_and_hists = copy.deepcopy(name_and_hists)
    for name, hist in name_and_hists:
        for i in xrange(100):
            if score_to_weight(i) == 0:
                hist[i] = 0
    max_height = max(max(hist.itervalues()) for name, hist in name_and_hists)
    for name, hist in name_and_hists:
        hist[0] = max_height
    for name, hist in name_and_hists:
        visualizations.show_hist(hist, name=name, bins=100, block=hist == name_and_hists[-1][1])


class is_hist:
    class _AnomalyResult:
        def __init__(self, is_anomaly, suspicious_hist, normal_hists, closest_anomalies_strength, closest_hist_index):
            self._is_anomaly = is_anomaly
            self._suspicious_hist = suspicious_hist
            self._normal_hists = normal_hists
            self._closest_anomalies_strength = closest_anomalies_strength
            self._closest_hist_index = closest_hist_index

        def and_if_so_show_it(self):
            if self._is_anomaly:
                print 'distance from hist #' + str(self._closest_hist_index) + ': ' + \
                      str(self._closest_anomalies_strength)
                max_hist = {}
                for hist in self._normal_hists:
                    for score, count in hist.iteritems():
                        max_hist[score] = max(max_hist.get(score, 0), count)
                show_hists([
                    ('max over normal hists', max_hist),
                    ('closest normal hist', self._normal_hists[self._closest_hist_index]),
                    ('suspicious hist', self._suspicious_hist)
                ])
            return self

        def get_anomalies_strength(self):
            return self._closest_anomalies_strength

        def __nonzero__(self):
            return self._is_anomaly

        def __repr__(self):
            return str(self.__nonzero__())

        def __str__(self):
            return self.__repr__()

    def __init__(self, hist):
        self._suspicious_hist = hist

    def anomalous_compared_to(self, normal_hists):
        strength = min(enumerate([self._calc_anomalies_strength(normal_hist=normal_hist,
                                                                suspicious_hist=self._suspicious_hist)
                              for normal_hist in normal_hists]), key=lambda index_and_strength: index_and_strength[1])
        is_anomaly = strength[1] >= ANOMALIES_STRENGTH_THRESHOLD
        return is_hist._AnomalyResult(is_anomaly=is_anomaly,
                                      suspicious_hist=self._suspicious_hist,
                                      normal_hists=normal_hists,
                                      closest_anomalies_strength=strength[1],
                                      closest_hist_index=strength[0])

    @staticmethod
    def _calc_anomalies_strength(normal_hist, suspicious_hist):
        overdraft_distance = 5
        stash = sum(score_to_weight(score) * normal_hist.get(score, 0)
                    for score in xrange(100, 100 - overdraft_distance, -1))
        distance = 0
        normal_median = is_hist._calc_hist_median(normal_hist)
        for score in xrange(100, -1, -1):
            stash += score_to_weight(score) * normal_hist.get(score - overdraft_distance, 0)
            stash_after = stash - score_to_weight(score) * suspicious_hist.get(score, 0)
            if stash_after < 0:
                # we add to the denominator so that if we're dealing with small numbers
                # we won't get big distance (e.g. - first time we see a score of 100).
                # if we're dealing with big numbers it won't affect the result
                relative_deviation = 1. * abs(stash_after) / (normal_median + normal_hist.get(score, 0) + 1)
                if abs(stash_after) > 2 * normal_median and relative_deviation > 0.2:
                    distance += score_to_weight(score) * relative_deviation
            stash = max(0, stash_after)
        return distance * 0.01

    @staticmethod
    def _calc_hist_median(hist):
        sorted_counts = sorted(count
                               for score, count in hist.iteritems()
                               if score_to_weight(score) > 0)

        if len(sorted_counts) > 0:
            return sorted_counts[len(sorted_counts) / 2]
        return 0


def find_most_quite_period_start(field_scores, warming_period):
    anomaly_strengths = [is_hist(scores_hist).anomalous_compared_to([{}]).get_anomalies_strength()
                         for day, scores_hist in list(field_scores)[:warming_period*2]]

    min_period_anomaly_strength = sys.maxint
    min_period_start = None
    for warming_period_start in xrange(len(anomaly_strengths) - warming_period + 1):
        period_anomaly_strength = sum(anomaly_strengths[i]
                                      for i in xrange(warming_period_start, warming_period_start + warming_period))
        if period_anomaly_strength < min_period_anomaly_strength:
            min_period_anomaly_strength = period_anomaly_strength
            min_period_start = warming_period_start
    return min_period_start


def is_inside_interval(time, interval):
    return (interval[0] is None or utils.time_utils.get_epoch(time) >= interval[0]) and \
           (interval[1] is None or utils.time_utils.get_epoch(time) < interval[1])


def find_scores_anomalies(table_scores, warming_period, score_field_names, start, end):
    if not set(score_field_names or []).issubset(set(field_scores.field_name for field_scores in table_scores)):
        raise Exception("some of score field names don't exist in impala. Maybe a misspell?")
    for field_scores in filter(lambda field_scores: score_field_names is None or field_scores.field_name in score_field_names,
                               table_scores):
        print
        print '---------------------------'
        print field_scores.field_name
        print '---------------------------'
        field_scores = filter(lambda day_and_scores_hist: is_inside_interval(day_and_scores_hist[0], (start, end)),
                              field_scores)

        min_period_start = find_most_quite_period_start(field_scores, warming_period)
        print 'warming period starts at', min_period_start

        normal_hists = [day_and_scores_hist[1]
                        for day_and_scores_hist in list(field_scores)[min_period_start: min_period_start + warming_period]]

        for day, scores_hist in field_scores[min_period_start + warming_period:]:
            print 'analyzing ' + day + '...'
            if is_hist(scores_hist).anomalous_compared_to(normal_hists).and_if_so_show_it():
                print 'anomaly detected:', day
                print
            else:
                normal_hists.append(scores_hist)
