import datetime
import os
from common.utils import print_verbose

from common import config
from common import results
from common.results import alphas_and_betas, fs_reduction


def _call_updater(conf_file_path, updater, *args):
    with open(conf_file_path, 'r') as f:
        conf_lines = f.readlines()
    transformed = updater(conf_lines, *args)

    now = str(datetime.datetime.now()).replace(' ', '_').replace(':', '-')
    now = now[:now.index('.')]
    os.rename(conf_file_path, conf_file_path + '.backup-' + now)
    with open(conf_file_path, 'w') as f:
        f.writelines(transformed)

def update_configurations():
    store = results.store.Store(config.interim_results_path + '/results.json')
    updated_something = False
    w = store.get('w')
    if w is not None:
        updated_something = True
        print_verbose('Updating alphas and betas...')
        _call_updater(config.entity_events_path, alphas_and_betas.update, w)
    fs_reducers = store.get('fs_reducers')
    if fs_reducers is not None:
        updated_something = True
        print_verbose('Updating Fs low-values-score-reducer configurations...')
        _call_updater(config.aggregated_feature_event_prevalance_stats_path, fs_reduction.update, fs_reducers)
    if not updated_something:
        print_verbose('Nothing to update')
