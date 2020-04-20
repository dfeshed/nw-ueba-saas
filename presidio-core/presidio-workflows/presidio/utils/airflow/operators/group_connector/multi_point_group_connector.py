from airflow import AirflowException
from airflow.models import BaseOperator
from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService


class MultiPointGroupConnector(BaseOperator):
    """
    MultiPointGroupConnector contains first tasks and last tasks
    """

    @apply_defaults
    def __init__(self, builder, dag, add_sequential_sensor, short_circuit_operator, *args, **kwargs):
        super(MultiPointGroupConnector, self).__init__(dag=dag, *args, **kwargs)

        old_tasks = dag.tasks
        builder.build(dag)
        new_tasks = [item for item in dag.tasks if item not in old_tasks]

        self._first_tasks = [task for task in new_tasks if
                             not task.upstream_list and not isinstance(task, MultiPointGroupConnector)]
        self._last_tasks = [task for task in new_tasks if
                            not task.downstream_list and not isinstance(task, MultiPointGroupConnector)]

        task_sensor_service = TaskSensorService()
        if add_sequential_sensor:
            self._first_tasks = self.add_sensor(new_tasks, task_sensor_service)
        if short_circuit_operator:
            self._first_tasks = self.add_short_circuit(short_circuit_operator, self._first_tasks, task_sensor_service)

    def execute(self):
        pass

    def _set_relatives(self, task_or_task_list, upstream=False):
        """
        This method invoke if MultiPointGroupConnector call ">> | << | set_downstream | set_upstream" to another operator.

        :param task_or_task_list:
        :param upstream:
        :return:
        """
        if upstream:
            for task in self._first_tasks:
                task.set_upstream(task_or_task_list)
        else:
            for task in self._last_tasks:
                task.set_downstream(task_or_task_list)

    def add_only_new(self, upstream_or_downstream_list, task_id):
        """
         This method invoke if operator call ">> | << | set_downstream | set_upstream" to MultiPointGroupConnector.

        :param upstream_or_downstream_list: downstream_task_ids or upstream_task_ids list
        :param task_id: task_id
        :return:
        """

        # find task by task_id
        if self.dag.has_task(task_id):
            task = self.dag.get_task(task_id)

            # if upstream:
            # add task_id to first_task.upstream_task_ids
            # add first_task.task_id to task.downstream_task_ids
            if upstream_or_downstream_list is self.upstream_task_ids:
                for first_task in self._first_tasks:
                    super(MultiPointGroupConnector, self).add_only_new(first_task.upstream_task_ids, task_id)
                    task.add_only_new(task.downstream_task_ids, first_task.task_id)

            # if downstream:
            # add task_id to last_task.downstream_task_ids
            # add last_task.task_id to task.upstream_task_ids
            elif upstream_or_downstream_list is self.downstream_task_ids:
                for last_task in self._last_tasks:
                    super(MultiPointGroupConnector, self).add_only_new(last_task.downstream_task_ids, task_id)
                    task.add_only_new(task.upstream_task_ids, last_task.task_id)
        else:
            raise AirflowException(
                'The {} dag should contain {} task_id'
                ''.format(self.dag.dag_id, task_id))

    def add_sensor(self, tasks, task_sensor_service):
        """
        Add sensor to tasks
        :param tasks: tasks
        :param task_sensor_service: task_sensor_service
        :return:
        """
        sensors = []
        for task in tasks:
            sensor = task_sensor_service.add_task_sequential_sensor(task)
            sensors.append(sensor)
        return sensors

    def add_short_circuit(self, short_circuit_operator, tasks, task_sensor_service):
        """
        Add short_circuit_operator to tasks
        :param short_circuit_operator: short_circuit_operator
        :param tasks: tasks
        :param task_sensor_service: task_sensor_service
        :return:
        """
        for task in tasks:
            task_sensor_service.add_task_short_circuit(task, short_circuit_operator)
        return [short_circuit_operator]
