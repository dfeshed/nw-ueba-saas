from airflow.operators.python_operator import ShortCircuitOperator

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.raw_model_feature_aggregation_buckets_operator import RawModelFeatureAggregationBucketsOperator
from presidio.operators.model.raw_model_operator import RawModelOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import ConfigServerConfigurationReaderSingleton


class RawModelDagBuilder(PresidioDagBuilder):
    """
        A "Raw Model DAG" builder - The "Raw Model DAG" consists of 2 tasks / operators
        (feature aggregation buckets and raw models) and can configured for any data source.
        The builder accepts the data source through the c'tor, and the "build" method builds and
        returns the DAG according to this data source.
        """

    build_model_interval_conf_key = "components.ade.models.enriched_records.build_model_interval_in_days"
    build_model_interval_default_value = 1
    feature_aggregation_buckets_interval_conf_key = "components.ade.models.enriched_records.feature_aggregation_buckets_interval_in_days"
    feature_aggregation_buckets_interval_default_value = 1
    min_gap_from_dag_start_date_to_start_modeling_conf_key = "components.ade.models.enriched_records.min_data_time_range_for_building_models_in_days"
    min_gap_from_dag_start_date_to_start_modeling_default_value = 14

    def __init__(self, data_source):
        """
        C'tor.
        :param data_source: The data source whose events should be raw modeled
        :type data_source: str
        """

        self.data_source = data_source

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._build_model_interval = config_reader.read_daily_timedelta(RawModelDagBuilder.build_model_interval_conf_key,
                                                                        RawModelDagBuilder.build_model_interval_default_value)
        self._feature_aggregation_buckets_interval = \
            config_reader.read_daily_timedelta(RawModelDagBuilder.feature_aggregation_buckets_interval_conf_key,
                                               RawModelDagBuilder.feature_aggregation_buckets_interval_default_value)
        self._min_gap_from_dag_start_date_to_start_modeling = self.get_min_gap_from_dag_start_date_to_start_modeling(config_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_modeling(config_reader):
        return config_reader.read_daily_timedelta(RawModelDagBuilder.min_gap_from_dag_start_date_to_start_modeling_conf_key,
                                           RawModelDagBuilder.min_gap_from_dag_start_date_to_start_modeling_default_value)

    def build(self, raw_model_dag):
        """
        Builds the "Feature Aggregation Buckets" operator, the "Raw Models" operator and wire the second as downstream of the first.
        :param raw_model_dag: The DAG to which the operator flow should be added.
        :type aggregations_dag: airflow.models.DAG
        :return: The input DAG, after the operator flow was added
        :rtype: airflow.models.DAG
        """
        task_sensor_service = TaskSensorService()

        #defining feature aggregation buckets operator
        raw_model_feature_aggregation_buckets_operator = RawModelFeatureAggregationBucketsOperator(fixed_duration_strategy = FIX_DURATION_STRATEGY_DAILY,
                                                                                                   command=PresidioDagBuilder.presidio_command,
                                                                                                   data_source=self.data_source,
                                                                                                   dag=raw_model_dag)
        feature_aggregation_buckets_short_circuit_operator = ShortCircuitOperator(
            task_id=('feature_aggregation_buckets_short_circuit{0}'.format(self.data_source)),
            dag=raw_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._feature_aggregation_buckets_interval,
                                                                     raw_model_dag.schedule_interval),
            provide_context=True
        )
        task_sensor_service.add_task_short_circuit(raw_model_feature_aggregation_buckets_operator,
                                                   feature_aggregation_buckets_short_circuit_operator)

        # defining model operator
        raw_model_operator = RawModelOperator(data_source=self.data_source,
                                              command="process",
                                              session_id=raw_model_dag.dag_id.split('.',1)[0],
                                              dag=raw_model_dag)
        raw_model_short_circuit_operator = ShortCircuitOperator(
            task_id='raw_model_short_circuit{0}'.format(self.data_source),
            dag=raw_model_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     self._build_model_interval,
                                                                     raw_model_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(raw_model_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_modeling,
                                                                                                                                   kwargs['execution_date']),
            provide_context=True
        )
        task_sensor_service.add_task_sequential_sensor(raw_model_operator)
        task_sensor_service.add_task_short_circuit(raw_model_operator, raw_model_short_circuit_operator)

        # defining the dependencies between the operators
        raw_model_feature_aggregation_buckets_operator.set_downstream(raw_model_short_circuit_operator)


        return raw_model_dag