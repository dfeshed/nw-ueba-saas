
from airflow import AirflowException
from airflow.models import BaseOperator
from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.airflow.operators.wiring.wiring_utils import WiringUtils


class WireOperator(BaseOperator):
    """
    WireOperator contains first tasks and last tasks
    """

    @apply_defaults
    def __init__(self, builder, dag, add_sequential_sensor, short_circuit_operator, *args, **kwargs):
        super(WireOperator, self).__init__(*args, **kwargs)

        old_tasks = dag.tasks
        builder.build(dag)
        new_tasks = [item for item in dag.tasks if item not in old_tasks]

        self.first_tasks = [task for task in new_tasks if not task.upstream_list and not isinstance(task, WireOperator)]
        self.last_tasks = [task for task in new_tasks if not task.downstream_list and not isinstance(task, WireOperator)]

        task_sensor_service = TaskSensorService()
        if add_sequential_sensor:
            self.first_tasks = WiringUtils.add_sensor(new_tasks, task_sensor_service)
        if short_circuit_operator:
            self.first_tasks = WiringUtils.add_short_circuit(short_circuit_operator, self.first_tasks, task_sensor_service)

    def execute(self):
        pass

    def _set_relatives(self, task_or_task_list, upstream=False):
        """
        This method invoke if wire_operator call ">> | << | set_downstream | set_upstream" to another operator.

        :param task_or_task_list:
        :param upstream:
        :return:
        """
        if upstream:
            for task in self.first_tasks:
                task.set_upstream(task_or_task_list)
        else:
            for task in self.last_tasks:
                task.set_downstream(task_or_task_list)

    def append_only_new(self, upstream_or_downstream_list, task_id):
        """
         This method invoke if operator call ">> | << | set_downstream | set_upstream" to wire_operator.

        :param upstream_or_downstream_list: downstream_task_ids or upstream_task_ids list
        :param task_id: task_id
        :return:
        """

        # find task by task_id
        if self.dag.has_task(task_id):
            task = self.dag.get_task(task_id)

            # if upstream:
            # append task_id to first_task.upstream_task_ids
            # append first_task.task_id totask.downstream_task_ids
            if upstream_or_downstream_list is self.upstream_task_ids:
                for first_task in self.first_tasks:
                    super(WireOperator, self).append_only_new(first_task.upstream_task_ids, task_id)
                    task.append_only_new(task.downstream_task_ids, first_task.task_id)

            # if downstream:
            # append task_id to last_task.downstream_task_ids
            # append last_task.task_id to task.upstream_task_ids
            elif upstream_or_downstream_list is self.downstream_task_ids:
                for last_task in self.last_tasks:
                    super(WireOperator, self).append_only_new(last_task.downstream_task_ids, task_id)
                    task.append_only_new(task.upstream_task_ids, last_task.task_id)
        else:
            raise AirflowException(
                'The {} dag should contain {} task_id'
                ''.format(self.dag.dag_id, task_id))

