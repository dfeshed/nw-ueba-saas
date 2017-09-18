from datetime import timedelta
from airflow.operators.python_operator import ShortCircuitOperator

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.model.raw_model_feature_aggregation_buckets_operator import RawModelFeatureAggregationBucketsOperator
from presidio.operators.model.raw_model_operator import RawModelOperator
from presidio.utils.services.fixed_duration_strategy import FIX_DURATION_STRATEGY_DAILY, is_execution_date_valid
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


class RawModelDagBuilder(PresidioDagBuilder):
    """
        A "Raw Model DAG" builder - The "Raw Model DAG" consists of 2 tasks / operators
        (feature aggregation buckets and raw models) and can configured for any data source.
        The builder accepts the data source through the c'tor, and the "build" method builds and
        returns the DAG according to this data source.
        """

    def __init__(self, data_source):
        """
        C'tor.
        :param data_source: The data source whose events should be raw modeled
        :type data_source: str
        """

        self.data_source = data_source
        self._build_model_interval = timedelta(days=1)
        self._feature_aggregation_buckets_interval = timedelta(days=1)
        self._feature_aggregation_buckets_operator_gap_from_raw_model_operator_in_timedelta = timedelta(days=2)

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
                                                                     raw_model_dag.schedule_interval),
            provide_context=True
        )
        task_sensor_service.add_task_short_circuit(raw_model_operator, raw_model_short_circuit_operator)

        # defining the dependencies between the operators
        task_sensor_service.add_task_gap_sensor(raw_model_feature_aggregation_buckets_operator,
                                                raw_model_operator,
                                                self._feature_aggregation_buckets_operator_gap_from_raw_model_operator_in_timedelta)
        raw_model_feature_aggregation_buckets_operator.set_downstream(raw_model_short_circuit_operator)


        return raw_model_dag