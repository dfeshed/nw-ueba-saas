from airflow import AirflowException
from airflow.models import BaseOperator, DAG
from airflow.utils.decorators import apply_defaults


class ContainerOperator(BaseOperator):

    @apply_defaults
    def __init__(self, start_operator, end_operator, *args, **kwargs):
        super(ContainerOperator, self).__init__(*args, **kwargs)
        self.start_operator = start_operator
        self.end_operator = end_operator

    def execute(self, context):
        pass

    def _set_relatives(self, task_or_task_list, upstream=False):
        """
        This method invoke if container operator call ">> | << | set_downstream | set_upstream" to operator.

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

            # append task_id to the container list in order to realize if it is upstream or downstream list
            super(ContainerOperator, self).append_only_new(upstream_or_downstream_list, task_id)

            # if upstream
            # find upstream_task_ids list of container.start_operator.
            # remove task_id from container upstream_task_ids list.
            # add container.start_operator.task_id to task.downstream_task_ids
            if task_id in self.upstream_task_ids:
                array = self.start_operator.upstream_task_ids
                self._upstream_task_ids.remove(task_id)
                task.append_only_new(task.downstream_task_ids, self.start_operator.task_id)

            # if downstream:
            # find downstream_task_ids list of end_operator.
            # remove task_id from container downstream_task_ids list.
            # add container.end_operator.task_id to task.upstream_task_ids
            elif task_id in self.downstream_task_ids:
                array = self.end_operator.downstream_task_ids
                self.downstream_task_ids.remove(task_id)
                task.append_only_new(task.upstream_task_ids, self.end_operator.task_id)

            # add task_id to container upstream | downstream list
            super(ContainerOperator, self).append_only_new(array, task_id)
        else:
            raise AirflowException(
                'The {} dag should contain {} task_id {}'
                ''.format(self.dag.dag_id, task_id))
