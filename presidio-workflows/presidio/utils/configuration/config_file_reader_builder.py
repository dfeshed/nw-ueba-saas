from ConfigParser import SafeConfigParser

import pkg_resources

from presidio.utils.configuration.config_file_reader import ConfigFileConfigurationReader


class ConfigFileConfigurationReaderBuilder():
    def build(self):
        parser = SafeConfigParser()
        file_path = pkg_resources.resource_filename('presidio',
                                                    'resources/java/config.ini')
        parser.read(file_path)
        config_properties = dict(parser.items('config_server'))
        return ConfigFileConfigurationReader(app_name=config_properties["application_name"],
                                             profile=config_properties["profile"],
                                             path=config_properties["config_path"])
