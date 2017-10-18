import logging

import os
import requests
from requests import ConnectionError

from presidio.utils.airflow.configuration.abstract_configuration_reader import AbstractConfigurationReader

OUTPUT_FORMAT = "json"

SOURCE_KEY = "source"
PROPERTY_SOURCES_KEY = "propertySources"


class ConfigServerConfigurationReader(AbstractConfigurationReader):
    """
    reads properties from configuration server
    """

    def __init__(self, app_name, profile, config_server, auth):
        """

        :param app_name: name of application that we want to read the configuration for. i.e. "input-core" 
        :param profile: configuration profile. i.e.: dev,prod etc...
        :param config_server: address of the configuration server, i.e.: "http://localhost:8888"
        :param auth: authentication method and values of the conf server. i.e. HTTPBasicAuth('user', 'pass')
        """
        super(ConfigServerConfigurationReader, self).__init__()
        self.app_name = app_name
        self.profile = profile
        self.config_server = config_server
        self.auth = auth
        self.properties = None

    def _get_application_properties(self):
        """

        :return: json containing the configuration for application,profile 
        """
        request_path = "%s-%s.%s" % (self.app_name,self.profile, OUTPUT_FORMAT)

        config_server_address = str(os.sep).join((self.config_server, request_path))
        logging.debug("connecting to config server address: %s",config_server_address)
        try:
            config_server_properties = requests.get(config_server_address, auth=self.auth).json()
        except ConnectionError as e:
            logging.error(("failed to connect to config server=%s" % config_server_address), e)
            raise e

        return config_server_properties

    def read_from_store(self, conf_key):
        """
             reads configuration from spring cloud config server
            :param conf_key: property key to be searched
            :return: None if key not found, otherwise: first value answering the key
            """
        if not self.properties:
            self.properties = self._get_application_properties()
        value = self.properties
        for key in conf_key.split("."):
            if isinstance(value, dict):
                value = value.get(key)
            else:
                value = value
        return value

    def set_properties(self,properties):
        """
        setter to be used in unit test in order to prevent config server usage 
        :param properties: 
        """
        logging.debug("config reader properties are set manually. you must be in testing it...")
        self.properties = properties


