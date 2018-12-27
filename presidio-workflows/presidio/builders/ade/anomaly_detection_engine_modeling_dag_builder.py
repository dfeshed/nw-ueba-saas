from presidio.builders.ade.model.aggr_model_dag_builder import AggrModelDagBuilder
from presidio.builders.ade.model.raw_model_dag_builder import RawModelDagBuilder
from presidio.builders.ade.model.smart_model_dag_builder import SmartModelDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.operators.ade_manager.ade_manager_operator import AdeManagerOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.services.fixed_duration_strategy import fixed_duration_strategy_to_string, \
    FIX_DURATION_STRATEGY_DAILY, FIX_DURATION_STRATEGY_HOURLY, is_execution_date_valid


class AnomalyDetectionEngineModelingDagBuilder(PresidioDagBuilder):
    """
    The "Anomaly Detection Engine Modeling" DAG consists of all the modeling per all configured data sources and smarts.
    """

    def __init__(self, data_sources, hourly_smart_events_confs, daily_smart_events_confs):
        """
        C'tor.
        :param data_sources: The data sources for which raw and aggr models should be built
        :type data_sources: list[str]
        :param hourly_smart_events_confs: hourly smart events configuration for which smart models should be built
        :type hourly_smart_events_confs: list[str]
        :param daily_smart_events_confs: daily smart events configuration for which smart models should be built
        :type daily_smart_events_confs: list[str]
        """

        self.data_sources = data_sources
        self.hourly_smart_events_confs = hourly_smart_events_confs
        self.daily_smart_events_confs = daily_smart_events_confs

    def build(self, anomaly_detection_engine_modeling_dag):
        """
        Receives an ADE MODELING DAG, creates the operators and links them to the DAG and configures the dependencies between them.
        :param anomaly_detection_engine_modeling_dag: The ADE MODELING DAG to populate
        :type anomaly_detection_engine_modeling_dag: airflow.models.DAG
        :return: The given ADE MODELING DAG, after it has been populated
        :rtype: airflow.models.DAG
        """
        self.log.info("populating the ade modeling dag, dag_id=%s for data sources: %s and hourly smarts: %s",
                     anomaly_detection_engine_modeling_dag.dag_id, self.data_sources, self.hourly_smart_events_confs)

        task_sensor_service = TaskSensorService()

        daily_short_circuit_operator = self._create_infinite_retry_short_circuit_operator(
            task_id='ade_modeling_daily_short_circuit',
            dag=anomaly_detection_engine_modeling_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     anomaly_detection_engine_modeling_dag.schedule_interval)
        )

        self._build_data_source_model_dags(anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service)

        self._build_smart_model_dags(anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service)

        return anomaly_detection_engine_modeling_dag

    def _build_data_source_model_dags(self, anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service):
        # collect all sub dag operators, which use enriched data
        enriched_data_customer_tasks = []

        # Iterate all configured data sources and
        # define the raw models and hourly and daily aggregation models sub dags, their sensors and short circuit.
        for data_source in self.data_sources:
            # Create the raw model for the data source
            raw_model_presidio_dag_wiring = self._wire(RawModelDagBuilder(data_source), anomaly_detection_engine_modeling_dag,
                                               daily_short_circuit_operator, [], [], False)

            # Create the hourly aggr model for the data source
            aggr_model_presidio_dag_wiring = self._wire(AggrModelDagBuilder(data_source, FIX_DURATION_STRATEGY_HOURLY),
                                                anomaly_detection_engine_modeling_dag, daily_short_circuit_operator,
                                                [], [], False)

            enriched_data_customer_tasks.extend(raw_model_presidio_dag_wiring.last_tasks)
            enriched_data_customer_tasks.extend(aggr_model_presidio_dag_wiring.last_tasks)

        self._build_ade_manager_operator(anomaly_detection_engine_modeling_dag, enriched_data_customer_tasks,
                                         daily_short_circuit_operator)

    def _build_smart_model_dags(self, anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service):
        # Iterate all hourly smart configurations and
        # define the smart model sub dag with its sensor and short circuit
        for smart_events_conf in self.hourly_smart_events_confs:
            if smart_events_conf:
                # Create the smart model for the configuration
                aggr_model_presidio_dag_wiring = self._wire(SmartModelDagBuilder(FIX_DURATION_STRATEGY_HOURLY, smart_events_conf), anomaly_detection_engine_modeling_dag,
                                                    daily_short_circuit_operator, [], [], False)
            else:
                raise Exception("smart configuration is None or empty")

    def _build_ade_manager_operator(self, anomaly_detection_engine_modeling_dag, enriched_data_customer_tasks, daily_short_circuit_operator):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished to use it.
        Set daily_short_circuit in order to run AdeManagerOperator once a day.

        AdeManagerOperator instances do not run sequentially (AdeManagerOperator does not use sequential sensor)
         as a result of following assumption: AdeManagerOperator should run once a day (using daily_short_circuit),
         all instances will get skip status except the last instance, therefore sequential sensor is unnecessary.


        :param anomaly_detection_engine_modeling_dag: The ADE DAG
        :type anomaly_detection_engine_modeling_dag: airflow.models.DAG
        :param enriched_data_customer_tasks: sub_dag operators, who use enriched data.
        :param daily_short_circuit_operator: daily short_circuit
        """

        ade_manager_operator = AdeManagerOperator(
            command=AdeManagerOperator.enriched_ttl_cleanup_command,
            dag=anomaly_detection_engine_modeling_dag
        )

        daily_short_circuit_operator.set_downstream(ade_manager_operator)

        for enriched_data_customer_task in enriched_data_customer_tasks:
            enriched_data_customer_task.set_downstream(ade_manager_operator)
