import json
import logging
from abc import ABCMeta, abstractmethod
from datetime import timedelta

from presidio.utils.airflow.configuration.configuration_exceptions import InvalidDefaultValueException, \
    DefaultConfFileNotValidException


class AbstractConfigurationReader:
    """
        abstract configuration reader (store agnostic)
    """
    __metaclass__ = ABCMeta

    def read(self, conf_key=None, default_value=None, default_value_file_path=None):
        """
        
        :param conf_key: key of the configuration to retrieve 
        :param default_value: the value to be returned in case the retrieved value is none
        :param default_value_file_path: default value can be stored as file (instead of object) 
        :raises InvalidDefaultValueException in case default_value and default_value_file_path are given. 
        only one of them should exist
        """
        if default_value is not None and default_value_file_path is not None:
            raise InvalidDefaultValueException(conf_key=conf_key, default_value=default_value,
                                               default_value_file_path=default_value_file_path)
        retrieved_value = self.read_from_store(conf_key)
        if retrieved_value is None:
            if default_value is not None:
                retrieved_value = default_value
            if default_value_file_path is not None:
                retrieved_value = self.read_default_value_from_file(
                    default_value_file_path=default_value_file_path).get(conf_key)
            logging.debug("retrieved value for conf_key=%s is None, retrieving default value instead. default value=%s",
                          conf_key, str(retrieved_value))
        return retrieved_value

    @abstractmethod
    def read_from_store(self, conf_key):
        """
        the db where overriding configuration can be stored may vary.
         it can be stored at mongodb, airflow variables or dozen other options. 
         each implementing configuration reader should give the ability to read from a specific store.
        :param conf_key: 
        """
        pass

    @staticmethod
    def read_default_value_from_file(default_value_file_path):
        """
        reads default configuration configured in resourced json
        :return: json dictionary containing default configuration value
        """
        try:
            with open(default_value_file_path) as conf_file:
                default_file_value = json.load(conf_file)
        except Exception as e:
            raise DefaultConfFileNotValidException(default_value_file_path=default_value_file_path, cause=e)
        return default_file_value

    def read_daily_timedelta(self, conf_key, default_value=None):
        return timedelta(days=self.read(conf_key=conf_key, default_value=default_value))