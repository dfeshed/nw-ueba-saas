import datetime
import sys
import time

sys.path.append('..')
from data.entities import Entities, FsAndPs
import hist_utils
from algorithm import weights, reducer
from common import config
from common.utils.io import print_verbose
from common.utils.mongo import get_collection_names
from common.results.store import Store

def _load_data(mongo_ip, should_query, entity_type, start, end):
    entities = Entities(dir_path = config.interim_results_path + '/entities/' + entity_type,
                        entity_type = entity_type,
                        mongo_ip = mongo_ip)
    if should_query:
        print_verbose('Querying entities...')
        entities.query(start_time=start or config.get_start_time(),
                       end_time=end or config.get_end_time(),
                       should_save_every_day=True)
    print_verbose('Entities in entities.txt:')
    print_verbose(entities)
    print_verbose()
    print_verbose('Calculating Fs and Ps distribution...')
    fs_and_ps = FsAndPs(entities)
    return entities, fs_and_ps

def is_len_at_least(generator, n):
    while n > 0:
        try:
            generator.next()
            n -= 1
        except StopIteration:
            return False
    return True

def _run_algo(entities, fs_and_ps, store):
    if not is_len_at_least(entities.iterate(is_daily = True), 30):
        print 'too few daily entity events with positive value. skipping...'
        return
    if not is_len_at_least(entities.iterate(is_daily = False), 90):
        print 'too few hourly entity events with positive value. skipping...'
        return
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
                                                                                                                         is_daily = False))

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

    w = store.get('w', {})
    w[entities.entity_type + '_daily'] = w_daily
    w[entities.entity_type + '_hourly'] = w_hourly
    store.set('w', w)
    store.set(entities.entity_type + '_daily_reducer', daily_reducer)
    store.set(entities.entity_type + '_hourly_reducer', hourly_reducer)

def _get_entity_types():
    return set(map(lambda collection_name: collection_name[:collection_name.find('_daily') +
                                                            collection_name.find('_hourly') + 1],
                   get_collection_names(host=config.mongo_ip, collection_names_regex='^entity_event_(?!meta_data_).')))

def _main(should_query, should_run_algo, start, end):
    script_start_time = time.time()
    store = Store(config.interim_results_path + '/results.json')
    for entity_type in _get_entity_types():
        entities, fs_and_ps = _load_data(mongo_ip = config.mongo_ip,
                                         should_query = should_query,
                                         entity_type = entity_type,
                                         start = start,
                                         end = end)
        if should_run_algo:
            _run_algo(entities = entities, fs_and_ps = fs_and_ps, store = store)
    print_verbose("The script's run time was", datetime.timedelta(seconds = int(time.time() - script_start_time)))

def load_data(start = None, end = None):
    _main(should_query = True, should_run_algo = False, start = start, end = end)

def run_algo(start = None, end = None):
    _main(should_query = False, should_run_algo = True, start = start, end = end)

def load_data_and_run_algo(start = None, end = None):
    _main(should_query = True, should_run_algo = True, start = start, end = end)
