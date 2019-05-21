from presidio.operators.output.abstract_output_operator import AbstractOutputOperator
from presidio.utils.airflow.operators.spring_boot_jar_operator import SpringBootJarOperator
from presidio.utils.services.string_service import is_blank


class OutputOperator(AbstractOutputOperator):
    """
    Runs an output task (a JAR file) using a bash command.
    The c'tor accepts the task arguments that are constant throughout the
    operator runs (e.g. the fixed duration strategy and the schema).
    Other arguments, such as the start date and the end date, are evaluated before every run.
    """

    def get_task_name(self):
        """
        :return: The task name
        """
        return 'hourly_output_processor'

    def get_retry_args(self, bash_command, command):
        if not is_blank(self.java_args):
            java_args = self.java_args
            java_args.pop('smart_record_conf_name')
            java_args.update({'entity_type': self.entity_type})
            java_args_str = ' '.join(SpringBootJarOperator.java_args_prefix + '%s %s' % (key, val) for (key, val) in
                        java_args.iteritems())
            bash_command.append(command)
            bash_command.append(java_args_str)

