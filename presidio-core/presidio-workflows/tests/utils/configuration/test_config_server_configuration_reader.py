import json

import pkg_resources

from presidio.utils.configuration.config_server_configuration_reader import ConfigServerConfigurationReader


def test_read_existing_configuration():
    conf_reader = ConfigServerConfigurationReader(app_name=None, profile=None, config_server=None, auth=None)
    _read_conf_file(conf_reader)
    conf_value = conf_reader.read(conf_key="aaa.bbb")
    expected_value = u'#dot#'
    assert expected_value == conf_value


def test_read_non_existing_configuration():
    conf_reader = ConfigServerConfigurationReader(app_name=None, profile=None, config_server=None, auth=None)
    _read_conf_file(conf_reader)
    conf_value = conf_reader.read(conf_key="best conf key in the world!!!")
    expected_value = None
    assert expected_value == conf_value


def _read_conf_file(conf_reader):
    file_path = pkg_resources.resource_filename('tests',
                                                'resources/utils/configuration/conf_server_dummy_response.json')
    with open(file_path) as data_file:
        conf_reader.set_properties(properties=json.load(data_file))
