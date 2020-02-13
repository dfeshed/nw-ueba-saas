from datetime import timedelta
from airflow import LoggingMixin

from presidio.operators.adapter.hour_is_ready_s3_operator import HourIsReadyS3Operator
from presidio.utils.airflow.operators.sensor.hour_is_ready_on_prem_sensor_operator import \
    HourIsReadyOnPremSensorOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton

ADAPTER_HOUR_IS_READY_SENSOR_TYPE_CONF_KEY = "components.adapter.hour_is_ready_sensor.type"
ADAPTER_HOUR_IS_READY_SENSOR_TYPE_DEFAULT_VALUE = "on_prem"
ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_CONF_KEY = "components.adapter.hour_is_ready_sensor.command"
ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_DEFAULT_VALUE = "hourIsReady"


class HourIsReadySensorOperatorBuilder(LoggingMixin):

    def __init__(self, schema):
        self.schema = schema
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._sensor_type = conf_reader.read(ADAPTER_HOUR_IS_READY_SENSOR_TYPE_CONF_KEY,
                                             ADAPTER_HOUR_IS_READY_SENSOR_TYPE_DEFAULT_VALUE)
        self._sensor_command = conf_reader.read(ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_CONF_KEY,
                                                ADAPTER_HOUR_IS_READY_SENSOR_COMMAND_DEFAULT_VALUE)

    def build(self, dag):
        task_id = 'adapter_sensor_{}'.format(self.schema)
        if self._sensor_type == "on_prem":
            return HourIsReadyOnPremSensorOperator(dag=dag,
                                                   task_id=task_id,
                                                   poke_interval=60,  # 1 minute
                                                   timeout=60 * 60 * 24 * 7,  # 1 week
                                                   schema_name=self.schema)

        elif self._sensor_type == "s3":
            return HourIsReadyS3Operator(dag=dag,
                                         command=self._sensor_command,
                                         task_id=task_id,
                                         schema=self.schema,
                                         retries=99999,
                                         retry_delay=timedelta(minutes=1))
