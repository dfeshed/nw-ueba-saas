from common import config
from common import utils
from common.utils.io import print_verbose

import algo_utils


def calc_low_values_reducer_params(entities, is_daily, w):
    top_entities = algo_utils.calc_top_entities_given_w(entities, is_daily, w, config.NUM_OF_ALERTS_PER_DAY)
    thresholds_per_day = [algo_utils.calc_entity_event_value(entities_group[-1], w)
                          for entities_group in top_entities]

    thresholds_per_day = sorted(thresholds_per_day)
    threshold_value = thresholds_per_day[len(thresholds_per_day) / 2]

    return calc_low_values_reducer_params_given_threshold(threshold_value)


def calc_low_values_reducer_params_given_threshold(threshold_value):
    reducing_factor = 0.1
    max_value_for_fully_reduce = threshold_value * 0.666
    min_value_for_not_reduce = max_value_for_fully_reduce + (1 - reducing_factor) * (threshold_value - max_value_for_fully_reduce) / (0.5 - reducing_factor)
    print_verbose("the threshold value is %f" % threshold_value)
    print 'min_value_for_not_reduce = %.4f, max_value_for_fully_reduce = %.4f, reducing_factor = %.2f' % (min_value_for_not_reduce, max_value_for_fully_reduce, reducing_factor)
    reducer = {
        'min_value_for_not_reduce': min_value_for_not_reduce,
        'max_value_for_fully_reduce': max_value_for_fully_reduce,
        'reducing_factor': reducing_factor
    }
    reduced_score = utils.score.reduce_low_values(score = 100, value = threshold_value, reducer = reducer)
    assert abs(50 - reduced_score < 0.0001), reduced_score
    return reducer
