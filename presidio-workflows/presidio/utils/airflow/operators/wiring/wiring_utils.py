

class WiringUtils:

    def __init__(self):
        pass

    @staticmethod
    def add_sensor(tasks, task_sensor_service):
        """
        Add sensor to tasks
        :param tasks: tasks
        :param task_sensor_service: task_sensor_service
        :return:
        """
        sensors = []
        for task in tasks:
            upstream_without_sensor = task.upstream_list
            task_sensor_service.add_task_sequential_sensor(task)
            upstream_with_sensor = task.upstream_list
            sensor = [task for task in upstream_with_sensor if not upstream_without_sensor]
            sensors.extend(sensor)
        return sensors

    @staticmethod
    def add_short_circuit(short_circuit_operator, tasks, task_sensor_service):
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

