import logging
from mongo_stats import remove_documents

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from step4.validation.distribution.validation import validate_distribution
from step4.validation.missing_events.validation import validate_no_missing_events
import bdp_utils.run
from bdp_utils.manager import DontReloadModelsOverridingManager
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', '..']))
from automatic_config.common.utils.mongo import update_models_time


logger = logging.getLogger('2.6-step4')


class Manager(DontReloadModelsOverridingManager):
    def __init__(self, host, validation_timeout, validation_polling, days_to_ignore):
        super(Manager, self).__init__(logger=logger)
        self._runner = bdp_utils.run.Runner(name='2.6-BdpEntityEventsCreation.scores',
                                            logger=logger,
                                            host=host,
                                            block=False)
        self._builder = bdp_utils.run.Runner(name='2.6-BdpEntityEventsCreation.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True,
                                             block_until_log_reached='Spring context closed')
        self._host = host
        self._validation_timeout = validation_timeout
        self._validation_polling = validation_polling
        self._days_to_ignore = days_to_ignore

    def _run(self):
        entity_event_value_models_regex = r'model_entity_event\.(.*\.)?normalized_username\.'
        alert_control_models_regex = r'model_entity_event\.(.*\.)?global.alert_control\.'
        models_regex = '(' + entity_event_value_models_regex + '|' + alert_control_models_regex + ')'
        scored_entity_events_regex = 'scored___entity_event_'
        self._runner.infer_start_and_end(collection_names_regex='^entity_event_(?!meta_data)')
        end_rounded = ((self._runner.get_end() / (60 * 60 * 24)) + 1) * (60 * 60 * 24)
        self._builder.set_start(end_rounded).set_end(end_rounded)
        for sub_step_name, sub_step in [('run scores', lambda: self._run_bdp(days_to_ignore=self._days_to_ignore)),
                                        ('build models', self._build_models),
                                        ('move models back in time', lambda: self._move_models_back_in_time(collection_names_regex=models_regex)),
                                        ('clean scored collections', lambda: self._clean_collections(collection_names_regex=scored_entity_events_regex,
                                                                                                     msg='removing scored entity events...')),
                                        ('run scores after entity event models and global entity event models have been built', lambda: self._run_bdp(days_to_ignore=self._days_to_ignore)),
                                        ('clean unneeded models', lambda: self._clean_collections(collection_names_regex=models_regex,
                                                                                                  msg='removing unneeded models...')),
                                        ('build models second time (so we have good alert control models)', self._build_models),
                                        ('move models back in time second time', lambda: self._move_models_back_in_time(collection_names_regex=models_regex)),
                                        ('clean scored collections second time', lambda: self._clean_collections(collection_names_regex=scored_entity_events_regex,
                                                                                                                 msg='removing scored entity events...')),
                                        ('run scores after all needed models have been built (including alert control)', lambda: self._run_bdp(days_to_ignore=0)),
                                        ('validate', self._validate)]:
            logger.info('running sub step ' + sub_step_name + '...')
            if not sub_step():
                return False
        return True

    def _run_bdp(self, days_to_ignore):
        logger.info('running BDP' +
                    ((' excluding ' + str(days_to_ignore) + ' days') if days_to_ignore > 0 else '') + '...')
        start_backup = self._runner.get_start()
        start = start_backup + days_to_ignore * 60 * 60 * 24
        self._runner.set_start(start)
        kill_process = self._runner.run(overrides_key='2.6-step4.scores')
        self._runner.set_start(start_backup)
        is_valid = validate_no_missing_events(host=self._host,
                                              timeout=self._validation_timeout,
                                              polling=self._validation_polling,
                                              start=start,
                                              end=self._runner.get_end())
        kill_process()
        logger.info('DONE')
        return is_valid

    def _build_models(self):
        self._builder.run(overrides_key='2.6-step4.build_models')
        logger.info('DONE')
        return True

    def _move_models_back_in_time(self, collection_names_regex):
        is_success = update_models_time(logger=logger,
                                        host=self._host,
                                        collection_names_regex=collection_names_regex,
                                        time=self._runner.get_start())
        logger.info('DONE')
        return is_success

    def _clean_collections(self, collection_names_regex, msg):
        logger.info(msg)
        is_success = remove_documents(host=self._host,
                                      collection_names_regex=collection_names_regex)
        logger.info('DONE')
        return is_success

    def _validate(self):
        validate_distribution(host=self._host)
        return True
