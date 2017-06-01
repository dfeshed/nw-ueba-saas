from airflow.operators.python_operator import ShortCircuitOperator

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.services.fixed_duration_strategy import is_execution_date_valid



class PresidioTaskSensorService(TaskSensorService):


    def add_task_short_circuit_fixed_duration_operator(self, task, fixed_duration_strategy, schedule_interval):
        task_id = '%s_%s' % (task.task_id, 'short_circuit')
        short_circuit_operator = ShortCircuitOperator(
            task_id=task_id,
            python_callable=lambda **kwargs: is_execution_date_valid(kwargs['execution_date'],
                                                                                fixed_duration_strategy,
                                                                                schedule_interval),
            provide_context=True
        )
        short_circuit_operator.set_downstream(task)


