import datetime
import os
from common import config
from common import results
from common.results import alphas_and_betas, reducers
from common.utils.io import print_verbose


class _UpdatesManager:
    def __init__(self):
        self._backuped = set()

    def update(self, conf_file_path, updater, *args):
        if not os.path.exists(conf_file_path):
            raise Exception('file must exist: ' + conf_file_path)
        with open(conf_file_path, 'r') as f:
            conf_lines = f.read().splitlines()
        transformed = updater(conf_lines, *args)

        if not conf_file_path in self._backuped:
            self._backuped.add(conf_file_path)
            now = str(datetime.datetime.now()).replace(' ', '_').replace(':', '-')
            now = now[:now.index('.')]
            os.rename(conf_file_path, conf_file_path + '.backup-' + now)

        with open(conf_file_path, 'w') as f:
            f.write(transformed)

    def updated_something(self):
        return len(self._backuped) > 0


def update_configurations():
    store = results.store.Store(config.interim_results_path + '/results.json')
    updates_manager = _UpdatesManager()
    w = store.get('w')
    if w is not None:
        updates_manager.update(config.entity_events_path, alphas_and_betas.update, w)
        print_verbose('updated alphas and betas')

    daily_reducer = store.get('daily_reducer')
    if daily_reducer is not None:
        updates_manager.update(config.aggregated_feature_event_prevalance_stats_path, reducers.update, {'normalized_username_daily': daily_reducer})
        print_verbose('updated daily entities low-values-score-reducer configuration')

    hourly_reducer = store.get('hourly_reducer')
    if hourly_reducer is not None:
        updates_manager.update(config.aggregated_feature_event_prevalance_stats_path, reducers.update, {'normalized_username_hourly': hourly_reducer})
        print_verbose('updated hourly entities low-values-score-reducer configuration')

    fs_reducers = store.get('fs_reducers')
    if fs_reducers is not None:
        updates_manager.update(config.aggregated_feature_event_prevalance_stats_path, reducers.update, fs_reducers)
        print_verbose('updated Fs low-values-score-reducer configurations')

    if not updates_manager.updated_something():
        print_verbose('Nothing to update')
