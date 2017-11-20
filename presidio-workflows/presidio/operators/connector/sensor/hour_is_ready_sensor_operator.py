import logging
import os
import time
import datetime
from airflow.operators.sensors import BaseSensorOperator
from presidio.utils.connector.properties_loader import load_and_get_property

LATEST_READY_HOUR_MARKER = "LATEST_READY_HOUR"


class HourIsReadySensorOperator(BaseSensorOperator):
    """
    Sensor count files until the latest ready hour is at least this hour start time and source count = sink count
    (will return true also when source count < sink count but this is not a valid state for the system)

    :param schema_name: The schema that we trying to check if is ready
    :type schema_name: string
    :param hour_start_time: the hour (start time) that we trying to check if is ready
    :type hour_start_time: string
    """
    ui_color = '#e0d576'  # yellow
    ui_fgcolor = '#000000'  # black

    def __init__(
            self,
            schema_name,
            *args, **kwargs):
        super(HourIsReadySensorOperator, self).__init__(*args, **kwargs)

        self._schema_name = schema_name.lower()

    def poke(self, context):
        logging.debug("context is " + str(context))
        try:
            """
            @return: bool - whether the events for an hour (whose start time is represented as 'ts' in the context) are 
            ready for adapter processing
            """
            self._hour_start_time = str(context['ts']).replace(" ", "T") + "Z"  # adjust no-timezone->UTC
            logging.info(
                'Poking for the following: '
                'schema_name = {self._schema_name}, '
                'time = self._hour_start_time-{self._hour_start_time}.'.format(**locals()))
            presidio_home = os.environ.get("PRESIDIO_HOME")
            if presidio_home is None:
                user = os.environ.get("USER")
                raise EnvironmentError("PRESIDIO_HOME is not configured for user {0}".format(user))
            source_properties_file = os.path.join(presidio_home, "flume", "counters", "source", self._schema_name)
            logging.debug("source file = " + source_properties_file)

            if not os.path.isfile(source_properties_file):
                logging.info("No count file for source in path {0}".format(source_properties_file))
                return False

            latest_ready_hour = self.get_counter_property(LATEST_READY_HOUR_MARKER, source_properties_file)
            if latest_ready_hour is None:
                raise RuntimeError("No {0} property!".format(LATEST_READY_HOUR_MARKER))

            # Convert the date times to epoch representation. example: 2017-06-27T19\:00\:00Z -> 1498579200.0
            hour_start_time_epoch_seconds = time.mktime(
                datetime.datetime.strptime(self._hour_start_time.replace("\\", ""),
                                           "%Y-%m-%dT%H:%M:%SZ").timetuple())
            latest_ready_hour_epoch_seconds = time.mktime(
                datetime.datetime.strptime(latest_ready_hour.replace("\\", ""),
                                           "%Y-%m-%dT%H:%M:%SZ").timetuple())
            hour_is_ready = hour_start_time_epoch_seconds <= latest_ready_hour_epoch_seconds

            if hour_is_ready:
                logging.info("Hour {0} is ready!".format(self._hour_start_time))
            else:
                logging.info("Hour {0} is not ready!".format(self._hour_start_time))
            return hour_is_ready
        except Exception as exception:
            logging.error("HourIsReadySensorOperator for schema: {0} and hour_start_time: {1} "
                          "has Failed.".format(self._schema_name, self._hour_start_time), exc_info=True)
            return False

    @staticmethod
    def get_counter_property(property_to_get, properties_file):
        return load_and_get_property(property_to_get, properties_file)
