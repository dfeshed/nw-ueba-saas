from datetime import timedelta

from airflow import AirflowException
from airflow.models import BaseOperator
from airflow.operators.dummy_operator import DummyOperator
from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.operators.wiring.wiring_utils import WiringUtils


class ContainerOperator(BaseOperator):
    """
    ContainerOperator contains start_operator and end_operator.
    """

    @apply_defaults
    def __init__(self, builder, dag, container_operator_id, retry_args, add_sequential_sensor, short_circuit_operator, *args, **kwargs):
        super(ContainerOperator, self).__init__(*args, **kwargs)

        old_tasks = dag.tasks
        builder.build(dag)
        new_tasks = [item for item in dag.tasks if item not in old_tasks]

        first_tasks = [task for task in new_tasks if not task.upstream_list and not isinstance(task, ContainerOperator)]
        last_tasks = [task for task in new_tasks if not task.downstream_list and not isinstance(task, ContainerOperator)]

        task_sensor_service = TaskSensorService()
        if add_sequential_sensor:
            first_tasks = WiringUtils.add_sensor(new_tasks, task_sensor_service)
        if short_circuit_operator:
            first_tasks = WiringUtils.add_short_circuit(short_circuit_operator, first_tasks, task_sensor_service)

        start_task_id = '{}.{}'.format("start_operator", container_operator_id)
        self.start_operator = self._create_dummy_operators(dag, start_task_id, retry_args)

        end_task_id = '{}.{}'.format("end_operator", container_operator_id)
        self.end_operator = self._create_dummy_operators(dag, end_task_id, retry_args)

        self.start_operator >> first_tasks
        self.end_operator << last_tasks

    def execute(self):
        pass

    def _set_relatives(self, task_or_task_list, upstream=False):
        """
        This method invoke if container operator call ">> | << | set_downstream | set_upstream" to another operator.

        :param task_or_task_list:
        :param upstream:
        :return:
        """
        if upstream:
            self.start_operator.set_upstream(task_or_task_list)
        else:
            self.end_operator.set_downstream(task_or_task_list)

    def append_only_new(self, upstream_or_downstream_list, task_id):
        """
         This method invoke if operator call ">> | << | set_downstream | set_upstream" to container operator.

        :param upstream_or_downstream_list: downstream_task_ids or upstream_task_ids list
        :param task_id: task_id
        :return:
        """

        # find task by task_id
        if self.dag.has_task(task_id):
            task = self.dag.get_task(task_id)

            # if upstream:
            # find upstream_task_ids list of container.start_operator.
            # add container.start_operator.task_id to task.downstream_task_ids
            if upstream_or_downstream_list is self.upstream_task_ids:
                task_list_to_append_to = self.start_operator.upstream_task_ids
                task.append_only_new(task.downstream_task_ids, self.start_operator.task_id)

            # if downstream:
            # find downstream_task_ids list of container.end_operator.
            # add container.end_operator.task_id to task.upstream_task_ids
            elif upstream_or_downstream_list is self.downstream_task_ids:
                task_list_to_append_to = self.end_operator.downstream_task_ids
                task.append_only_new(task.upstream_task_ids, self.end_operator.task_id)

            # add task_id to upstream | downstream list of container start/end operator.
            super(ContainerOperator, self).append_only_new(task_list_to_append_to, task_id)
        else:
            raise AirflowException(
                'The {} dag should contain {} task_id'
                ''.format(self.dag.dag_id, task_id))

    def _create_dummy_operators(self, dag, task_id, retry_args):
        return DummyOperator(dag=dag, task_id=task_id, retries=retry_args['retries'],
                                       retry_delay=timedelta(seconds=int(retry_args['retry_delay'])),
                                       retry_exponential_backoff=retry_args['retry_exponential_backoff'],
                                       max_retry_delay=timedelta(
                                           seconds=int(retry_args['max_retry_delay'])))
