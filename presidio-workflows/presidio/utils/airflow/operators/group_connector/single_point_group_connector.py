from datetime import timedelta

from airflow.operators.dummy_operator import DummyOperator
from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.group_connector.multi_point_group_connector import MultiPointGroupConnector


class SinglePointGroupConnector(MultiPointGroupConnector):
    """
    SinglePointGroupConnector contains start_operator as a first_tasks and end_operator as a last_tasks.
    """

    @apply_defaults
    def __init__(self, dag, single_point_group_connector_id, retry_args, *args, **kwargs):
        super(SinglePointGroupConnector, self).__init__(dag=dag, *args, **kwargs)

        start_task_id = '{}.{}'.format("start_operator", single_point_group_connector_id)
        start_operator = self._create_dummy_operators(dag, start_task_id, retry_args)

        end_task_id = '{}.{}'.format("end_operator", single_point_group_connector_id)
        end_operator = self._create_dummy_operators(dag, end_task_id, retry_args)

        start_operator >> self._first_tasks
        end_operator << self._last_tasks

        self._first_tasks = [start_operator]
        self._last_tasks = [end_operator]

    def _create_dummy_operators(self, dag, task_id, retry_args):
        return DummyOperator(dag=dag, task_id=task_id, retries=retry_args['retries'],
                             retry_delay=timedelta(seconds=int(retry_args['retry_delay'])),
                             retry_exponential_backoff=retry_args['retry_exponential_backoff'],
                             max_retry_delay=timedelta(
                                 seconds=int(retry_args['max_retry_delay'])))
