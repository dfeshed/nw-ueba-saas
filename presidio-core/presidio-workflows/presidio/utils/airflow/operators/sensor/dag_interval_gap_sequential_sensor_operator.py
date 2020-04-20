import pytz

from presidio.utils.airflow.operators.sensor.root_dag_gap_sequential_sensor_operator import \
    RootDagGapSequentialSensorOperator
from presidio.utils.services.time_service import floor_time


class DagIntervalGapSequentialSensorOperator(RootDagGapSequentialSensorOperator):
    """
    Ensure that dag instance are running sequentially after all the specified dag_ids instances finished running in specific time.
    The sensor checks every poked interval if all the previous dag_ids instances ran already
    (reached to one of the following states: success, failed).
    (The time calculation: floor execution_date according to fixed_duration_strategy and subtract the interval.

    :param dag_ids: The dag_ids that you want to wait for
    :type dag_ids: list
    :param interval: The period of time you want to subtract from execution_date
    :type interval: timedelta
    :param start_time: The machine start_time
    :type start_time: datetime
    :param fixed_duration_strategy: duration (e.g. hourly or daily)
    :type fixed_duration_strategy: timedelta

    """
    ui_color = '#19647e'
    ui_fgcolor = '#fff'

    def __init__(
            self,
            dag_ids,
            interval,
            start_time,
            fixed_duration_strategy,
            *args, **kwargs):
        super(DagIntervalGapSequentialSensorOperator, self).__init__(
            dag_ids=dag_ids,
            start_time=start_time,
            *args,
            **kwargs
        )

        self.interval = interval
        self.fixed_duration_strategy = fixed_duration_strategy

    def poke(self, context):
        '''
        @return: bool - whether there are tasks to wait for.
        '''

        execution_date = context['execution_date']
        execution_date = floor_time(execution_date, time_delta=self.fixed_duration_strategy) - self.interval
        execution_date = execution_date.replace(tzinfo=pytz.utc)

        self.log.info(
            'Poking for all dag instances of '
            '{self._dag_ids} with time '
            'execution_date ... '.format(**locals()))

        return self._is_finished_wait_for_gapped_dag(execution_date)
