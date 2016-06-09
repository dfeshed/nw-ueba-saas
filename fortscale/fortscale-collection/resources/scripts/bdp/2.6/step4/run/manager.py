import logging
from mongo_stats import update_models_time, remove_documents

import os
import sys
sys.path.append(os.path.sep.join([os.path.dirname(os.path.abspath(__file__)), '..', '..', '..']))
from step4.validation.distribution.validation import validate_distribution
import bdp_utils.run


logger = logging.getLogger('2.6-step4')


class Manager:
    def __init__(self, host, days_to_ignore):
        self._runner = bdp_utils.run.Runner(name='2.6-BdpEntityEventsCreation.run',
                                            logger=logger,
                                            host=host,
                                            block=True)
        self._builder = bdp_utils.run.Runner(name='2.6-BdpEntityEventsCreation.build_models',
                                             logger=logger,
                                             host=host,
                                             block=True)
        self._host = host
        self._days_to_ignore = days_to_ignore

    def run(self):
        entity_event_value_models_regex = r'model_entity_event\.(.*\.)?normalized_username\.'
        alert_control_models_regex = r'model_entity_event\.(.*\.)?global.alert_control\.'
        scored_entity_events_regex = 'scored___entity_event_'
        self._runner.infer_start_and_end(collection_names_regex='^entity_event_(?!meta_data)')
        self._builder.set_start(self._runner.get_end()).set_end(self._runner.get_end())
        for step in [lambda: self._run_bdp(days_to_ignore=self._days_to_ignore),
                     self._build_models,
                     lambda: self._move_models_back_in_time(collection_names_regex=entity_event_value_models_regex),
                     lambda: self._clean_collections(collection_names_regex=alert_control_models_regex,
                                                     msg='removing unneeded models...'),
                     lambda: self._clean_collections(collection_names_regex=scored_entity_events_regex,
                                                     msg='removing scored entity events...'),
                     lambda: self._run_bdp(days_to_ignore=self._days_to_ignore),
                     self._build_models,
                     lambda: self._move_models_back_in_time(collection_names_regex=alert_control_models_regex),
                     lambda: self._clean_collections(collection_names_regex=scored_entity_events_regex,
                                                     msg='removing scored entity events...'),
                     lambda: self._run_bdp(days_to_ignore=0),
                     self._validate]:
            if not step():
                return False
        return True

    def _run_bdp(self, days_to_ignore):
        logger.info('running BDP...')
        start = self._runner.get_start()
        self._runner.set_start(start + days_to_ignore * 60 * 60 * 24)
        self._runner.run(overrides_key='2.6-step4.run')
        self._runner.set_start(start)
        logger.info('DONE')
        return True

    def _build_models(self):
        logger.info('building models...')
        self._builder.run(overrides_key='2.6-step4.build_models')
        logger.info('DONE')
        return True

    def _move_models_back_in_time(self, collection_names_regex):
        logger.info('moving models back in time by ' + str(self._runner.get_start()) + ' seconds...')
        is_success = update_models_time(host=self._host,
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
