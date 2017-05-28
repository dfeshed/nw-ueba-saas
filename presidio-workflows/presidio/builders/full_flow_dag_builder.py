from airflow import DAG
from airflow.operators.subdag_operator import SubDagOperator

from presidio.builders.collector.collector_dag_builder import CollectorDagBuilder
from presidio.builders.presidio_core_dag_builder import PresidioCoreDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder


class FullFlowDagBuilder(PresidioDagBuilder):
    """
    The "full flow rum" DAG consists of all the presidio flow including the collector
    """

    def build(self, full_flow_dag):
        """
        Receives a full flow DAG, creates the operators (collector and presidio core), links them to the DAG and 
        configures the dependencies between them.
        :param full_flow_dag: The full flow DAG to populate
        :type full_flow_dag: airflow.models.DAG
        :return: The given full flow DAG, after it has been populated
        :rtype: airflow.models.DAG
        """

        default_args = full_flow_dag.default_args
        data_sources = default_args.get("data_sources")

        collector_sub_dag = self._get_collector_sub_dag_operator(data_sources, full_flow_dag)
        presidio_core_sub_dag = self._get_presidio_core_sub_dag_operator(data_sources, full_flow_dag)

        collector_sub_dag >> presidio_core_sub_dag

        return full_flow_dag

    @staticmethod
    def _get_collector_sub_dag_operator(data_sources, full_flow_dag):
        collector_dag_id = 'collector_dag'

        collector_dag = DAG(
            dag_id='{}.{}'.format(full_flow_dag.dag_id, collector_dag_id),
            schedule_interval=full_flow_dag.schedule_interval,
            start_date=full_flow_dag.start_date,
            default_args=full_flow_dag.default_args
        )

        return SubDagOperator(
            subdag=CollectorDagBuilder(data_sources).build(collector_dag),
            task_id=collector_dag_id,
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
