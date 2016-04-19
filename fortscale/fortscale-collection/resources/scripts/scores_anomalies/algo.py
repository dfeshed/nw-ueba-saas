import copy
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..']))
from automatic_config.common import visualizations
from automatic_config.common.utils.score import score_to_weight_squared_min_50
from automatic_config.common import utils


def show_hists(hists, block=True):
    if type(hists) != list:
        hists = [hists]
    hists = copy.deepcopy(hists)
    for hist in hists:
        for i in xrange(50):
            hist[i] = 0
    max_height = max(max(hist.itervalues()) for hist in hists)
    for hist in hists:
        hist[0] = max_height
    for hist in hists:
        visualizations.show_hist(hist, bins=100, block=hist == hists[-1] and block)


class is_hist:
    class _AnomalyResult:
        def __init__(self, is_anomaly, suspicious_hist, normal_hists, closest_dist, closest_hist_index):
            self._is_anomaly = is_anomaly
            self._suspicious_hist = suspicious_hist
            self._normal_hists = normal_hists
            self._closest_dist = closest_dist
            self._closest_hist_index = closest_hist_index

        def and_if_so_show_it(self):
            if self._is_anomaly:
                print 'distance from hist #' + str(self._closest_hist_index) + ': ' + str(self._closest_dist)
                max_hist = {}
                for hist in self._normal_hists:
                    for score, count in hist.iteritems():
                        max_hist[score] = max(max_hist.get(score, 0), count)
                show_hists([self._normal_hists[self._closest_hist_index], max_hist, self._suspicious_hist])
            return self

        def get_anomaly_strength(self):
            return self._closest_dist

        def __nonzero__(self):
            return self._is_anomaly

        def __repr__(self):
            return str(self.__nonzero__())

        def __str__(self):
            return self.__repr__()

    def __init__(self, hist):
        self._suspicious_hist = hist

    def anomalous_compared_to(self, normal_hists):
        dist = min(enumerate([self._distance(normal_hist, self._suspicious_hist)
                              for normal_hist in normal_hists]), key=lambda index_and_dist: index_and_dist[1])
        is_anomaly = dist[1] > 0.015
        return is_hist._AnomalyResult(is_anomaly=is_anomaly,
                                      suspicious_hist=self._suspicious_hist,
                                      normal_hists=normal_hists,
                                      closest_dist=dist[1],
                                      closest_hist_index=dist[0])

    # @staticmethod
    # def _distance(base_hist, to_hist):
    #     base_noise = is_hist._calc_noise(base_hist)
    #     return math.log(1 + max(0, is_hist._calc_noise(to_hist) - base_noise), 10) / (math.log(1 + base_noise, 10) + 1)

    @staticmethod
    def _distance(base_hist, to_hist):
        overdraft_distance = 5
        stash = sum(score_to_weight_squared_min_50(score) * base_hist.get(score, 0)
                    for score in xrange(100, 100 - overdraft_distance, -1))
        distance = 0
        for score in xrange(100, -1, -1):
            stash += score_to_weight_squared_min_50(score) * base_hist.get(score - overdraft_distance, 0)
            stash_after = stash - score_to_weight_squared_min_50(score) * to_hist.get(score, 0)
            if stash_after < 0:
                # we add to the denominator so that if we're dealing with small numbers
                # we won't get big distance (e.g. - first time we see a score of 100).
                # if we're dealing with big numbers it won't affect the result
                relative_deviation = 1. * abs(stash_after) / (to_hist[score] + 50)
                if relative_deviation > 0.2 or True:  # xxx
                    distance += score_to_weight_squared_min_50(score) * relative_deviation
            stash = max(0, stash_after)
        return distance * 0.01

    # @staticmethod
    # def _calc_noise(hist):
    #     return sum([score_to_weight_squared_min_50(score) * hist[score]
    #                 for score in hist.iterkeys()])


def find_scores_anomalies(table_scores, warming_period, score_field_names, start, end):
    if not set(score_field_names or []).issubset(set(field_scores.field_name for field_scores in table_scores)):
        raise Exception("some of score field names don't exist in impala. Maybe a misspell?")
    for field_scores in filter(lambda field_scores: score_field_names is None or field_scores.field_name in score_field_names,
                               table_scores):
        print
        print '---------------------------'
        print field_scores.field_name
        print '---------------------------'
        filtered_field_scores = filter(lambda day_and_scores_hist: is_inside_interval(day_and_scores_hist[0], (start, end)),
                                       field_scores)
        # day_and_strengths = [(day, is_hist(field_scores[20160319]).anomalous_compared_to([scores_hist]).get_anomaly_strength())
        #                      for day, scores_hist in filtered_field_scores]
        # for day_and_strength in sorted(day_and_strengths, key=lambda day_and_strength: day_and_strength[1]):
        #     print day_and_strength
        # show_hists([field_scores[20160223], field_scores[20160319]])
        # xxx

        min_period_start = find_most_quite_period_start(filtered_field_scores, warming_period)
        print 'warming period starts at', min_period_start

        normal_hists = [day_and_scores_hist[1]
                        for day_and_scores_hist in list(filtered_field_scores)[min_period_start: min_period_start + warming_period]]

        for day, scores_hist in filtered_field_scores:
            if is_hist(scores_hist).anomalous_compared_to(normal_hists).and_if_so_show_it():
                print 'anomaly detected:', day
            else:
                normal_hists.append(scores_hist)


def find_most_quite_period_start(field_scores, warming_period):
    anomaly_strengths = [is_hist(scores_hist).anomalous_compared_to([{}]).get_anomaly_strength()
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
    return (interval[0] is None or utils.time_utils.time_to_epoch(time) >= interval[0]) and \
           (interval[1] is None or utils.time_utils.time_to_epoch(time) < interval[1])
