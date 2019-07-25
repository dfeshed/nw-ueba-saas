from airflow.utils.decorators import apply_defaults

from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator


class InputPreProcessor(SpringBootJarOperator):

    @apply_defaults
    def __init__(self, name, arguments , command, task_id=None, *args, **kwargs):
        self.task_id = task_id or 'input_pre_processor{}'.format(self.name)

        java_args = {
            'name': name,
            'arguments': arguments
        }

        super(InputPreProcessor, self).__init__(command=command,
                                                java_args=java_args, task_id=self.task_id(), *args, **kwargs)

