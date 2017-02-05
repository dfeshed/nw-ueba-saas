import json
import os
import re
import zipfile

import alphas_and_betas
import reducers
from store import Store
from .. import config
from ..utils.io import print_verbose, backup, open_overrides_file, FileWriter, iter_overrides_files


class _UpdatesManager:
    def __init__(self):
        self._backuped = set()

    def update(self, conf_file_path, updater, *args):
        with open_overrides_file(conf_file_path) as f:
            conf_lines = f.read().splitlines()
            if type(conf_file_path) == dict:
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

def init_reducers(logger):
    for f in iter_overrides_files(overriding_path=config.aggregated_feature_event_prevalance_stats_path['overriding_path'],
                                      jar_name=config.aggregated_feature_event_prevalance_stats_path['jar_name'],
                                      path_in_jar=config.aggregated_feature_event_prevalance_stats_path['path_in_jar']):
            _init_reducers_conf_file(f, logger)


def _init_reducers_conf_file(f, logger):
    original_to_backup = {}
    scorer_json = json.load(f)
    for scorers_conf in scorer_json['data-source-scorers']:
        if len(scorers_conf['scorers']) != 1:
            raise Exception('multiple scorers are not supported')
        scorer_conf = scorers_conf['scorers'][0]
        reduction_configs = scorer_conf['reduction-configs']
        reduction_config = reduction_configs[0]
        reduction_config = {
            'reducingFactor': 0.1,
            'maxValueForFullyReduce': reduction_config['maxValueForFullyReduce'],
            'minValueForNotReduce': reduction_config['minValueForNotReduce'],
            'reducingFeatureName': reduction_config['reducingFeatureName']
        }
        reduction_configs[0] = reduction_config
    path = f.name
    logger.info('initiating scorer reducing_factor=0.1 of ' + path + '...')
    original_to_backup[path] = backup(path=path) if os.path.isfile(path) else None
    with FileWriter(path) as scorer_confFile:
        json.dump(scorer_json, scorer_confFile, indent=4)


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
            file_names = []
            if os.path.isdir(config.aggregated_feature_event_prevalance_stats_path['overriding_path']):
                tmp_file_names = os.listdir(config.aggregated_feature_event_prevalance_stats_path['overriding_path'])
                for file_name in tmp_file_names:
                    file_names.append(config.aggregated_feature_event_prevalance_stats_path['path_in_jar'] + '/' + file_name)

            if len(file_names) == 0:
                zf = zipfile.ZipFile('/home/cloudera/fortscale/streaming/lib/' +
                                     config.aggregated_feature_event_prevalance_stats_path['jar_name'], 'r')
                file_names = zf.namelist()
            for file_name in file_names:
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
            if os.path.exists(config.aggregated_feature_event_prevalance_stats_additional_path):
                for filename in filter(lambda name: 'backup' not in name and name.endswith('.json'),
                                       os.listdir(config.aggregated_feature_event_prevalance_stats_additional_path)):
                    updates_manager.update(config.aggregated_feature_event_prevalance_stats_additional_path + '/' + filename,
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
