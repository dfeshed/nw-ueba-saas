from airflow.utils.log.logging_mixin import LoggingMixin

from presidio.utils.airflow.operators.sensor.task_sensor_service import TaskSensorService
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class PresidioDagWiring(LoggingMixin):
    conf_reader = ConfigServerConfigurationReaderSingleton().config_reader

    def __init__(self):
        super(PresidioDagWiring, self).__init__()
        self.first_tasks = []
        self.last_tasks = []
        self.task_sensor_service = TaskSensorService()

    def wire(self, builder, dag, short_circuit_operator, upstream_tasks, downstream_tasks, add_sequential_sensor,
             filter_operator=None):
        """
        Build dag and wire short_circuit_operator, sequential_sensor, upstream_tasks and downstream_tasks
        :param dag: dag
        :param add_sequential_sensor: boolean
        :param short_circuit_operator: short_circuit_operator
        :param filter_operator: filter_operator
        :param downstream_tasks:
        :param upstream_tasks:
        :param builder: builder
        """

        old_tasks = dag.tasks
        builder.build(dag)
        new_tasks = [item for item in dag.tasks if item not in old_tasks]
        self._find_first_tasks(filter_operator, new_tasks)
        self._find_last_tasks(filter_operator, new_tasks)
        self._wire(add_sequential_sensor, short_circuit_operator, upstream_tasks, downstream_tasks, filter_operator)

    def wire_operator(self, operator, short_circuit_operator, upstream_tasks, downstream_tasks, add_sequential_sensor):
        """
        Receives a operator and wire short_circuit_operator, sequential_sensor, upstream_tasks and downstream_tasks
        :param operator: operator
        :param short_circuit_operator: short_circuit_operator
        :param downstream_tasks: downstream_tasks
        :param upstream_tasks: upstream_tasks
        :param add_sequential_sensor: add_sequential_sensor
        """

        self.first_tasks = [operator]
        self.last_tasks = [operator]
        self._wire(add_sequential_sensor, short_circuit_operator, upstream_tasks, downstream_tasks, None)

    def _wire(self, add_sequential_sensor, short_circuit_operator, upstream_tasks, downstream_tasks, filter_operator):
        """
        wire short_circuit_operator, sequential_sensor, upstream_tasks and downstream_tasks
        and update the first and last tasks.
        :param add_sequential_sensor: add_sequential_sensor
        :param short_circuit_operator: short_circuit_operator
        :param upstream_tasks: upstream_tasks
        :param downstream_tasks: downstream_tasks
        :param filter_operator: filter_operator
        :return:
        """
        first_tasks_to_add = []
        last_tasks_to_add = []

        if add_sequential_sensor:
            for first_new_task in self.first_tasks:
                upstream_tasks_without_sensor = first_new_task.upstream_list
                self.task_sensor_service.add_task_sequential_sensor(first_new_task)
                upstream_tasks_with_sensor = first_new_task.upstream_list
                sensor = [item for item in upstream_tasks_with_sensor if item not in upstream_tasks_without_sensor]
                first_tasks_to_add.extend(sensor)

        if short_circuit_operator is not None:
            first_tasks_to_add.append(short_circuit_operator)
            for first_new_task in self.first_tasks:
                self.task_sensor_service.add_task_short_circuit(first_new_task, short_circuit_operator)

        if upstream_tasks:
            for upstream_task in upstream_tasks:
                upstream_task >> self.first_tasks
            first_tasks_to_add.extend(upstream_tasks)

        if downstream_tasks:
            for downstream_task in downstream_tasks:
                self.last_tasks >> downstream_task
            last_tasks_to_add.extend(downstream_tasks)

        if first_tasks_to_add:
            self._find_first_tasks(filter_operator, first_tasks_to_add)
        if last_tasks_to_add:
            self._find_last_tasks(filter_operator, last_tasks_to_add)

    def _find_first_tasks(self, filter_operator, tasks):
        self.first_tasks = [task for task in tasks if not task.upstream_list]
        if filter_operator is not None:
            self.first_tasks = [first_task for first_task in self.first_tasks if
                                not isinstance(first_task, filter_operator)]

    def _find_last_tasks(self, filter_operator, tasks):
        self.last_tasks = [task for task in tasks if not task.downstream_list]
        if filter_operator is not None:
            self.last_tasks = [last_task for last_task in self.last_tasks if not isinstance(last_task, filter_operator)]
