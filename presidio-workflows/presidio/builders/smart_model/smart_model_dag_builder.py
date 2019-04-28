from presidio.builders.model.aggr_model_dag_builder import AggrModelDagBuilder
from presidio.builders.model.raw_model_dag_builder import RawModelDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.smart_model_accumulate_operator import SmartModelAccumulateOperator
from presidio.operators.model.smart_model_operator import SmartModelOperator
from presidio.utils.airflow.schedule_interval_utils import get_schedule_interval
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

class SmartModelDagBuilder(PresidioDagBuilder):
    """
           A "Smart Model DAG" builder -
           The "Smart Model DAG" consists of smart accumulating operator followed by smart model build operator
           The smart accumulating operator responsible for accumulating the smart events
           The smart model build operator is responsible for building the models
           Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

           returns the DAG according to the given smart
           """

    build_model_interval_conf_key = "components.ade.models.smart_records.build_model_interval_in_days"
    build_model_interval_default_value = 1
    accumulate_interval_conf_key = "components.ade.models.smart_records.accumulate_interval_in_days"
    accumulate_interval_default_value = 1
    min_data_time_range_for_building_models_conf_key = "components.ade.models.smart_records.min_data_time_range_for_building_models_in_days"
    min_data_time_range_for_building_models_default_value = 14

    def __init__(self):
        config_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self._build_model_interval = config_reader.read_daily_timedelta(
            SmartModelDagBuilder.build_model_interval_conf_key,
            SmartModelDagBuilder.build_model_interval_default_value)
        self._accumulate_interval = config_reader.read_daily_timedelta(
            SmartModelDagBuilder.accumulate_interval_conf_key,
            SmartModelDagBuilder.accumulate_interval_default_value)

        self._min_gap_from_dag_start_date_to_start_accumulating = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(
            config_reader)
        self._min_gap_from_dag_start_date_to_start_modeling = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_modeling(
            config_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_accumulating(config_reader):
        raw_model_min_gap_from_dag_start_date_to_start_modeling = RawModelDagBuilder.get_min_gap_from_dag_start_date_to_start_raw_modeling(
            config_reader)
        aggr_model_min_gap_from_dag_start_date_to_start_modeling = AggrModelDagBuilder.get_min_gap_from_dag_start_date_to_start_aggr_modeling(
            config_reader)
        return max(raw_model_min_gap_from_dag_start_date_to_start_modeling,
                   aggr_model_min_gap_from_dag_start_date_to_start_modeling)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(config_reader):
        return SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(config_reader) + \
               config_reader.read_daily_timedelta(SmartModelDagBuilder.min_data_time_range_for_building_models_conf_key,
                                                  SmartModelDagBuilder.min_data_time_range_for_building_models_default_value)

    def build(self, smart_model_dag):
        """
        Fill the given "Smart Model DAG" with smart accumulating operator followed by smart model build operator
        The smart accumulating operator responsible for accumulating the smart events
        The smart model build operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

        :param smart_model_dag: The smart_model DAG to populate
        :type smart_model_dag: airflow.models.DAG
        :return: The smart model DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        smart_accumulate_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_accumulate_short_circuit',
            dag=smart_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._accumulate_interval,
                                                                     get_schedule_interval(smart_model_dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_model_dag,
                                                 self._min_gap_from_dag_start_date_to_start_accumulating,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(smart_model_dag))
        )

        # defining the smart model accumulator
        smart_model_accumulate_operator = SmartModelAccumulateOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            smart_events_conf=smart_model_dag.default_args.get("smart_conf_name"),
            dag=smart_model_dag
        )

        smart_accumulate_short_circuit_operator >> smart_model_accumulate_operator

        smart_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_model_short_circuit',
            dag=smart_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_model_interval,
                                                                     get_schedule_interval(smart_model_dag)) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                 smart_model_dag,
                                                 self._min_gap_from_dag_start_date_to_start_modeling,
                                                 kwargs['execution_date'],
                                                 get_schedule_interval(smart_model_dag)))

        # defining the smart model
        smart_model_operator = SmartModelOperator(smart_events_conf=smart_model_dag.default_args.get("smart_conf_name"),
                                                  command="process",
                                                  session_id=smart_model_dag.dag_id,
                                                  dag=smart_model_dag)

        smart_model_short_circuit_operator >> smart_model_operator
        smart_model_accumulate_operator.set_downstream(smart_model_short_circuit_operator)
        return smart_model_dag
