import pytz
from airflow.models import DagRun
from airflow.operators.sensors import BaseSensorOperator
from airflow.utils.db import provide_session
from airflow.utils.state import State
from datetime import timedelta
from presidio.utils.services.time_service import floor_time


class RootDagGapSequentialSensorOperator(BaseSensorOperator):
    """
    Ensure that dag instance are running sequentially after all the specified dag_ids instances finished running.
    The sensor checks every poked interval if all the previous dag_ids instances ran already
    (reached to one of the following states: success, failed).
    Optional: if interval + start_time + fixed_duration_strategy was given, the sensor will check if all the
    previous dag_ids instances ran already in specific time. (The calculation: floor execution_date according
    to fixed_duration_strategy and subtract the interval.

    :param dag_ids: The dag_ids that you want to wait for
    :type dag_ids: list
    """
    ui_color = '#19647e'
    ui_fgcolor = '#fff'

    def __init__(
            self,
            dag_ids,
            interval=None,
            start_time=None,
            fixed_duration_strategy=None,
            *args, **kwargs):
        super(RootDagGapSequentialSensorOperator, self).__init__(
            retries=99999,
            retry_exponential_backoff=True,
            max_retry_delay=timedelta(seconds=300),
            retry_delay=timedelta(seconds=5),
            *args,
            **kwargs
        )

        self._dag_ids = dag_ids
        self.start_time = start_time
        self.interval = interval
        self.fixed_duration_strategy = fixed_duration_strategy

    def poke(self, context):
        '''
        @return: bool - whether there are tasks to wait for.
        '''

        execution_date = context['execution_date']

        self.log.info(
            'Poking for all dag instances of'
            '{self._dag_ids} with time '
            'execution_date ... '.format(**locals()))

        return self._is_finished_wait_for_gapped_dag(execution_date)

    @provide_session
    def _is_finished_wait_for_gapped_dag(self, execution_date, session=None):

        if self.interval and self.fixed_duration_strategy and self.start_time:
            execution_date = floor_time(execution_date, time_delta=self.fixed_duration_strategy)
            execution_date = execution_date - self.interval
            execution_date = execution_date.replace(tzinfo=pytz.utc)
            if execution_date < self.start_time:
                return True

        for dag_id in self._dag_ids:
            gapped_root_dag_run = session.query(DagRun).filter(
                DagRun.dag_id == dag_id,
                DagRun.execution_date == execution_date,
                DagRun.state.in_({State.SUCCESS, State.FAILED}),
            ).order_by(
                DagRun.execution_date.asc()
            ).first()

            if gapped_root_dag_run is None:
                return False

        return True
