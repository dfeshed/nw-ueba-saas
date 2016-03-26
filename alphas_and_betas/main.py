import datetime
import sys
import time

sys.path.append('..')
from data.entities import Entities, FsAndPs
import hist_utils
from common.utils import print_verbose
from common import config
from algorithm import weights, reducer
from common.result.store import Store

def load_data(mongo_ip = None, path = None):
    START_TIME = config.START_TIME
    END_TIME = config.END_TIME
    entities = Entities(path = path or 'entities.txt', mongo_ip = mongo_ip or config.mongo_ip)
    print_verbose('Querying entities...')
    entities.query(start_time = START_TIME, end_time = END_TIME, should_save_every_day = True)
    if hasattr(config, 'IS_CISCO'):
        entities.set_entities_filter(lambda entity: START_TIME <= entity['startTime'] < 1456099200 or entity['startTime'] >= 1456272000)
    print_verbose('Entities in entities.txt:')
    print_verbose(entities)
    print_verbose()
    print_verbose('Calculating Fs and Ps distribution...')
    fs_and_ps = FsAndPs(entities)
    return entities, fs_and_ps

def run_algo(entities, fs_and_ps, store):
    print '------------------------------------------'
    print '--- Calculating daily alphas and betas ---'
    print '------------------------------------------'
    fs_and_ps.show(is_daily = True)
    w_daily = weights.give_penalty_and_then_iterate(entities,
                                                    fs_and_ps,
                                                    is_daily = True,
                                                    penalty_calculator = weights.allow_one_high_score_per_user_per_year(weights.penalty_calculator(is_daily = True,
                                                                                                                                                   hist_transformer = hist_utils.normalize_hist_by_unreliability),
                                                                                                                        is_daily = True))

    print
    print '-------------------------------------------'
    print '--- Calculating hourly alphas and betas ---'
    print '-------------------------------------------'
    fs_and_ps.show(is_daily = False)
    w_hourly = weights.give_penalty_and_then_iterate(entities,
                                                     fs_and_ps,
                                                     is_daily = False,
                                                     penalty_calculator = weights.allow_one_high_score_per_user_per_year(weights.penalty_calculator(is_daily = False,
                                                                                                                                                    hist_transformer = hist_utils.normalize_hist_by_unreliability),
                                                                                                                         is_daily = False),
                                                     max_allowed_contribution = 12.65)

    print
    print '----------------------------------------------------------------------'
    print '--- Calculating normalized_username_daily_scorer.reduction.configs ---'
    print '----------------------------------------------------------------------'
    daily_reducer = reducer.calc_low_values_reducer_params(entities, is_daily = True, w = w_daily)

    print
    print '-----------------------------------------------------------------------'
    print '--- Calculating normalized_username_hourly_scorer.reduction.configs ---'
    print '-----------------------------------------------------------------------'
    hourly_reducer = reducer.calc_low_values_reducer_params(entities, is_daily = False, w = w_hourly)

    store.set({
        'w': {
            'normalized_username_daily': w_daily,
            'normalized_username_hourly': w_hourly
        }
    })
    store.set('daily_reducer', daily_reducer)
    store.set('hourly_reducer', hourly_reducer)

def main(mongo_ip = None, path = None):
    start_time = time.time()
    store = Store('store.json')
    entities, fs_and_ps = load_data(mongo_ip = mongo_ip, path = path)
    run_algo(entities = entities, fs_and_ps = fs_and_ps, store = store)
    print_verbose("The script's run time was", datetime.timedelta(seconds = int(time.time() - start_time)))

if __name__ == '__main__':
    main()
