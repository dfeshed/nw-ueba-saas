import json
import logging

import os

from presidio.utils.airflow.configuration.abstract_configuration_reader import AbstractConfigurationReader

class ConfigFileConfigurationReader(AbstractConfigurationReader):
    """
    reads properties from configuration server
    """

    def __init__(self, app_name, profile, path):
        """

        :param app_name: name of application that we want to read the configuration for. i.e. "input-core" 
        :param profile: configuration profile. i.e.: dev,prod etc...
        :param path: path of conf file directory
        """
        super(ConfigFileConfigurationReader, self).__init__()
        self.app_name = app_name
        self.profile = profile
        self.path = path
        self.properties = None

    def _get_application_properties(self):
        """

        :return: json containing the configuration for application,profile 
        """
        conf_file_path = "%s/%s-%s.%s" % (self.path,self.app_name,self.profile, "json")
        logging.info("reading conf from path: %s",conf_file_path)
        if not os.path.exists(conf_file_path):
            raise "configuration file %s is missing" % conf_file_path
        with open(conf_file_path) as conf_file:
            config_properties = json.load(conf_file)
        return config_properties

    def read_from_store(self, conf_key):
        """
             reads configuration from config file
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
        setter to be used in unit test in order to prevent config  usage 
        :param properties: 
        """
        logging.info("config reader properties are set manually. you must be in testing it...")
        self.properties = properties
