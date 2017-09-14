from presidio.utils.configuration.config_file_reader_builder import ConfigFileConfigurationReaderBuilder
from presidio.utils.services.singleton import Singleton


class ConfigServerConfigurationReaderSingleton(Singleton):
    def init(self, *args, **kwds):
        self._config_reader = ConfigFileConfigurationReaderBuilder().build()

    @property
    def config_reader(self):
        return self._config_reader