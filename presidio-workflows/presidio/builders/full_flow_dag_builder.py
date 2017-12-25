
import logging
from datetime import timedelta

from presidio.builders.adapter.adapter_dag_builder import AdapterDagBuilder
from presidio.builders.core.presidio_core_dag_builder import PresidioCoreDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention import RetentionDagBuilder
from presidio.utils.airflow.operators.sensor.root_dag_gap_sensor_operator import RootDagGapSensorOperator
from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


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
        logging.debug("populating the full flow dag, dag_id=%s for data sources:%s ", full_flow_dag.dag_id, data_sources)

        task_sensor_service = TaskSensorService()

        root_dag_gap_sensor_operator = RootDagGapSensorOperator(dag=full_flow_dag, task_id='full_flow_gap_sensor', external_dag_id=full_flow_dag.dag_id,
                                       execution_delta=timedelta(days=1),
                                       poke_interval=5)

        adapter_sub_dag = self._get_adapter_sub_dag_operator(data_sources, full_flow_dag)
        task_sensor_service.add_task_sequential_sensor(adapter_sub_dag)
        presidio_core_sub_dag = self._get_presidio_core_sub_dag_operator(data_sources, full_flow_dag)

        retention_sub_dag = self._get_presidio_retention_sub_dag_operator(data_sources, full_flow_dag)

        root_dag_gap_sensor_operator >> adapter_sub_dag >> presidio_core_sub_dag >> retention_sub_dag
        logging.debug("Finished creating dag - %s", full_flow_dag.dag_id)

        return full_flow_dag

    def _get_adapter_sub_dag_operator(self, data_sources, full_flow_dag):
        adapter_dag_id = 'adapter_dag'

        return self._create_sub_dag_operator(AdapterDagBuilder(data_sources), adapter_dag_id, full_flow_dag)

    def _get_presidio_core_sub_dag_operator(self, data_sources, full_flow_dag):
        presidio_core_dag_id = 'presidio_core_dag'

        return self._create_sub_dag_operator(PresidioCoreDagBuilder(data_sources), presidio_core_dag_id, full_flow_dag)

    def _get_presidio_retention_sub_dag_operator(self, data_sources, full_flow_dag):
        retention_dag_id = 'retention_dag'

        return self._create_sub_dag_operator(RetentionDagBuilder(data_sources), retention_dag_id, full_flow_dag)



