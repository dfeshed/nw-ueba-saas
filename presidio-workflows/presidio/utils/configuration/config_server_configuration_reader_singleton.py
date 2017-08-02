from presidio.utils.configuration.config_server_reader_basic_auth_builder import \
    ConfigServerConfigurationReaderBasicAuthBuilder
from presidio.utils.services.singleton import Singleton


class ConfigServerConfigurationReaderSingleton(Singleton):
    def init(self, *args, **kwds):
        self._config_server_reader = ConfigServerConfigurationReaderBasicAuthBuilder().build()

    @property
    def config_server_reader(self):
        return self._config_server_reader