from datetime import timedelta

from airflow.models import Variable

from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.factories.indicator_dag_factory import IndicatorDagFactory
from presidio.utils.airflow.operators.sensor.root_dag_gap_sensor_operator import RootDagGapSensorOperator

from presidio.utils.airflow.schedule_interval_utils import set_schedule_interval


class InitialDagBuilder(PresidioDagBuilder):

    def build(self, initial_dag):
        """
        Receives an initial_dag DAG, creates root_dag_gap_sensor_operator and trigger operators:
        root_dag_gap_sensor_operator responsible for sensor all the dag instances.
        trigger operators responsible for triggering {schema}_indicator dags (ACTIVE_DIRECTORY_indicator, AUTHENTICATION_indicator etc...).
        :param initial_dag:
        :param presidio_core_dag: The presidio core DAG to populate
        :type presidio_core_dag: airflow.models.DAG
        :return: The given presidio core DAG, after it has been populated
        :rtype: airflow.models.DAG
        """
        triggers = []
        schemas = initial_dag.default_args['schemas']
        for schema in schemas:
            dag_id = IndicatorDagFactory.get_dag_id(schema)
            trigger = self._create_expanded_trigger_dag_run_operator('{0}_{1}'.format(schema, "trigger"), dag_id, initial_dag, None)
            set_schedule_interval(dag_id, initial_dag.schedule_interval)
            triggers.append(trigger)

        dag_ids = Variable.get(key="dags", default_var=[])
        if dag_ids:
            dag_ids = dag_ids.split(", ")

        root_dag_gap_sensor_operator = RootDagGapSensorOperator(dag=initial_dag,
                                                                task_id='all_dags_root_gap_sensor',
                                                                dag_ids=dag_ids,
                                                                execution_delta=timedelta(hours=23),
                                                                poke_interval=5)
        root_dag_gap_sensor_operator >> triggers
