from airflow import LoggingMixin

from presidio.operators.adapter.hour_is_ready_s3_operator import HourIsReadyAccordingToS3NWGatewaySensorOperator
from presidio.utils.airflow.operators.sensor.hour_is_ready_according_to_system_time_sensor_operator import \
    HourIsReadyAccordingToSystemTimeSensorOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

ADAPTER_HOUR_IS_READY_SENSOR_TYPE_CONF_KEY = "components.adapter.hour_is_ready_sensor.type"
ADAPTER_HOUR_IS_READY_SENSOR_TYPE_DEFAULT_VALUE = "HourIsReadyAccordingToSystemTimeSensorOperator"
ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_CONF_KEY = "components.adapter.hour_is_ready_sensor.command"
ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_DEFAULT_VALUE = "waitTillHourIsReady"


class HourIsReadySensorOperatorBuilder(LoggingMixin):

    def __init__(self, schema, timeout, time_to_sleep_in_seconds):
        self.schema = schema
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._sensor_type = conf_reader.read(ADAPTER_HOUR_IS_READY_SENSOR_TYPE_CONF_KEY,
                                             ADAPTER_HOUR_IS_READY_SENSOR_TYPE_DEFAULT_VALUE)
        self._sensor_command = conf_reader.read(ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_CONF_KEY,
                                                ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_DEFAULT_VALUE)
        self.timeout = timeout
        self.time_to_sleep_in_seconds = time_to_sleep_in_seconds

    def build(self, dag):
        task_id = 'adapter_sensor_{}'.format(self.schema)
        if self._sensor_type == "HourIsReadyAccordingToSystemTimeSensorOperator":
            return HourIsReadyAccordingToSystemTimeSensorOperator(dag=dag,
                                                                  task_id=task_id,
                                                                  poke_interval=self.time_to_sleep_in_seconds,
                                                                  timeout=self.timeout,
                                                                  schema_name=self.schema)

        elif self._sensor_type == "HourIsReadyAccordingToS3NWGatewaySensorOperator":
            return HourIsReadyAccordingToS3NWGatewaySensorOperator(dag=dag,
                                                                   command=self._sensor_command,
                                                                   task_id=task_id,
                                                                   schema=self.schema,
                                                                   run_clean_command_before_retry=False,
                                                                   timeout=self.timeout,
                                                                   time_to_sleep_in_seconds=self.time_to_sleep_in_seconds)
