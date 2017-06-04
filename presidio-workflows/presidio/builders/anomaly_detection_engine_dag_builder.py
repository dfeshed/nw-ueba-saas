from datetime import timedelta

from airflow import DAG
from airflow.operators.python_operator import ShortCircuitOperator
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.aggregation.aggregations_dag_builder import AggregationsDagBuilder
from presidio.operators.smart.smart_events_operator import SmartEventsOperator
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string,\
    FIX_DURATION_STRATEGY_DAILY, FIX_DURATION_STRATEGY_HOURLY
from presidio.operators.presidio_task_sensor_service import PresidioTaskSensorService
from presidio.builders.presidio_dag_builder import PresidioDagBuilder



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

        hourly_aggregations_max_gap_from_daily_aggregations_in_timedelta = timedelta(days=5)
        hourly_aggregations_max_gap_from_hourly_smart_in_timedelta = timedelta(days=5)
        daily_aggregations_max_gap_from_daily_smart_in_timedelta = timedelta(days=5)

        hourly_aggregations_sub_dag_operators = []
        daily_aggregations_sub_dag_operators = []
        task_sensor_service = PresidioTaskSensorService()

        # Iterate all configured data sources
        for data_source in self.data_sources:
            # Create the hourly aggregations subDAG operator for the data source
            hourly_aggregations_sub_dag_operator = self._get_aggregations_sub_dag_operator(
                FIX_DURATION_STRATEGY_HOURLY,
                data_source,
                anomaly_detection_engine_dag
            )
            task_sensor_service.add_task_short_circuit_fixed_duration_operator(hourly_aggregations_sub_dag_operator,
                                                                               FIX_DURATION_STRATEGY_HOURLY,
                                                                               anomaly_detection_engine_dag.schedule_interval)
            task_sensor_service.add_task_sequential_sensor(hourly_aggregations_sub_dag_operator)


            # Create the daily aggregations subDAG operator for the data source
            daily_aggregations_sub_dag_operator = self._get_aggregations_sub_dag_operator(
                FIX_DURATION_STRATEGY_DAILY,
                data_source,
                anomaly_detection_engine_dag
            )
            task_sensor_service.add_task_short_circuit_fixed_duration_operator(daily_aggregations_sub_dag_operator,
                                                                               FIX_DURATION_STRATEGY_DAILY,
                                                                               anomaly_detection_engine_dag.schedule_interval)
            task_sensor_service.add_task_sequential_sensor(daily_aggregations_sub_dag_operator)
            task_sensor_service.add_task_gap_sensor(hourly_aggregations_sub_dag_operator,
                                                    daily_aggregations_sub_dag_operator,
                                                    hourly_aggregations_max_gap_from_daily_aggregations_in_timedelta)

            # Configure the dependencies between the operators
            hourly_aggregations_sub_dag_operator.set_downstream(daily_aggregations_sub_dag_operator)

            hourly_aggregations_sub_dag_operators.append(hourly_aggregations_sub_dag_operator)
            daily_aggregations_sub_dag_operators.append(daily_aggregations_sub_dag_operator)

        # Iterate all hourly smart events configurations
        for hourly_smart_events_conf in self.hourly_smart_events_confs:
            # Create the smart events operator for the configuration
            hourly_smart_events_operator = SmartEventsOperator(
                fixed_duration_strategy=FIX_DURATION_STRATEGY_HOURLY,
                smart_events_conf=hourly_smart_events_conf,
                dag=anomaly_detection_engine_dag
            )
            task_sensor_service.add_task_sequential_sensor(hourly_smart_events_operator)

            # The hourly smart events operator should start after all hourly aggregations subDAG operators are finished
            for hourly_aggregations_sub_dag_operator in hourly_aggregations_sub_dag_operators:
                hourly_aggregations_sub_dag_operator.set_downstream(hourly_smart_events_operator)
                task_sensor_service.add_task_gap_sensor(hourly_aggregations_sub_dag_operator,
                                                        hourly_smart_events_operator,
                                                        hourly_aggregations_max_gap_from_hourly_smart_in_timedelta)


        # Iterate all daily smart events configurations
        for daily_smart_events_conf in self.daily_smart_events_confs:
            # Create the smart events operator for the configuration
            daily_smart_events_operator = SmartEventsOperator(
                fixed_duration_strategy=FIX_DURATION_STRATEGY_DAILY,
                smart_events_conf=daily_smart_events_conf,
                dag=anomaly_detection_engine_dag
            )
            task_sensor_service.add_task_sequential_sensor(daily_smart_events_operator)

            # The daily smart events operator should start after all daily aggregations subDAG operators are finished
            for daily_aggregations_sub_dag_operator in daily_aggregations_sub_dag_operators:
                daily_aggregations_sub_dag_operator.set_downstream(daily_smart_events_operator)
                task_sensor_service.add_task_gap_sensor(daily_aggregations_sub_dag_operator,
                                                        daily_smart_events_operator,
                                                        daily_aggregations_max_gap_from_daily_smart_in_timedelta)

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
