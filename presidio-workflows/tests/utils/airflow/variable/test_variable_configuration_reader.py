import pytest
from airflow.models import Variable

from presidio.utils.airflow.configuration.configuration_exceptions import DefaultConfFileDoesNotExists, \
    InvalidDefaultValue
from presidio.utils.airflow.variable.variable_configuration_reader import VariableReader
import pkg_resources


class TestVariableReader:
    file_path = pkg_resources.resource_filename('tests',
                                                'resources/utils/airflow/variable/default_test_conf.json')

    def test_exception_raised_if_no_default_file(self):
        var_key = "test_variable"
        Variable.set(key=var_key, value="{ }")
        variable_reader = VariableReader(var_key=var_key, default_value_file_path=None)
        with pytest.raises(DefaultConfFileDoesNotExists):
            variable_reader.read(conf_key="some_conf", default_value=None, default_value_file_path=None)

    def test_exception_raised_if_invalid_default_value(self):
        var_key = "test_variable1"
        Variable.set(key=var_key, value="{ }")
        variable_reader = VariableReader(var_key=var_key, default_value_file_path=self.file_path)
        with pytest.raises(InvalidDefaultValue):
            variable_reader.read(conf_key="some_conf", default_value="some default value",
                                 default_value_file_path=self.file_path)

    def test_should_return_default_conf_from_file(self):
        var_key = "test_variable2"
        variable_reader = VariableReader(var_key=var_key, default_value_file_path=self.file_path)
        actual_value = variable_reader.read_from_store(conf_key="foo")
        expected_value = {u'goo': 5, u'bar': u'a'}
        assert expected_value == actual_value
