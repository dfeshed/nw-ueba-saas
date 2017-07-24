import os
import requests

from presidio.utils.airflow.configuration.abstract_configuration_reader import AbstractConfigurationReader

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
        return requests.get(str(os.sep).join(self.config_server, self.app_name, self.profile), auth=self.auth).json()

    def read_from_store(self, conf_key):
        """
             reads configuration from spring cloud config server
            :param conf_key: property key to be searched
            :return: None if key not found, otherwise: first value answering the key
            """
        if not self.properties:
            self.properties = self._get_application_properties()
        property_sources = self.properties[PROPERTY_SOURCES_KEY]
        for property_source in property_sources:
            source = property_source[SOURCE_KEY]
            if source.has_key(conf_key):
                return source[conf_key]
        return None
