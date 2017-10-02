from airflow import DAG
from airflow.operators.python_operator import ShortCircuitOperator
from airflow.operators.subdag_operator import SubDagOperator

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

        task_sensor_service = TaskSensorService()

        daily_short_circuit_operator = ShortCircuitOperator(
            task_id='ade_scoring_daily_short_circuit',
            dag=anomaly_detection_engine_modeling_dag,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                     FIX_DURATION_STRATEGY_DAILY,
                                                                     anomaly_detection_engine_modeling_dag.schedule_interval),
            provide_context=True
        )

        self._build_data_source_model_dags(anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service)

        self._build_smart_model_dags(anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service)

        return anomaly_detection_engine_modeling_dag

    def _build_data_source_model_dags(self, anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service):
        # collect all sub dag operators, which use enriched data
        enriched_data_customer_tasks = []

        # Iterate all configured data sources and
        # define the hourly and daily aggregation sub dags, their sensors, short circuit and their flow dependencies.
        for data_source in self.data_sources:
            # Create the raw model subDag operator for the data source
            raw_model_sub_dag_operator = self._get_raw_model_sub_dag_operator(data_source,
                                                                              anomaly_detection_engine_modeling_dag)
            task_sensor_service.add_task_sequential_sensor(raw_model_sub_dag_operator)
            task_sensor_service.add_task_short_circuit(raw_model_sub_dag_operator, daily_short_circuit_operator)

            # Create the hourly aggr model subDag operator for the data source
            hourly_aggr_model_sub_dag_operator = self._get_aggr_model_sub_dag_operator(data_source,
                                                                                       FIX_DURATION_STRATEGY_HOURLY,
                                                                                       anomaly_detection_engine_modeling_dag)
            task_sensor_service.add_task_sequential_sensor(hourly_aggr_model_sub_dag_operator)
            task_sensor_service.add_task_short_circuit(hourly_aggr_model_sub_dag_operator, daily_short_circuit_operator)

            enriched_data_customer_tasks.append(hourly_aggr_model_sub_dag_operator)
            enriched_data_customer_tasks.append(raw_model_sub_dag_operator)

        self._build_manager_operator(anomaly_detection_engine_modeling_dag, enriched_data_customer_tasks,
                                     daily_short_circuit_operator)

    def _build_smart_model_dags(self, anomaly_detection_engine_modeling_dag, daily_short_circuit_operator, task_sensor_service):
        # Iterate all hourly smart configurations and
        # define the smart model sub dag with its sensor and short circuit
        for smart_events_conf in self.hourly_smart_events_confs:
            if smart_events_conf:
                # Create the smart model sub dag for the configuration
                smart_model_sub_dag_operator = self._get_smart_model_sub_dag_operator(FIX_DURATION_STRATEGY_HOURLY,
                                                                                      smart_events_conf,
                                                                                      anomaly_detection_engine_modeling_dag)
                task_sensor_service.add_task_sequential_sensor(smart_model_sub_dag_operator)

                task_sensor_service.add_task_short_circuit(smart_model_sub_dag_operator,
                                                           daily_short_circuit_operator)
            else:
                raise Exception("smart configuration is None or empty")

    @staticmethod
    def _build_manager_operator(anomaly_detection_engine_modeling_dag, enriched_data_customer_tasks, daily_short_circuit_operator):
        """
        Create AdeManagerOperator in order to clean enriched data after all enriched data customer tasks finished to use it.
        Set daily_short_circuit in order to run ManagerOperator once a day.

        AdeManagerOperator instances do not run sequentially (AdeManagerOperator does not use sequential sensor)
         as a result of following assumption: AdeManagerOperator should run once a day (using daily_short_circuit),
         all instances will get skip status except the last instance, therefore sequential sensor is unnecessary.


        :param anomaly_detection_engine_modeling_dag: The ADE DAG
        :type anomaly_detection_engine_modeling_dag: airflow.models.DAG
        :param enriched_data_customer_tasks: sub_dag operators, who use enriched data.
        :param daily_short_circuit_operator: daily short_circuit
        """

        ade_manager_operator = AdeManagerOperator(
            command=AdeManagerOperator.cleanup_command,
            dag=anomaly_detection_engine_modeling_dag
        )

        daily_short_circuit_operator.set_downstream(ade_manager_operator)

        for enriched_data_customer_task in enriched_data_customer_tasks:
            enriched_data_customer_task.set_downstream(ade_manager_operator)


    def _get_raw_model_sub_dag_operator(self, data_source, anomaly_detection_engine_modeling_dag):
        raw_model_dag_id = '{}_raw_model'.format(data_source)

        return AnomalyDetectionEngineModelingDagBuilder.create_sub_dag_operator(RawModelDagBuilder(data_source),
                                                                               raw_model_dag_id, anomaly_detection_engine_modeling_dag)

    @staticmethod
    def _get_aggr_model_sub_dag_operator(data_source, fixed_duration_strategy, anomaly_detection_engine_modeling_dag):
        aggr_model_dag_id = '{}_{}_aggr_model'.format(
            data_source,
            fixed_duration_strategy_to_string(fixed_duration_strategy)
        )

        return AnomalyDetectionEngineModelingDagBuilder.create_sub_dag_operator(
            AggrModelDagBuilder(data_source, fixed_duration_strategy), aggr_model_dag_id,
            anomaly_detection_engine_modeling_dag)

    @staticmethod
    def _get_smart_model_sub_dag_operator(fixed_duration_strategy, smart_events_conf, anomaly_detection_engine_modeling_dag):
        smart_model_dag_id = '{}_smart_model_sub_dag'.format(smart_events_conf)

        return AnomalyDetectionEngineModelingDagBuilder.create_sub_dag_operator(
            SmartModelDagBuilder(fixed_duration_strategy, smart_events_conf), smart_model_dag_id,
            anomaly_detection_engine_modeling_dag)

    @staticmethod
    def create_sub_dag_operator(sub_dag_builder, sub_dag_id, anomaly_detection_engine_modeling_dag):
        """
        create a sub dag of the recieved "anomaly_detection_engine_modeling_dag" fill it with a flow using the sub_dag_builder
        and wrap it with a sub dag operator.
        :param sub_dag_builder: 
        :type sub_dag_builder: PresidioDagBuilder
        :param sub_dag_id:
        :type sub_dag_id: str
        :param anomaly_detection_engine_modeling_dag: The ADE DAG to populate
        :type anomaly_detection_engine_modeling_dag: airflow.models.DAG
        :return: The given ADE DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        sub_dag = DAG(

            dag_id='{}.{}'.format(anomaly_detection_engine_modeling_dag.dag_id, sub_dag_id),
            schedule_interval=anomaly_detection_engine_modeling_dag.schedule_interval,
            start_date=anomaly_detection_engine_modeling_dag.start_date
        )

        return SubDagOperator(
            subdag=sub_dag_builder.build(sub_dag),
            task_id=sub_dag_id,
            dag=anomaly_detection_engine_modeling_dag
        )
