import logging

from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.ade.anomaly_detection_engine_dag_builder import AnomalyDetectionEngineDagBuilder
from presidio.builders.input.input_dag_builder import InputDagBuilder
from presidio.builders.output.output_dag_builder import OutputDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


class PresidioCoreDagBuilder(PresidioDagBuilder):
    """
    The "Online rum" DAG consists of all the presidio flow
    """

    def __init__(self, data_sources):
        """
        C'tor.
        :param data_sources: The data sources whose events should be handled
        :type data_sources: list[str]
        """

        self.data_sources = data_sources

    def build(self, presidio_core_dag):
        """
        Receives an presidio core DAG, creates the operators (input, ade and output), links them to the DAG and 
        configures the dependencies between them.
        :param presidio_core_dag: The presidio core DAG to populate
        :type presidio_core_dag: airflow.models.DAG
        :return: The given presidio core DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        logging.info("populating the presidio core dag, dag_id=%s ", presidio_core_dag.dag_id)

        task_sensor_service = TaskSensorService()

        input_sub_dag_operator = self._get_input_sub_dag_operator(
            self.data_sources,
            presidio_core_dag
        )
        task_sensor_service.add_task_sequential_sensor(input_sub_dag_operator)

        ade_sub_dag_operator = self._get_ade_sub_dag_operator(self.data_sources, presidio_core_dag)

        output_sub_dag_operator = self._get_output_sub_dag_operator(
            self.data_sources,
            presidio_core_dag
        )

        task_sensor_service.add_task_sequential_sensor(output_sub_dag_operator)

        input_sub_dag_operator >> ade_sub_dag_operator >> output_sub_dag_operator

        return presidio_core_dag

    @staticmethod
    def _get_input_sub_dag_operator(data_sources, presidio_core_dag):
        input_dag_id = 'input_dag'

        input_dag = DAG(
            dag_id='{}.{}'.format(presidio_core_dag.dag_id, input_dag_id),
            schedule_interval=presidio_core_dag.schedule_interval,
            start_date=presidio_core_dag.start_date,
            default_args=presidio_core_dag.default_args
        )

        return SubDagOperator(
            subdag=InputDagBuilder(data_sources).build(input_dag),
            task_id=input_dag_id,
            dag=presidio_core_dag
        )

    @staticmethod
    def _get_ade_sub_dag_operator(data_sources, presidio_core_dag):
        default_args = presidio_core_dag.default_args
        daily_smart_events_confs = default_args.get("daily_smart_events_confs")
        hourly_smart_events_confs = default_args.get("hourly_smart_events_confs")

        ade_dag_id = 'ade_dag'

        ade_dag = DAG(
            dag_id='{}.{}'.format(presidio_core_dag.dag_id, ade_dag_id),
            schedule_interval=presidio_core_dag.schedule_interval,
            start_date=presidio_core_dag.start_date,
            default_args=presidio_core_dag.default_args
        )

        return SubDagOperator(
            subdag=AnomalyDetectionEngineDagBuilder(data_sources, hourly_smart_events_confs,
                                                    daily_smart_events_confs).build(ade_dag),
            task_id=ade_dag_id,
            dag=presidio_core_dag
        )

    @staticmethod
    def _get_output_sub_dag_operator(data_sources, presidio_core_dag):
        output_dag_id = 'output_dag'

        output_dag = DAG(
            dag_id='{}.{}'.format(presidio_core_dag.dag_id, output_dag_id),
            schedule_interval=presidio_core_dag.schedule_interval,
            start_date=presidio_core_dag.start_date,
            default_args=presidio_core_dag.default_args
        )

        return SubDagOperator(
            subdag=OutputDagBuilder(data_sources).build(output_dag),
            task_id=output_dag_id,
            dag=presidio_core_dag
        )
