from datetime import timedelta

from airflow import DAG
from airflow.operators.python_operator import ShortCircuitOperator
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.aggregation.aggregations_dag_builder import AggregationsDagBuilder
from presidio.operators.smart.smart_events_operator import SmartEventsOperator
from presidio.utils.date_time import fixed_duration_strategy_to_string
from presidio.utils.airflow.services.fixed_duration_strategy import is_last_interval_of_fixed_duration
from presidio.builders.presidio_dag_builder import PresidioDagBuilder

FIX_DURATION_STRATEGY_HOURLY = timedelta(hours=1)
FIX_DURATION_STRATEGY_DAILY = timedelta(days=1)


class AnomalyDetectionEngineDagBuilder(PresidioDagBuilder):
    """
    The "Anomaly Detection Engine" DAG consists of all the hourly and daily
    aggregation, scoring, modeling and smart tasks, per all configured data sources.
    """

    def __init__(self, data_sources, hourly_smart_events_confs, daily_smart_events_confs):
        """
        C'tor.
        :param data_sources: The data sources whose events should be handled
        :type data_sources: list[str]
        :param hourly_smart_events_confs: A "Smart Events Operator" is added for each hourly smart events configuration
        :type hourly_smart_events_confs: list[str]
        :param daily_smart_events_confs: A "Smart Events Operator" is added for each daily smart events configuration
        :type daily_smart_events_confs: list[str]
        """

        self.data_sources = data_sources
        self.hourly_smart_events_confs = hourly_smart_events_confs
        self.daily_smart_events_confs = daily_smart_events_confs

    def build(self, anomaly_detection_engine_dag):
        """
        Receives an ADE DAG, creates the operators (aggregation operators, scoring operators, model
        operators, subDAGs, etc.), links them to the DAG and configures the dependencies between them.
        :param anomaly_detection_engine_dag: The ADE DAG to populate
        :type anomaly_detection_engine_dag: airflow.models.DAG
        :return: The given ADE DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        hourly_aggregations_sub_dag_operators = []
        daily_aggregations_sub_dag_operators = []

        # Iterate all configured data sources
        for data_source in self.data_sources:

            # Create the "hourly short circuit operator" that allows the flow according to interval and fixed_duration
            hourly_short_circuit_operator = ShortCircuitOperator(
                task_id='hourly_{}_short_circuit'.format(data_source),
                python_callable=lambda **kwargs: is_last_interval_of_fixed_duration(kwargs['execution_date'], FIX_DURATION_STRATEGY_HOURLY, anomaly_detection_engine_dag.schedule_interval),
                provide_context=True
            )

            # Create the hourly aggregations subDAG operator for the data source
            hourly_aggregations_sub_dag_operator = self._get_aggregations_sub_dag_operator(
                timedelta(hours=1),
                data_source,
                anomaly_detection_engine_dag
            )

            # Create the "daily short circuit operator" that allows the flow from the hourly
            # aggregations subDAG to the daily aggregations subDAG only at the end of the day
            daily_short_circuit_operator = ShortCircuitOperator(
                task_id='daily_{}_short_circuit'.format(data_source),
                python_callable=lambda **kwargs: is_last_interval_of_fixed_duration(kwargs['execution_date'], FIX_DURATION_STRATEGY_DAILY, anomaly_detection_engine_dag.schedule_interval),
                provide_context=True
            )

            # Create the daily aggregations subDAG operator for the data source
            daily_aggregations_sub_dag_operator = self._get_aggregations_sub_dag_operator(
                timedelta(days=1),
                data_source,
                anomaly_detection_engine_dag
            )

            # Configure the dependencies between the operators
            hourly_short_circuit_operator.set_downstream(hourly_aggregations_sub_dag_operator)
            hourly_aggregations_sub_dag_operator.set_downstream(daily_aggregations_sub_dag_operator)
            daily_short_circuit_operator.set_downstream(daily_aggregations_sub_dag_operator)

            hourly_aggregations_sub_dag_operators.append(hourly_aggregations_sub_dag_operator)
            daily_aggregations_sub_dag_operators.append(daily_aggregations_sub_dag_operator)

        # Iterate all hourly smart events configurations
        for hourly_smart_events_conf in self.hourly_smart_events_confs:
            # Create the smart events operator for the configuration
            hourly_smart_events_operator = SmartEventsOperator(
                fixed_duration_strategy=timedelta(hours=1),
                smart_events_conf=hourly_smart_events_conf,
                dag=anomaly_detection_engine_dag
            )

            # The hourly smart events operator should start after all hourly aggregations subDAG operators are finished
            for hourly_aggregations_sub_dag_operator in hourly_aggregations_sub_dag_operators:
                hourly_aggregations_sub_dag_operator.set_downstream(hourly_smart_events_operator)

        # Iterate all daily smart events configurations
        for daily_smart_events_conf in self.daily_smart_events_confs:
            # Create the smart events operator for the configuration
            daily_smart_events_operator = SmartEventsOperator(
                fixed_duration_strategy=timedelta(days=1),
                smart_events_conf=daily_smart_events_conf,
                dag=anomaly_detection_engine_dag
            )

            # The daily smart events operator should start after all daily aggregations subDAG operators are finished
            for daily_aggregations_sub_dag_operator in daily_aggregations_sub_dag_operators:
                daily_aggregations_sub_dag_operator.set_downstream(daily_smart_events_operator)

        return anomaly_detection_engine_dag

    @staticmethod
    def _get_aggregations_sub_dag_operator(fixed_duration_strategy, data_source, anomaly_detection_engine_dag):
        aggregations_dag_id = '{}_{}_aggregations'.format(
            fixed_duration_strategy_to_string(fixed_duration_strategy),
            data_source
        )

        aggregations_dag = DAG(
            dag_id='{}.{}'.format(anomaly_detection_engine_dag.dag_id, aggregations_dag_id),
            schedule_interval=anomaly_detection_engine_dag.schedule_interval,
            start_date=anomaly_detection_engine_dag.start_date
        )

        return SubDagOperator(
            subdag=AggregationsDagBuilder(fixed_duration_strategy, data_source).build(aggregations_dag),
            task_id=aggregations_dag_id,
            dag=anomaly_detection_engine_dag
        )
