from airflow.operators.python_operator import ShortCircuitOperator

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.aggr_model_accumulate_aggregation_operator import AggrModelAccumulateAggregationsOperator
from presidio.operators.model.aggr_model_operator import AggrModelOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid
from presidio.utils.configuration.config_server_configuration_reader_singleton import ConfigServerConfigurationReaderSingleton


class AggrModelDagBuilder(PresidioDagBuilder):
    """
        An "Aggr Model DAG" builder - 
        The "Aggr Model DAG" consists of one sub dag followed by operator
        The sub dag responsible for building and accumulating the aggregation events data
        The operator is respobsible for building the models
        Accumulating the data will happen once a day whereas the models might be built once a day or less (i.e. once 2 days)
        
        There are 2 parameters that define the aggregation events to be built, accumulated and modeled:
        - data source
        - fixed duration strategy - The duration covered by the aggregations (e.g. hourly or daily)
        
        returns the DAG according to the given data source and fixed duration strategy
        """

    build_model_interval_conf_key = "components.ade.models.feature_aggregation_records.build_model_interval_in_days"
    build_model_interval_default_value = 1
    accumulate_interval_conf_key = "components.ade.models.feature_aggregation_records.accumulate_interval_in_days"
    accumulate_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_modeling_conf_key = "components.ade.models.feature_aggregation_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_modeling_default_value = 14

    def __init__(self, data_source, fixed_duration_strategy):
        """
        C'tor.
        :param data_source: The data source from which the aggregation events are being built accumulated and finnaly modeled.
        :type data_source: str
        :param fixed_duration_strategy: The duration covered by the aggregations (e.g. hourly or daily)
        :type fixed_duration_strategy: datetime.timedelta
        """

        self._data_source = data_source
        self._fixed_duration_strategy = fixed_duration_strategy

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._build_model_interval = config_reader.read_daily_timedelta(AggrModelDagBuilder.build_model_interval_conf_key,
                                                                        AggrModelDagBuilder.build_model_interval_default_value)
        self._accumulate_interval = config_reader.read_daily_timedelta(AggrModelDagBuilder.accumulate_interval_conf_key,
                                                                       AggrModelDagBuilder.accumulate_interval_default_value)
        self._min_gap_from_dag_start_date_to_start_modeling = self.get_min_gap_from_dag_start_date_to_start_modeling(config_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(config_reader):
        return config_reader.read_daily_timedelta(AggrModelDagBuilder.min_gap_from_dag_start_date_to_start_modeling_conf_key,
                                                  AggrModelDagBuilder.min_gap_from_dag_start_date_to_start_modeling_default_value)

    def build(self, aggr_model_dag):
        """
        Builds the "Accumulate Aggregation Events" sub dag, the "Aggr Models" operator and wire the second as downstream of the first.
        :param aggr_model_dag: The DAG to which the operator flow should be added.
        :type aggregations_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        task_sensor_service = TaskSensorService()

        #defining the Accumulate operator
        aggr_model_accumulate_aggregations_operator = AggrModelAccumulateAggregationsOperator(
            fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
            feature_bucket_strategy=self._fixed_duration_strategy,
            command=PresidioDagBuilder.presidio_command,
            data_source=self._data_source,
            dag=aggr_model_dag)
        aggr_accumulate_short_circuit_operator = ShortCircuitOperator(
            task_id='aggr_accumulate_short_circuit{0}'.format(self._data_source),
            dag=aggr_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._accumulate_interval,
                                                                     aggr_model_dag.schedule_interval),
            provide_context=True
        )
        task_sensor_service.add_task_short_circuit(aggr_model_accumulate_aggregations_operator, aggr_accumulate_short_circuit_operator)

        #defining the model operator
        aggr_model_operator = AggrModelOperator(data_source=self._data_source,
                                              command="process",
                                              session_id=aggr_model_dag.dag_id.split('.', 1)[0],
                                              dag=aggr_model_dag)
        aggr_model_short_circuit_operator = ShortCircuitOperator(
            task_id='aggr_model_short_circuit{0}'.format(self._data_source),
            dag=aggr_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_model_interval,
                                                                     aggr_model_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(aggr_model_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_modeling,
                                                                                                                                   kwargs['execution_date']),
            provide_context=True
        )
        task_sensor_service.add_task_sequential_sensor(aggr_model_operator)
        task_sensor_service.add_task_short_circuit(aggr_model_operator, aggr_model_short_circuit_operator)

        # defining the dependencies between the operators
        aggr_model_accumulate_aggregations_operator.set_downstream(aggr_model_short_circuit_operator)

        return aggr_model_dag