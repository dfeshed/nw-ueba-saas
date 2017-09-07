from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.adapter.adapter_dag_builder import AdapterDagBuilder
from presidio.builders.presidio_core_dag_builder import PresidioCoreDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService

import logging


class FullFlowDagBuilder(PresidioDagBuilder):
    """
    The "full flow rum" DAG consists of all the presidio flow including the adapter
    """

    def build(self, full_flow_dag):
        """
        Receives a full flow DAG, creates the operators (adapter and presidio core), links them to the DAG and
        configures the dependencies between them.
        :param full_flow_dag: The full flow DAG to populate
        :type full_flow_dag: airflow.models.DAG
        :return: The given full flow DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        default_args = full_flow_dag.default_args
        data_sources = [item.strip() for item in default_args.get("data_sources").split(',')]
        logging.info("populating the full flow dag, dag_id=%s for data sources:%s ", full_flow_dag.dag_id, data_sources)

        task_sensor_service = TaskSensorService()

        adapter_sub_dag = self._get_adapter_sub_dag_operator(data_sources, full_flow_dag)
        task_sensor_service.add_task_sequential_sensor(adapter_sub_dag)
        presidio_core_sub_dag = self._get_presidio_core_sub_dag_operator(data_sources, full_flow_dag)

        adapter_sub_dag >> presidio_core_sub_dag
        logging.info("Finished creating dag - %s", full_flow_dag.dag_id)

        return full_flow_dag

    @staticmethod
    def _get_adapter_sub_dag_operator(data_sources, full_flow_dag):
        adapter_dag_id = 'adapter_dag'

        adapter_dag = DAG(
            dag_id='{}.{}'.format(full_flow_dag.dag_id, adapter_dag_id),
            schedule_interval=full_flow_dag.schedule_interval,
            start_date=full_flow_dag.start_date,
            default_args=full_flow_dag.default_args
        )

        return SubDagOperator(
            subdag=AdapterDagBuilder(data_sources).build(adapter_dag),
            task_id=adapter_dag_id,
            dag=full_flow_dag
        )

    @staticmethod
    def _get_presidio_core_sub_dag_operator(data_sources, full_flow_dag):
        presidio_core_dag_id = 'presidio_core_dag'

        presidio_core_dag = DAG(
            dag_id='{}.{}'.format(full_flow_dag.dag_id, presidio_core_dag_id),
            schedule_interval=full_flow_dag.schedule_interval,
            start_date=full_flow_dag.start_date,
            default_args=full_flow_dag.default_args
        )

        return SubDagOperator(
            subdag=PresidioCoreDagBuilder(data_sources).build(presidio_core_dag),
            task_id=presidio_core_dag_id,
            dag=full_flow_dag
        )

