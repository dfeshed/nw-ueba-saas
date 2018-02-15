from airflow.operators.bash_operator import BashOperator

from aggr_model_dag_builder import AggrModelDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.smart_model_accumulate_operator import SmartModelAccumulateOperator
from presidio.operators.model.smart_model_operator import SmartModelOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY
from raw_model_dag_builder import RawModelDagBuilder


class SmartModelDagBuilder(PresidioDagBuilder):
    """
        A "Smart Model DAG" builder - 
        The "Smart Model DAG" consists of smart accumulating operator followed by smart model build operator
        The smart accumulating operator responsible for accumulating the smart events
        The smart model build operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once a week)

        There are 2 parameters that define the aggregation events to be built, accumulated and modeled:
        - smart_events_conf - The name of the configuration defining the smart events
        - build_model_interval - The interval that new models should be calculated.

        returns the DAG according to the given data source and fixed duration strategy
        """

    build_model_interval_conf_key = "components.ade.models.smart_records.build_model_interval_in_days"
    build_model_interval_default_value = 1
    accumulate_interval_conf_key = "components.ade.models.smart_records.accumulate_interval_in_days"
    accumulate_interval_default_value = 1
    min_data_time_range_for_building_models_conf_key = "components.ade.models.smart_records.min_data_time_range_for_building_models_in_days"
    min_data_time_range_for_building_models_default_value = 14

    def __init__(self, fixed_duration_strategy, smart_events_conf):
        """
        C'tor.
        :param smart_events_conf: The name of the configuration defining the smart events.
        :type smart_events_conf: str
        """

        self._fixed_duration_strategy = fixed_duration_strategy
        self._smart_events_conf = smart_events_conf

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader

        self._build_model_interval = config_reader.read_daily_timedelta(SmartModelDagBuilder.build_model_interval_conf_key,
                                                                        SmartModelDagBuilder.build_model_interval_default_value)
        self._accumulate_interval = config_reader.read_daily_timedelta(SmartModelDagBuilder.accumulate_interval_conf_key,
                                                                       SmartModelDagBuilder.accumulate_interval_default_value)

        self._min_gap_from_dag_start_date_to_start_accumulating = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(config_reader)
        self._min_gap_from_dag_start_date_to_start_modeling = SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_modeling(config_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_accumulating(config_reader):
        raw_model_min_gap_from_dag_start_date_to_start_modeling = RawModelDagBuilder.get_min_gap_from_dag_start_date_to_start_modeling(config_reader)
        aggr_model_min_gap_from_dag_start_date_to_start_modeling = AggrModelDagBuilder.get_min_gap_from_dag_start_date_to_start_modeling(config_reader)
        return max(raw_model_min_gap_from_dag_start_date_to_start_modeling, aggr_model_min_gap_from_dag_start_date_to_start_modeling)

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

        :param smart_model_dag: The DAG to which the operator flow should be added.
        :type smart_model_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        task_sensor_service = TaskSensorService()

        #defining the smart model accumulator
        smart_model_accumulate_operator = SmartModelAccumulateOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            command=PresidioDagBuilder.presidio_command,
            smart_events_conf=self._smart_events_conf,
            dag=smart_model_dag)
        smart_accumulate_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_accumulate_short_circuit',
            dag=smart_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._accumulate_interval,
                                                                     smart_model_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(smart_model_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_accumulating,
                                                                                                                                   kwargs['execution_date'],
                                                                                                                                   smart_model_dag.schedule_interval)
        )
        task_sensor_service.add_task_short_circuit(smart_model_accumulate_operator, smart_accumulate_short_circuit_operator)

        #defining the smart model
        smart_model_operator = SmartModelOperator(smart_events_conf=self._smart_events_conf,
                                                command="process",
                                                session_id=smart_model_dag.dag_id.split('.', 1)[0],
                                                dag=smart_model_dag)

        smart_model_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='smart_model_short_circuit',
            dag=smart_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_model_interval,
                                                                     smart_model_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(smart_model_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_modeling,
                                                                                                                                   kwargs['execution_date'],
                                                                                                                                   smart_model_dag.schedule_interval)
        )
        task_sensor_service.add_task_sequential_sensor(smart_model_operator)
        task_sensor_service.add_task_short_circuit(smart_model_operator, smart_model_short_circuit_operator)

        #defining the dependencies between the operators
        smart_model_accumulate_operator.set_downstream(smart_model_short_circuit_operator)


        #the following line is a workaround for bug in Airflow AIRFLOW-585 : Fix race condition in backfill execution loop
        t2 = BashOperator(
            task_id='sleep',
            bash_command='sleep 5',
            dag=smart_model_dag, retries=99999)

        return smart_model_dag