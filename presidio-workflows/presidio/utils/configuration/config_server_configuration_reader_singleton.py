import pkg_resources
from ConfigParser import SafeConfigParser
from presidio.utils.configuration.config_file_reader import ConfigFileConfigurationReader
from presidio.utils.configuration.config_server_configuration_reader import ConfigServerConfigurationReader
from presidio.utils.services.singleton import Singleton
from requests.auth import HTTPBasicAuth


class ConfigServerConfigurationReaderSingleton(Singleton):
    def init(self, *args, **kwargs):
        safe_config_parser = SafeConfigParser()
        safe_config_parser.read(pkg_resources.resource_filename('presidio', 'resources/java/config.ini'))
        config_server_properties = dict(safe_config_parser.items('config_server'))

        # Instantiate a reader that reads from the (possibly remote) server
        # noinspection PyAttributeOutsideInit
        self._config_server_configuration_reader = ConfigServerConfigurationReader(
            app_name=config_server_properties["application_name"],
            profile=config_server_properties["profile"],
            config_server=config_server_properties["address"],
            auth=HTTPBasicAuth(config_server_properties["username"], config_server_properties["password"])
        )

        # Instantiate a reader that reads from the local file
        # noinspection PyAttributeOutsideInit
        self._config_file_configuration_reader = ConfigFileConfigurationReader(
            app_name=config_server_properties["application_name"],
            profile=config_server_properties["profile"],
            path=config_server_properties["config_path"]
        )

    @property
    def config_server_configuration_reader(self):
        return self._config_server_configuration_reader

    @property
    def config_file_configuration_reader(self):
        return self._config_file_configuration_reader

    @property
    # The default reader is the one that reads from the local file
    def config_reader(self):
        return self._config_file_configuration_reader
