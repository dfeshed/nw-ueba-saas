import re

from .. import config
from ..results.store import Store


def get_indicator_score(a, name = None, reducer = None):
    def inner(a, name = None, reducer = None):
        name = name or a['name']
        score = a['score']
        reducer = reducer or store.get('fs_reducers', {}).get(name, None)
        score = reduce_low_values(score,
                                  a['value'],
                                  reducer = reducer,
                                  old_reducer = old_reducers.get(name, None))
        return score

    old_reducers = _load_old_low_values_reducers()
    store = Store(config.interim_results_path + '/results.json')

    global get_indicator_score
    get_indicator_score = inner
    return inner(a, name, reducer)


def reduce_low_values(score, value, reducer, old_reducer = None):
    if old_reducer is not None:
        score /= _calc_reducing_factor(value, old_reducer['min_value_for_not_reduce'], old_reducer['max_value_for_fully_reduce'], old_reducer['reducing_factor'])
    if reducer is not None:
        score *= _calc_reducing_factor(value, reducer['min_value_for_not_reduce'], reducer['max_value_for_fully_reduce'], reducer['reducing_factor'])
    return score


def _calc_reducing_factor(value, min_value_for_not_reduce, max_value_for_fully_reduce, reducing_factor):
    if value <= max_value_for_fully_reduce:
        factor = reducing_factor
    elif value < min_value_for_not_reduce:
        numerator = value - max_value_for_fully_reduce
        denominator = min_value_for_not_reduce - max_value_for_fully_reduce
        part_to_add = 1. * numerator / denominator
        factor = reducing_factor + (1 - reducing_factor) * part_to_add
    else:
        factor = 1
    return factor


def _load_old_low_values_reducers():
    res = {}
    with open(config.aggregated_feature_event_prevalance_stats_path, 'r') as f:
        for l in f.readlines():
            match = re.search('fortscale\.aggr_event\..*\.(.*)\.fortscale\.score\..*\.reduction\.configs=.*"reducingFactor":(\d+\.?\d*).*"maxValueForFullyReduce":(\d+\.?\d*).*"minValueForNotReduce":(\d+\.?\d*)', l)
            if match is not None:
                f_name, reducing_factor, max_value_for_fully_reduce, min_value_for_not_reduce = match.groups()
                if res.has_key(f_name):
                    raise Exception(f_name + ' was already encountered')
                res[f_name] = {
                    'reducing_factor': float(reducing_factor),
                    'max_value_for_fully_reduce': float(max_value_for_fully_reduce),
                    'min_value_for_not_reduce': float(min_value_for_not_reduce)
                }
    return res


def create_score_to_weight_squared(min_score):
    def score_to_weight_squared(score):
        return max(0, 1 - ((100. - score) / (100 - min_score)) ** 2)
    return score_to_weight_squared

score_to_weight_squared_min_50 = create_score_to_weight_squared(50)


def score_to_weight_linear(score):
    return score * 0.01
