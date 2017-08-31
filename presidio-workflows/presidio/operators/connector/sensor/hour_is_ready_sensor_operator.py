import logging
import os
from airflow.operators.sensors import BaseSensorOperator
from presidio.utils.connector.properties_loader import load_and_get_property

HOUR_IS_READY_MARKER = "READY"
HOUR_IS_READY_DELIM = "_"


class HourIsReadySensorOperator(BaseSensorOperator):
    """
    Sensor count files until source-is-ready and source-count = sink-count

    :param schema_name: The schema that we trying to check if is ready
    :type schema_name: string
    :param hour_end_time: the hour (end time) that we trying to check if is ready
    :type hour_end_time: string
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
            @return: bool - whether the events for an hour (whose end time was given as hour_end_time) are ready for
            adapter processing
            """
            self._hour_end_time = str(context['next_execution_date']).replace(" ", "T") + "Z"  # adjust no-timezone->UTC
            hour_start_time = context['ts']
            logging.info(
                'Poking for the following: '
                'schema_name = {self._schema_name}, '
                'time = {self._hour_end_time}.'.format(**locals()))
            presidio_home = os.environ.get("PRESIDIO_HOME")
            if presidio_home is None:
                user = os.environ.get("USER")
                logging.error("PRESIDIO_HOME is not configured for user {0}".format(user))
            source_properties_file = os.path.join(presidio_home, "flume", "counters", "source", self._schema_name)
            logging.info("source_properties_file = " + source_properties_file)
            sink_properties_file = os.path.join(presidio_home, "flume", "counters", "sink", self._schema_name)
            logging.info("sink file = " + sink_properties_file)
            if not os.path.isfile(source_properties_file):
                logging.debug("No count file for source in path {0}".format(source_properties_file))
                return False

            if not os.path.isfile(sink_properties_file):
                logging.debug("No count file for sink in path {0}".format(sink_properties_file))
                return False

            source_counter_property = self.get_counter_property(source_properties_file)
            if source_counter_property is None:
                return False
            if HOUR_IS_READY_DELIM in source_counter_property:
                source_is_ready = source_counter_property.split(HOUR_IS_READY_DELIM)[0] == HOUR_IS_READY_MARKER
                source_count = source_counter_property.split(HOUR_IS_READY_DELIM)[1]
            else:
                source_is_ready = False
                source_count = source_counter_property

            sink_count = self.get_counter_property(sink_properties_file)
            if sink_count is None:
                return False

            if sink_count > source_count:
                logging.warn("Sink count is larger than the source count. This is an invalid state!. "
                             "source count: {0}, "
                             "sink count: {1}".format(source_counter_property, sink_count))
                logging.debug("Source count for schema {0} and time {1} is : {2}".format(
                    self._schema_name,
                    self._hour_end_time,
                    source_counter_property))
                logging.debug("Sink count for schema {0} and time {1} is : {2}".format(
                    self._schema_name,
                    self._hour_end_time,
                    sink_count))
            return source_is_ready and source_count <= sink_count
        except Exception as exception:
            logging.error("HourIsReadySensorOperator for schema: {0} and hour_end_time: {1} "
                          "has Failed.".format(self._schema_name, self._hour_end_time), exc_info=True)
            return False

    def get_counter_property(self, properties_file):
        key = self._hour_end_time.replace(":", "\\:")  # java-props escapes char ':' and python just reads it as string
        return load_and_get_property(key, properties_file)
