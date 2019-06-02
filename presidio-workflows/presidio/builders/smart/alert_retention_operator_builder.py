from datetime import timedelta
from airflow import LoggingMixin
from presidio.operators.retention.alert_retention_operator import AlertRetentionOperator
from presidio.utils.configuration.config_server_configuration_reader_singleton import \
    ConfigServerConfigurationReaderSingleton


class AlertRetentionOperatorBuilder(LoggingMixin):
    """
    The "AlertRetentionOperatorBuilder" builds and returns alert_retention operator.
    """

    RETENTION_COMMAND_CONFIG_PATH = 'retention.command'
    RETENTION_COMMAND_DEFAULT_VALUE = 'retention'
    alert_min_time_to_start_retention_in_days_conf_key = "retention.min_time_to_start_retention_in_days"
    alert_min_time_to_start_retention_in_days_default_value = 2
    alert_retention_interval_in_hours_conf_key = "retention.retention_interval_in_hours"
    alert_retention_interval_in_hours_default_value = 24

    def __init__(self):
        conf_reader = ConfigServerConfigurationReaderSingleton().config_reader
        self._retention_command = conf_reader.read(
            AlertRetentionOperatorBuilder.RETENTION_COMMAND_CONFIG_PATH,
            AlertRetentionOperatorBuilder.RETENTION_COMMAND_DEFAULT_VALUE)

    @staticmethod
    def get_alert_min_time_to_start_retention_in_days(conf_reader):
        return conf_reader.read(
            AlertRetentionOperatorBuilder.alert_min_time_to_start_retention_in_days_conf_key,
            AlertRetentionOperatorBuilder.alert_min_time_to_start_retention_in_days_default_value)

    @staticmethod
    def get_alert_retention_interval_in_hours(conf_reader):
        return timedelta(
            hours=conf_reader.read(
                AlertRetentionOperatorBuilder.alert_retention_interval_in_hours_conf_key,
                AlertRetentionOperatorBuilder.alert_retention_interval_in_hours_default_value))

    def build(self, dag, entity_type):
        """
        Builds alert_retention operator.
        :param dag: The DAG to which all relevant retention operators should be added
        :type dag: airflow.models.DAG
        :param entity_type: The entity_type which all relevant alerts will be deleted.
        :type entity_type: String
        :return: alert_retention
        :rtype: presidio.operators.fixed_duration_jar_operator.FixedDurationJarOperator
        """

        self.log.debug("populating the %s dag with alert_retention tasks", dag.dag_id)

        alert_retention = AlertRetentionOperator(
            command=self._retention_command,
            entity_type=entity_type,
            run_clean_command_before_retry=False,
            dag=dag)

        return alert_retention
