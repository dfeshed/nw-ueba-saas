import config
import utils
import visualizations
from algorithm import algo_utils
from data.fs import Fs
from utils import print_verbose


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
            weight = score_to_weight(algo_utils.get_indicator_score(a, f.collection_name))
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

def calc_eliminated(hists):
    value_thresholds = [0] + [v + 1 for v in list(hists[False].iterkeys()) + list(hists[True].iterkeys())]
    eliminated = {False: {}, True: {}}
    for value_threshold in value_thresholds:
        for tf_type in [True, False]:
            eliminated[tf_type][value_threshold] = sum([count
                                                         for value, count in hists[tf_type].iteritems()
                                                         if value < value_threshold])
    return eliminated

def calc_min_value_for_not_reduce(f, score_to_weight, max_bad_value_diff = 2):
    median_value = find_median_value(f)
    if median_value < max_bad_value_diff + 2:
        max_bad_value_diff = 1
    print_verbose('median value:', median_value, ', max_bad_value_diff:', max_bad_value_diff)

    hists = find_positive_values_hists(f, max_bad_value_diff = max_bad_value_diff, score_to_weight = score_to_weight)
    print_verbose(f.collection_name + ':')
    print_verbose('true positives:')
    visualizations.show_hist(hists[True])
    print_verbose('false positives:')
    visualizations.show_hist(hists[False])
    eliminated = calc_eliminated(hists)
    visualizations.plot_reduction_threshold_effect(eliminated)

    gains = {}
    for value_threshold in sorted(eliminated[False].iterkeys()):
        gains[value_threshold] = eliminated[False][value_threshold] - eliminated[True][value_threshold]

    max_gain = max(gains.iteritems(), key = lambda g: g[1])
    print_verbose('gains:', gains)
    if max_gain[1] > 0:
        return max_gain[0]

def calc_min_value_for_not_reduce_for_hists(score_to_weight, should_query = True, fs = None):
    print
    print '----------------------------------------------------------------------'
    print '--------------------- min_value_for_not_reduce  ----------------------'
    print '----------------------------------------------------------------------'
    fs = fs or Fs('fs.txt')
    if should_query:
        fs.query(config.mongo_ip, save_intermediate = True)
    res = {}
    for f in fs:
        print_verbose()
        min_value_for_not_reduce = calc_min_value_for_not_reduce(f, score_to_weight = score_to_weight)
        if min_value_for_not_reduce is not None:
            res[f.collection_name] = min_value_for_not_reduce
            print_verbose('found min_value_for_not_reduce:', min_value_for_not_reduce)
    print_verbose()
    utils.print_json(res)
    return fs

def create_score_to_weight_squared(min_score):
    def score_to_weight_squared(score):
        return max(0, 1 - ((score - 100) / (100.0 - min_score)) ** 2)
    return score_to_weight_squared

score_to_weight_squared_min_50 = create_score_to_weight_squared(50)
score_to_weight_linear = lambda score: score * 0.01