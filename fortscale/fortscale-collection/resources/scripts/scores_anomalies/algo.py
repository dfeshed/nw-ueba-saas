import copy
import os
import sys

sys.path.append(os.path.sep.join([os.path.dirname(__file__), '..']))
from automatic_config.common import visualizations
from automatic_config.common.utils.score import score_to_weight_squared_min_50


def show_hist(hist, block=True):
    hist = copy.deepcopy(hist)
    hist[0] = 300
    for i in xrange(1, 50):
        hist[i] = 0
    visualizations.show_hist(hist, bins=100, block=block)


class is_hist:
    def __init__(self, hist):
        self._suspicious_hist = hist

    def anomalous_compared_to(self, normal_hists):
        dist = min(enumerate([self._distance(normal_hist, self._suspicious_hist)
                              for normal_hist in normal_hists]), key=lambda index_and_dist: index_and_dist[1])
        # is_anomaly = dist[1] > 0.75
        is_anomaly = dist[1] > 0.025
        if is_anomaly:
            print 'distance from hist #' + str(dist[0]) + ': ' + str(dist[1])
            show_hist(normal_hists[dist[0]], block=False)
            show_hist(self._suspicious_hist)
        return is_anomaly

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
                # if we're dealing with b ig numbers it won't affect the result
                relative_deviation = 1. * abs(stash_after) / (to_hist[score] + 50)
                if relative_deviation > 0.2 or True:
                    distance += score_to_weight_squared_min_50(score) * relative_deviation
            stash = max(0, stash_after)
        return distance * 0.01

    @staticmethod
    def _calc_noise(hist):
        return sum([score_to_weight_squared_min_50(score) * hist[score]
                    for score in hist.iterkeys()])


def find_scores_anomalies(table_scores, warming_period):
    for field_scores in table_scores:
        print
        print '---------------------------'
        print field_scores.field_name
        print '---------------------------'
        normal_hists = []
        for i, (day, scores_hist) in enumerate(field_scores):
            is_anomaly = False
            if i >= warming_period:
                is_anomaly = is_hist(scores_hist).anomalous_compared_to(normal_hists)
            if is_anomaly:
                print 'anomaly detected:', day
            else:
                normal_hists.append(scores_hist)
