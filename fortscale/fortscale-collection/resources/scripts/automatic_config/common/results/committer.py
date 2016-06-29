import os
import re
import zipfile
from .. import config
from store import Store
import alphas_and_betas
import reducers
from ..utils.io import print_verbose, backup, open_overrides_file, FileWriter


class _UpdatesManager:
    def __init__(self):
        self._backuped = set()

    def update(self, conf_file_path, updater, *args):
        with open_overrides_file(conf_file_path) as f:
            conf_lines = f.read().splitlines()
            conf_file_path = conf_file_path['overriding_path']
        transformed = updater(conf_lines, *args)

        if conf_file_path not in self._backuped:
            self._backuped.add(conf_file_path)
            if os.path.exists(conf_file_path):
                backup(path=conf_file_path)

        with FileWriter(conf_file_path) as f:
            f.write(transformed)

    def updated_something(self):
        return len(self._backuped) > 0


def update_configurations():
    store = Store(config.interim_results_path + '/results.json')
    updates_manager = _UpdatesManager()
    w = store.get('w')
    if w is not None:
        updates_manager.update(config.entity_events_path, alphas_and_betas.update, w)
        print_verbose('updated alphas and betas')

    reducers_to_update = {}
    daily_reducer = None
    hourly_reducer = None
    if type(config.aggregated_feature_event_prevalance_stats_path) != dict:
        daily_reducer = store.get('daily_reducer')
        if daily_reducer is not None:
            reducers_to_update.update({'normalized_username_daily': daily_reducer})
        hourly_reducer = store.get('hourly_reducer')
        if hourly_reducer is not None:
            reducers_to_update.update({'normalized_username_hourly': hourly_reducer})
    fs_reducers = store.get('fs_reducers')
    if fs_reducers is not None:
        reducers_to_update.update(fs_reducers)

    if len(reducers_to_update) > 0 or fs_reducers is not None:
        if type(config.aggregated_feature_event_prevalance_stats_path) == dict:
            zf = zipfile.ZipFile('/home/cloudera/fortscale/streaming/lib/' +
                                 config.aggregated_feature_event_prevalance_stats_path['jar_name'], 'r')
            for file_name in zf.namelist():
                match = re.match(config.aggregated_feature_event_prevalance_stats_path['path_in_jar'] + '/(.+)', file_name)
                if match is not None:
                    conf_file_path = {
                        'overriding_path': config.aggregated_feature_event_prevalance_stats_path['overriding_path'] +
                                           '/' + match.group(1),
                        'jar_name': config.aggregated_feature_event_prevalance_stats_path['jar_name'],
                        'path_in_jar': match.group(0)
                    }
                    updates_manager.update(conf_file_path,
                                           reducers.update26,
                                           reducers_to_update)
        else:
            updates_manager.update(config.aggregated_feature_event_prevalance_stats_path,
                                   reducers.update,
                                   reducers_to_update)
        if len(reducers_to_update) > 0:
            raise Exception("Some reducers weren't found: " + str(reducers_to_update))
        print_verbose('updated ' + ', '.join([s for s, cond in {
            'daily entities low-values-score-reducer configuration': daily_reducer is not None,
            'hourly entities low-values-score-reducer configuration': hourly_reducer is not None,
            'Fs low-values-score-reducer configurations': fs_reducers is not None
        }.iteritems() if cond]))

    if not updates_manager.updated_something():
        print_verbose('Nothing to update')
