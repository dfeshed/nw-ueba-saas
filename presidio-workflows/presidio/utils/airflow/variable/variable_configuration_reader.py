from airflow.models import Variable

from presidio.utils.airflow.configuration.abstract_configuration_reader import AbstractConfigurationReader


class VariableReader(AbstractConfigurationReader):
    """
    reads airflow variable, contains a default value in case the airflow value does not exist
    """

    def __init__(self, default_value_file_path, var_key):
        super(VariableReader, self).__init__()
        self.var_key = var_key
        self.default_value_file_path = default_value_file_path

    def read_from_store(self, conf_key):
        """
        :return: Try to get the value of the variable "variable_scheduler_dag_params" 
        from airflow's variables key-value store
        if not exist, default value is returned 
        """
        default_value_from_file = \
            self.read_default_value_from_file(default_value_file_path=self.default_value_file_path)
        variable_value = Variable.get(key=self.var_key, default_var=default_value_from_file, deserialize_json=True)
        return variable_value.get(conf_key)
