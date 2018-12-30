from datetime import timedelta

from presidio.builders.adapter.adapter_dag_builder import AdapterDagBuilder
from presidio.builders.core.presidio_core_dag_builder import PresidioCoreDagBuilder
from presidio.builders.presidio_dag_builder import PresidioDagBuilder
from presidio.builders.retention.retention_dag_builder import RetentionDagBuilder
from presidio.utils.airflow.operators.wiring.container_operator import ContainerOperator
from presidio.utils.airflow.operators.wiring.wire_operator import WireOperator
from presidio.utils.airflow.operators.sensor.root_dag_gap_sensor_operator import RootDagGapSensorOperator


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
        self.log.debug("populating the full flow dag, dag_id=%s for data sources:%s ", full_flow_dag.dag_id,
                       data_sources)

        root_dag_gap_sensor_operator = RootDagGapSensorOperator(dag=full_flow_dag, task_id='full_flow_gap_sensor',
                                                                external_dag_id=full_flow_dag.dag_id,
                                                                execution_delta=timedelta(days=1),
                                                                poke_interval=5)

        adapter_sub_dag = self._get_adapter_container_operator(data_sources, full_flow_dag)

        presidio_core_sub_dag = self._get_presidio_core_container_operator(data_sources, full_flow_dag)

        retention_sub_dag = self._get_presidio_retention_container_operator(data_sources, full_flow_dag)

        root_dag_gap_sensor_operator >> adapter_sub_dag >> presidio_core_sub_dag >> retention_sub_dag
        self.log.debug("Finished creating dag - %s", full_flow_dag.dag_id)

        self.remove_relatives_of_container_operator(full_flow_dag)
        self.remove_container_operator_tasks(full_flow_dag)
        return full_flow_dag

    @staticmethod
    def remove_relatives_of_container_operator(full_flow_dag):
        """
         Remove ContainerOperator from downstream and upstream lists of other tasks
        :param full_flow_dag:
        :return:
        """
        tasks = full_flow_dag.tasks
        for task in tasks:
            if not isinstance(task, ContainerOperator) and not isinstance(task, WireOperator):
                for t in task.downstream_list:
                    if isinstance(t, ContainerOperator) or isinstance(task, WireOperator):
                        task.downstream_task_ids.remove(t.task_id)
                for t in task.upstream_task_ids:
                    if isinstance(t, ContainerOperator) or isinstance(task, WireOperator):
                        task.upstream_task_ids.remove(t.task_id)

    @staticmethod
    def remove_container_operator_tasks(full_flow_dag):
        """
         Remove ContainerOperator tasks
        :param full_flow_dag:
        :return:
        """
        dicts = full_flow_dag.task_dict
        for task_id, task in dicts.items():
            if isinstance(task, ContainerOperator) or isinstance(task, WireOperator):
                dicts.pop(task_id)

    def _get_adapter_container_operator(self, data_sources, full_flow_dag):
        adapter_dag_id = 'adapter_dag'
        return self._create_container_operator(AdapterDagBuilder(data_sources), adapter_dag_id, full_flow_dag, None, False)

    def _get_presidio_core_container_operator(self, data_sources, full_flow_dag):
        presidio_core_dag_id = 'presidio_core_dag'

        return self._create_container_operator(PresidioCoreDagBuilder(data_sources), presidio_core_dag_id, full_flow_dag, None, False)

    def _get_presidio_retention_container_operator(self, data_sources, full_flow_dag):
        retention_dag_id = 'retention_dag'

        return self._create_container_operator(RetentionDagBuilder(data_sources), retention_dag_id, full_flow_dag, None, False)
