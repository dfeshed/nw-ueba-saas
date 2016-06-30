import sys
import os
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..']))

from common import utils
from common import visualizations
from common.utils.io import print_verbose
from common import config


def find_median_value(f):
    values = []
    for user_fs in f.iter_fs_by_users():
        for a in user_fs:
            values.append(a['value'])

    return sorted(values)[len(values) / 2]

def iterate_interesting_scores(f, score_to_weight):
    for user_fs in f.iter_fs_by_users():
        user_history = []
        for a in sorted(user_fs, key = lambda a: a['start_time_unix']):
            weight = score_to_weight(utils.score.get_indicator_score(a, f._collection.name))
            if len(user_history) > 0 and weight > 0:
                yield {'history': user_history, 'a': a, 'weight': weight}
            user_history.append(a['value'])

def find_positive_values_hists(f, max_bad_value_diff, score_to_weight):
    false_positives_values_hist = {}
    true_positives_values_hist = {}
    for e in iterate_interesting_scores(f, score_to_weight):
        if abs(1. * sum(e['history']) / len(e['history']) - e['a']['value']) <= max_bad_value_diff:
            hist = false_positives_values_hist
        else:
            hist = true_positives_values_hist
        hist[e['a']['value']] = hist.get(e['a']['value'], 0) + e['weight']
    return {
        False: false_positives_values_hist,
        True: true_positives_values_hist
    }

def calc_f_reducer(f, score_to_weight, max_bad_value_diff = 2):
    median_value = find_median_value(f)
    if median_value < max_bad_value_diff + 2:
        max_bad_value_diff = 1
    print_verbose('median value:', median_value, ', max_bad_value_diff:', max_bad_value_diff)

    hists = find_positive_values_hists(f, max_bad_value_diff = max_bad_value_diff, score_to_weight = score_to_weight)
    print_verbose('true positives:')
    visualizations.show_hist(hists[True])
    print_verbose('false positives:')
    visualizations.show_hist(hists[False])
    return find_best_reducer(f, hists)

def iter_reducers_space(f):
    yield None
    min_positive_score = config.F_REDUCER_TO_MIN_POSITIVE_SCORE.get(f._collection.name[len('scored___aggr_event__'):])
    for max_value_for_fully_reduce in xrange(0 if min_positive_score is None else min_positive_score - 1, 30):
        for min_value_for_not_reduce in xrange(max_value_for_fully_reduce + 1, 30):
            for reducing_factor in [0.1 * i for i in xrange(1, 10)] if min_positive_score is None else [0]:
                yield {
                    'min_value_for_not_reduce': min_value_for_not_reduce,
                    'max_value_for_fully_reduce': max_value_for_fully_reduce,
                    'reducing_factor': reducing_factor
                }

def calc_reducer_weight(reducer):
    if reducer is None:
        reducer = {
            'max_value_for_fully_reduce': 1,
            'min_value_for_not_reduce': 2,
            'reducing_factor': 1
        }

    HEIGHT_RELATIVE_IMPORTANCE = 0.8
    slope_penalty = (1. - reducer['reducing_factor']) / (reducer['min_value_for_not_reduce'] - reducer['max_value_for_fully_reduce'])
    height_penalty = 0.1 / reducer['reducing_factor']
    return (1 - HEIGHT_RELATIVE_IMPORTANCE) * (1 - slope_penalty) + HEIGHT_RELATIVE_IMPORTANCE * (1 - height_penalty)

def find_best_reducer(f, hists):
    best_reducer = None
    max_reducer_score = -1
    for reducer in iter_reducers_space(f):
        reducer_gain = calc_reducer_gain(f, hists, reducer)
        reducer_penalty = -(1 - calc_reducer_weight(reducer))
        PENALTY_IMPORTANCE = 0.05
        reducer_score = (1 - PENALTY_IMPORTANCE) * reducer_gain + PENALTY_IMPORTANCE * reducer_penalty
        if reducer_score > max_reducer_score:
            max_reducer_score = reducer_score
            best_reducer = reducer
            print_verbose('improved best reducer score. weighted gain =',
                          (1 - PENALTY_IMPORTANCE) * reducer_gain,
                          'weighted penalty =',
                          PENALTY_IMPORTANCE * reducer_penalty, reducer)
    return best_reducer

def calc_reducer_gain(f, hists, reducer):
    reduced_count_sum = {True: 0, False: 0}
    for tf_type in [True, False]:
        reduced_count_sum[tf_type] = 0
        for value, count in hists[tf_type].iteritems():
            score_dummy = 50.
            reducing_factor = utils.score.get_indicator_score({
                'value': value,
                'score': score_dummy
            }, name = f._collection.name, reducer = reducer) / score_dummy
            reduced_count_sum[tf_type] += count * reducing_factor
    # use Bayesian approach of bernoulli distribution (with Beta conjugate prior).
    # the reason we add a prior is to handle the case where we have few samples - in this case
    # we're more prone to get a drastic reducer (maximal slope) as the final result, because
    # it might be really worthwhile to cut the low values completely as a result of fluctuations
    # in the data. adding a prior eliminates the fluctuations.
    prior_a = prior_b = 30
    a = reduced_count_sum[True] + prior_a
    b = reduced_count_sum[False] + prior_b
    # calculate Beta's mode:
    probability_of_seeing_good_f = 1. * (a - 1) / (a + b - 2)
    return probability_of_seeing_good_f

def calc_fs_reducers(score_to_weight, fs):
    print
    print '----------------------------------------------------------------------'
    print '--------------------------- Fs reducers  -----------------------------'
    print '----------------------------------------------------------------------'
    res = {}
    for f in fs:
        print_verbose(f._collection.name + ':')
        if len(list(f.iter_fs_by_users())) == 0:
            print_verbose('empty collection!')
            continue
        reducer = calc_f_reducer(f, score_to_weight = score_to_weight)
        if reducer is not None:
            res[f._collection.name[len('scored___aggr_event__'):]] = reducer
            print_verbose('found reducer:', reducer)
        print_verbose()
    print_verbose()
    utils.io.print_json(res)
    return res