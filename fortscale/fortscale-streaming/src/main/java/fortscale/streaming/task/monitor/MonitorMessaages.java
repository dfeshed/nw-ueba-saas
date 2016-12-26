package fortscale.streaming.task.monitor;

/**
 * Created by shays on 21/12/2015.
 */
public class MonitorMessaages {

    public static final String NO_STATE_CONFIGURATION_MESSAGE = "fortscale.message.no_state_configuration_message";
    public static final String CANNOT_EXTRACT_STATE_MESSAGE = "fortscale.message.config";
    public static final String CANNOT_EXTRACT_USER_NAME_MESSAGE = "fortscale.message.cannot_extract_user_name_message";
    public static final String FAIL_TO_NORMALIZED_USERNAME = "fortscale.message.fail_to_normalized_username";
    public static final String BAD_CONFIG_KEY ="fortscale.message.bad_streaming_config_key";
    public static final String SEND_TO_OUTPUT_TOPIC_FAILED_MESSAGE = "fortscale.message.send_to_output_topic_failed_message";
    public static final String MESSAGE_DOES_NOT_CONTAINS_TIMESTAMP_IN_FIELD = "fortscale.message.message_does_not_contains_timestamp_in_field";
    public static final String CANNOT_PARSE_MESSAGE_LABEL = "fortscale.message.cannot_parse_message_label";
    public static final String HOST_IS_EMPTY_LABEL = "fortscale.message.host_is_empty_label";
    public static final String MessageFilter = "fortscale.message.message_filter";
    public static final String NO_TIMESTAMP_FIELD_IN_MESSAGE_label = "fortscale.message.no_timestamp_field_in_message_label";
    public static final String FAILED_TO_SEND_EVENT_TO_KAFKA_LABEL = "fortscale.message.failed_to_send_event_to_kafka_label";
    public static final String EVENT_OLDER_THEN_NEWEST_EVENT_LABEL = "fortscale.message.event_older_then_newest_event_label";
    //Labels for monitoring
    public static final String ACCOUNT_NAME_MATCH_TO_REGEX = "fortscale.message.account_name_match_to_regex";
    public static final String SERVICE_NAME_MATCH_COMPUTER_NAME = "fortscale.message.service_name_match_computer_name";
    public static final String NO_LOG_USERNAME_IN_MESSAGE_LABEL = "fortscale.message.no_log_username_in_message_label";
}
