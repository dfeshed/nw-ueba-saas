import datetime
import logging
import time
from datetime import timedelta
from airflow.operators.sensors import BaseSensorOperator
from presidio.utils.connector.properties_loader import load_and_get_property

DELAY_TIME_IN_SECONDS = 60 * 15


class HourIsReadySensorOperator(BaseSensorOperator):
    """
    Sensor that waits for 15 minutes
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
        super(HourIsReadySensorOperator, self).__init__(
            retries=99999,
            retry_exponential_backoff=True,
            max_retry_delay=timedelta(seconds=300),
            retry_delay=timedelta(seconds=5),
            *args,
            **kwargs
        )

        self._hour_start_time = None
        self._schema_name = schema_name.lower()

    def poke(self, context):
        logging.debug("context is " + str(context))
        try:
            self._hour_start_time = str(context['ts']).replace(" ", "T") + "Z"  # adjust no-timezone->UTC

            # Convert the date times to epoch representation. example: 2017-06-27T19\:00\:00Z -> 1498579200.0
            hour_start_time_epoch_seconds = time.mktime(
                datetime.datetime.strptime(self._hour_start_time.replace("\\", ""),
                                           "%Y-%m-%dT%H:%M:%SZ").timetuple())
            now_epoch_seconds = int(time.time())

            hour_end_time_epoch_seconds = hour_start_time_epoch_seconds + 3600  # add an hour
            time_to_sleep_seconds = (hour_end_time_epoch_seconds + DELAY_TIME_IN_SECONDS) - now_epoch_seconds
            if time_to_sleep_seconds > 0:
                logging.info(
                    "Hour {0} is not ready!. hour_end_time_epoch_seconds: {1}, now_epoch_seconds: {2}, "
                    "time_to_sleep_seconds: {3}".format(
                        self._hour_start_time, hour_end_time_epoch_seconds, now_epoch_seconds, time_to_sleep_seconds))
                return False

            logging.info("Hour {0} is ready!".format(self._hour_start_time))
            return True
        except Exception as exception:
            logging.error("HourIsReadySensorOperator for schema: {0} and hour_start_time: {1} "
                          "has Failed.".format(self._schema_name, self._hour_start_time), exc_info=True)
            return False

    @staticmethod
    def get_counter_property(property_to_get, properties_file):
        return load_and_get_property(property_to_get, properties_file)
