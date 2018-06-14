from datetime import timedelta
import logging

from presidio.builders.ade.aggregation.aggregations_dag_builder import AggregationsDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.smart.smart_events_operator import SmartEventsOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string, FIX_DURATION_STRATEGY_HOURLY, is_execution_date_valid
from presidio.utils.configuration.config_server_configuration_reader_singleton import ConfigServerConfigurationReaderSingleton
from presidio.builders.ade.model.smart_model_dag_builder import SmartModelDagBuilder

class AnomalyDetectionEngineScoringDagBuilder(PresidioDagBuilder):
    """
    The "Anomaly Detection Engine Scoring" DAG consists of all the hourly and daily
    aggregation, scoring, and smart tasks, per all configured data sources and smart configuration.
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

        config_reader = ConfigServerConfigurationReaderSingleton().config_reader
        # currently we start scoring only when smart models start accumulating.
        # later we will probably run scoring according to the specific modeling configuration.
        self._min_gap_from_dag_start_date_to_start_scoring = self.get_min_gap_from_dag_start_date_to_start_scoring(config_reader)

    @staticmethod
    def get_min_gap_from_dag_start_date_to_start_scoring(config_reader):
        return SmartModelDagBuilder.get_min_gap_from_dag_start_date_to_start_accumulating(config_reader)

    def build(self, anomaly_detection_engine_scoring_dag):
        """
        Receives an ADE DAG, creates the operators (aggregation operators, scoring operators, subDAGs, etc.), 
        links them to the DAG and configures the dependencies between them.
        :param anomaly_detection_engine_scoring_dag: The ADE DAG to populate
        :type anomaly_detection_engine_scoring_dag: airflow.models.DAG
        :return: The given ADE SCORING DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        logging.info("populating the ade scoring dag, dag_id=%s for data sources: %s and hourly smarts: %s",
                     anomaly_detection_engine_scoring_dag.dag_id, self.data_sources, self.hourly_smart_events_confs)
        hourly_aggregations_sub_dag_operator_list = []

        task_sensor_service = TaskSensorService()


        # defining hourly and daily short circuit operators which should be wired to the sub dags and operators that
        # defined directly under the ADE SCORING dag
        hourly_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_scoring_hourly_short_circuit',
            dag=anomaly_detection_engine_scoring_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_HOURLY,
                                                                     anomaly_detection_engine_scoring_dag.schedule_interval) &
                                             PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(anomaly_detection_engine_scoring_dag,
                                                                                                                                   self._min_gap_from_dag_start_date_to_start_scoring,
                                                                                                                                   kwargs['execution_date'],
                                                                                                                                   anomaly_detection_engine_scoring_dag.schedule_interval)
        )

        # Iterate all configured data sources and
        # define the hourly and daily aggregation sub dags, their sensors, short circuit and their flow dependencies.
        self._build_data_source_scoring_flow(anomaly_detection_engine_scoring_dag,task_sensor_service,
                                             hourly_short_circuit_operator, hourly_aggregations_sub_dag_operator_list)

        # Iterate all hourly smart configurations and
        # define the smart operator with its sensor, short circuit and flow dependecies.
        self._build_smart_flow(anomaly_detection_engine_scoring_dag, self.hourly_smart_events_confs,
                               FIX_DURATION_STRATEGY_HOURLY, task_sensor_service,
                               hourly_short_circuit_operator,
                               hourly_aggregations_sub_dag_operator_list)

        return anomaly_detection_engine_scoring_dag

    def _build_data_source_scoring_flow(self, anomaly_detection_engine_scoring_dag, task_sensor_service,
                                        hourly_short_circuit_operator, hourly_aggregations_sub_dag_operator_list):
        # Iterate all configured data sources and
        # define the hourly and daily aggregation sub dags, their sensors, short circuit and their flow dependencies.
        for data_source in self.data_sources:
            # Create the hourly aggregations subDAG operator for the data source
            hourly_aggregations_sub_dag_operator = self._get_aggregations_sub_dag_operator(
                FIX_DURATION_STRATEGY_HOURLY,
                data_source,
                anomaly_detection_engine_scoring_dag
            )
            task_sensor_service.add_task_sequential_sensor(hourly_aggregations_sub_dag_operator)
            task_sensor_service.add_task_short_circuit(hourly_aggregations_sub_dag_operator,
                                                       hourly_short_circuit_operator)

            hourly_aggregations_sub_dag_operator_list.append(hourly_aggregations_sub_dag_operator)

    def _build_smart_flow(self, anomaly_detection_engine_scoring_dag, smart_events_confs, fixed_duration_strategy,
                          task_sensor_service, smart_short_circuit_operator,
                          aggregations_sub_dag_operator_list):
        # Iterate all smart configurations and
        # define the smart operator with its sensor, short circuit and flow dependecies.
        for smart_events_conf in smart_events_confs:
            if smart_events_conf:
                # Create the smart events operator for the configuration
                smart_events_operator = SmartEventsOperator(
                    command=SmartEventsOperator.liors_special_run_command,
                    fixed_duration_strategy=fixed_duration_strategy,
                    smart_events_conf=smart_events_conf,
                    dag=anomaly_detection_engine_scoring_dag
                )
                task_sensor_service.add_task_sequential_sensor(smart_events_operator)
                task_sensor_service.add_task_short_circuit(smart_events_operator, smart_short_circuit_operator)

                # The hourly smart events operator should start after all hourly aggregations subDAG operators are finished
                for aggregations_sub_dag_operator in aggregations_sub_dag_operator_list:
                    aggregations_sub_dag_operator.set_downstream(smart_events_operator)

            else:
                raise Exception("smart configuration is None or empty")

    def _get_aggregations_sub_dag_operator(self, fixed_duration_strategy, data_source, anomaly_detection_engine_dag):
        aggregations_dag_id = '{}_{}_aggregations'.format(
            fixed_duration_strategy_to_string(fixed_duration_strategy),
            data_source
        )

        return self._create_sub_dag_operator(
            AggregationsDagBuilder(fixed_duration_strategy, data_source), aggregations_dag_id,
            anomaly_detection_engine_dag)




