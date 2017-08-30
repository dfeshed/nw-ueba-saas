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
    ui_color = '#a50b0b'  # dark red
    ui_fgcolor = '#000000'  # black

    def __init__(
            self,
            schema_name,
            *args, **kwargs):
        super(HourIsReadySensorOperator, self).__init__(*args, **kwargs)

        self._schema_name = schema_name

    def poke(self, context):
        try:
            """
            @return: bool - whether the events for an hour (whose end time was given as hour_end_time) are ready for
            adapter processing
            """
            self._hour_end_time = context['execution_date']
            logging.info(
                'Poking for the following'
                '{self._schema_name}.'
                '{self._hour_end_time} on'
                '{execution_date} ... '.format(**locals()))

            presidio_home = os.environ.get("PRESIDIO_HOME")
            source_properties_file = os.path.join(presidio_home, "flume", "counters", "source", self._schema_name)
            sink_properties_file = os.path.join(presidio_home, "flume", "counters", "sink", self._schema_name)
            if not os.path.isfile(source_properties_file):
                return False

            if not os.path.isfile(sink_properties_file):
                return False

            source_counter_property = self.get_counter_property(source_properties_file)
            if source_counter_property is None:
                return False
            if HOUR_IS_READY_DELIM in source_counter_property:
                source_is_ready = source_counter_property.split(HOUR_IS_READY_DELIM)[0]
                source_count = source_counter_property.split(HOUR_IS_READY_DELIM)[1]
            else:
                source_is_ready = False
                source_count = source_counter_property

            sink_count = self.get_counter_property(sink_properties_file)
            if sink_count is None:
                return False

            if sink_count > source_count:
                logging.warn("Sink count is larger than the source count. This an invalid state!. source count: %s, "
                             "sink count: %s" % source_counter_property, sink_count)
            return source_is_ready and source_count <= sink_count
        except Exception as exception:
            logging.error(("HourIsReadySensorOperator for schema: %s and hour_end_time: %s "
                           "has Failed." % self._schema_name, self._hour_end_time), exception)

    def get_counter_property(self, properties_file):
        return load_and_get_property(self._hour_end_time, properties_file)

