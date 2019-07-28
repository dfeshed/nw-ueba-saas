from datetime import timedelta

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.input.input_pre_processor_operator import InputPreProcessor
from presidio.utils.airflow.context_wrapper import ContextWrapper
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
import json

from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_HOURLY, \
    FIX_DURATION_STRATEGY_DAILY
from presidio.utils.services.time_service import convert_to_utc, floor_time


class InputPreProcessorDagBuilder(PresidioDagBuilder):

    def __init__(self):
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self.input_pre_processing = self.get_input_pre_processing(conf_reader)

    def build(self, dag):
        hourly_short_circuit_operator = self._build_input_pre_processing_short_circuit(dag)
        for pre_processing in self.input_pre_processing:
            name = pre_processing.get("name")
            static_arguments_json = pre_processing.get("static_arguments")
            all_json_arguments = self.add_dynamic_arguments(pre_processing.get("dynamic_arguments"),
                                                            static_arguments_json)
            self._build_input_pre_processing_operator(dag, name, json.dumps(all_json_arguments),
                                                      hourly_short_circuit_operator)
        return dag

    @staticmethod
    def get_input_pre_processing(conf_reader):
        return conf_reader.read(conf_key='input_pre_processing')

    def _build_input_pre_processing_operator(self, dag, name, arguments, hourly_short_circuit_operator):
        hourly_short_circuit_operator >> InputPreProcessor(name=name, arguments=arguments,
                                                           command=PresidioDagBuilder.presidio_command,
                                                           run_clean_command_before_retry=False,).build(dag)

    def _build_input_pre_processing_short_circuit(self, dag):
        return self._create_infinite_retry_short_circuit_operator(
            task_id='input_pre_processing_hourly_short_circuit',
            dag=dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     get_schedule_interval(dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 dag,
                                                 self._min_gap_from_dag_start_date_to_start_scoring,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(dag))
        )

    def add_dynamic_arguments(self, context, dynamic_arguments, static_arguments_json):
        context_wrapper = ContextWrapper(context)
        execution_date = context_wrapper.get_execution_date()

        if 'startInstant' in dynamic_arguments:
            start_date = floor_time(execution_date, time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_start_date = convert_to_utc(start_date)
            static_arguments_json[0]['startInstant'] = utc_start_date
        if 'endInstant' in dynamic_arguments:
            end_date = floor_time(execution_date + timedelta(days=1),
                                  time_delta=FIX_DURATION_STRATEGY_DAILY)
            utc_end_date = convert_to_utc(end_date)
            static_arguments_json[0]['endInstant'] = utc_end_date

        return static_arguments_json



