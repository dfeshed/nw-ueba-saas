from ConfigParser import SafeConfigParser

import pkg_resources
from requests.auth import HTTPBasicAuth

from presidio.utils.configuration.config_server_configuration_reader import ConfigServerConfigurationReader


class ConfigServerConfigurationReaderBasicAuthBuilder():
    def build(self):
        """
        :func: <presidio.utils.configuration.config_server_configuration_reader.ConfigServerConfigurationReader>
        :return: conf reader with basic authentication 
        """
        parser = SafeConfigParser()
        file_path = pkg_resources.resource_filename('presidio',
                                                    'resources/java/config.ini')
        parser.read(file_path)
        config_server_properties = dict(parser.items('config_server'))
        basic_auth = HTTPBasicAuth(config_server_properties["username"], config_server_properties["password"])
        return ConfigServerConfigurationReader(app_name=config_server_properties["application_name"],
                                               profile=config_server_properties["profile"],
                                               config_server=config_server_properties["address"],
                                               auth=basic_auth)
