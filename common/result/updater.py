import datetime
import os

from common import config
from common import result


def _call_updater(conf_file_path, updater, *args):
    with open(conf_file_path, 'r') as f:
        conf_lines = f.readlines()
    transformed = updater(conf_lines, *args)

    now = str(datetime.datetime.now()).replace(' ', '_').replace(':', '-')
    now = now[:now.index('.')]
    os.rename(conf_file_path, conf_file_path + '.backup-' + now)
    with open(conf_file_path, 'w') as f:
        f.writelines(transformed)

def update_configurations(store):
    w = store.get('w')
    if w is not None:
        _call_updater(config.entity_events_path, result.alphas_and_betas.update, w)
    fs_reducers = store.get('fs_reducers')
    if fs_reducers is not None:
        _call_updater(config.aggregated_feature_event_prevalance_stats_path, result.fs_reduction.update, fs_reducers)
