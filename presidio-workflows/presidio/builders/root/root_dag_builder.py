from datetime import timedelta


from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.retention_dag_builder import RetentionDagBuilder
from presidio.factories.abstract_dag_factory import AbstractDagFactory
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.utils.airflow.operators.sensor.root_dag_gap_sensor_operator import RootDagGapSensorOperator

from presidio.utils.airflow.schedule_interval_utils import set_schedule_interval
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid, FIX_DURATION_STRATEGY_DAILY


class RootDagBuilder(PresidioDagBuilder):

    def build(self, dag):
        """
        Receives a dag, creates root_dag_gap_sensor_operator and trigger operators:
        root_dag_gap_sensor_operator responsible for sensor all the dag instances.
        trigger operators responsible for triggering {schema}_indicator dags (ACTIVE_DIRECTORY_indicator, AUTHENTICATION_indicator etc...).
        :param dag: root dag
        :type dag: airflow.models.DAG
        :return: The given presidio core DAG, after it has been populated
        :rtype: airflow.models.DAG
        """
        triggers = []
        schemas = dag.default_args['schemas']
        for schema in schemas:
            dag_id = IndicatorDagFactory.get_dag_id(schema)
            trigger = self._create_expanded_trigger_dag_run_operator('{0}_{1}'.format(schema, "trigger"), dag_id, dag, None)
            set_schedule_interval(dag_id, dag.schedule_interval)
            triggers.append(trigger)

        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        retention_trigger = self._create_expanded_trigger_dag_run_operator('retention_trigger', "retention", dag,
                                                                    python_callable=lambda context, dag_run_obj: is_execution_date_valid(context['execution_date'],
                                                                    RetentionDagBuilder.get_retention_interval_in_hours(conf_reader),
                                                                    dag.schedule_interval) &
                                                                    PresidioDagBuilder.validate_the_gap_between_dag_start_date_and_current_execution_date(
                                                                    dag,
                                                                    timedelta(days=RetentionDagBuilder.get_min_time_to_start_retention_in_days(conf_reader)),
                                                                    context['execution_date'],
                                                                    dag.schedule_interval))

        triggers.append(retention_trigger)
        set_schedule_interval('retention', FIX_DURATION_STRATEGY_DAILY)

        dag_ids = AbstractDagFactory.get_registered_dag_ids()

        root_dag_gap_sensor_operator = RootDagGapSensorOperator(dag=dag,
                                                                task_id='all_dags_root_gap_sensor',
                                                                dag_ids=dag_ids,
                                                                execution_delta=timedelta(hours=23),
                                                                poke_interval=5)
        root_dag_gap_sensor_operator >> triggers
