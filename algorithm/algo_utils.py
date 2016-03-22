import re

import config


def get_indicator_score(a, name = None):
    name = name or a['name']
    score = a['score']
    reducer = config.REDUCERS.get(name, None)
    score = reduce_low_values(score,
                              a['value'],
                              reducer = reducer,
                              old_reducer = old_reducers.get(name, None))
    return score

def reduce_low_values(score, value, reducer, old_reducer = None):
    if old_reducer is not None:
        score /= calc_reducing_factor(value, old_reducer['min_value_for_not_reduce'], old_reducer['max_value_for_fully_reduce'], old_reducer['reducing_factor'])
    if reducer is not None:
        score *= calc_reducing_factor(value, reducer['min_value_for_not_reduce'], reducer['max_value_for_fully_reduce'], reducer['reducing_factor'])
    return score

def calc_reducing_factor(value, min_value_for_not_reduce, max_value_for_fully_reduce, reducing_factor):
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

def load_old_low_values_reducers():
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
old_reducers = load_old_low_values_reducers()

def calc_entity_event_value(e, w):
    return sum([w[a['type']][a['name']] * get_indicator_score(a) * (.01 if a['type'] == 'F' else 1) for a in e['includedAggrFeatureEvents']])

def calc_top_entities_given_w(entities, is_daily, w, num_of_entities_per_day):
    return [sorted(entities, key = lambda e: calc_entity_event_value(e, w), reverse = True)[:num_of_entities_per_day]
            for entities in entities.group_by_day(is_daily)]