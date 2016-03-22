import config
import visualizations
from algorithm import algo_utils
from data.fs import Fs
from utils import print_verbose


def iterate_interesting_scores(f, score_to_weight):
    for user_fs in f.iter_fs_by_users():
        user_history = []
        for a in sorted(user_fs, key = lambda a: a['start_time_unix']):
            weight = score_to_weight(algo_utils.get_indicator_score(a, f.collection_name))
            if len(user_history) > 0 and weight > 0:
                yield {'history': user_history, 'a': a, 'weight': weight}
            user_history.append(a['value'])

def find_median_value(f, score_to_weight):
    values = [e['a']['value'] for e in iterate_interesting_scores(f, score_to_weight)]
    return sorted(values)[len(values) / 2]

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

def calc_min_value_for_not_reduce(f, score_to_weight, max_bad_value_diff = 2):
    hists = find_positive_values_hists(f, max_bad_value_diff = max_bad_value_diff, score_to_weight = score_to_weight)
    print_verbose(f.collection_name + ':')
    print_verbose('true positives:')
    visualizations.show_hist(hists[True])
    print_verbose('false positives:')
    visualizations.show_hist(hists[False])
    visualizations.plot_threshold_effect(hists)
    median_value = find_median_value(f, score_to_weight)
    if median_value < max_bad_value_diff + 2:
        print_verbose('median value is too small (', median_value, '). no point in reducing low values')
        return

    hist = hists[False]
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

def calc_min_value_for_not_reduce_for_hists(score_to_weight, should_query = True, fs = None):
    print
    print '----------------------------------------------------------------------'
    print '--------------------- min_value_for_not_reduce  ----------------------'
    print '----------------------------------------------------------------------'
    fs = fs or Fs('fs.txt')
    if should_query:
        fs.query(config.mongo_ip, save_intermediate = True)
    for f in fs:
        print_verbose()
        min_value_for_not_reduce = calc_min_value_for_not_reduce(f, score_to_weight = score_to_weight)
        if min_value_for_not_reduce is not None:
            print f.collection_name + ':', min_value_for_not_reduce
    return fs
