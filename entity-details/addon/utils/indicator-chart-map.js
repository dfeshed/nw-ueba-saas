/**
 * This utils contains a map for all anomalies and chart settings for those anomalies.
 * For any new anomalies just need to add entry here. Library will take care rendering respective chart with proper data.
 *
 * @private
 */
import singlePieSettings from './chart-settings/single-pie-settings';
import heatmapSettings from './chart-settings/heatmap-settings';
import activityTimeAnomalySettings from './chart-settings/activity-time-anomaly-settings';

export default {
  singlePie: { anomalyTypeFieldName: ['abnormal_event_day_time'], settings: singlePieSettings },
  singleColumnDataRate: { anomalyTypeFieldName: ['data_bucket'], settings: activityTimeAnomalySettings },
  singleColumnHistogram: {
    anomalyTypeFieldName: [
      'email_sender',
      'account_management_change_anomaly'
    ],
    settings: singlePieSettings },
  activityTimeAnomaly: {
    anomalyTypeFieldName: [
      'event_time',
      'abnormal_file_day_time',
      'abnormal_process_day_time',
      'abnormal_registry_day_time',
      'abnormal_logon_day_time',
      'abnormal_active_directory_day_time_operation'
    ],
    settings: heatmapSettings },
  geoLocation: { anomalyTypeFieldName: ['country'], settings: null },
  timeAggregation: { anomalyTypeFieldName: ['high_number_of_successful_object_change_operations'], settings: activityTimeAnomalySettings },
  geoLocationSequence: { anomalyTypeFieldName: ['vpn_geo_hopping'], settings: null },
  basicTwoHistogramsUser: {
    anomalyTypeFieldName: [
      'normalized_src_machine',
      'normalized_dst_machine',
      'action_code',
      'db_object',
      'db_username',
      'email_recipient_domain'
    ],
    settings: singlePieSettings },
  singlePieHistogram: {
    anomalyTypeFieldName: [
      'auth_method',
      'failure_code',
      'action_type',
      'status',
      'return_code',
      'attachment_file_extension',
      'executing_application',
      'abnormal_computer_accessed_remotely',
      'abnormal_process_injects_into_lsass',
      'abnormal_reconnaissance_tool_executed',
      'abnormal_process_executed_a_scripting_tool',
      'abnormal_application_triggered_by_scripting_tool',
      'abnormal_process_opened_by_scripting_tool',
      'abnormal_file_permission_change_operation_type',
      'abnormal_process_modified_registry_key_group',
      'user_abnormal_process_executed_a_scripting_tool',
      'user_abnormal_application_triggered_by_scripting_tool',
      'user_abnormal_process_opened_by_scripting_tool',
      'abnormal_process_injects_into_windows_process',
      'abnormal_file_action_operation_type',
      'admin_changed_his_own_password',
      'user_account_enabled',
      'user_account_disabled',
      'user_account_unlocked',
      'user_account_type_changed',
      'user_account_locked',
      'user_password_never_expires_option_changed',
      'user_password_changed_by_non-owner',
      'user_password_changed',
      'user_password_reset',
      'nested_member_added_to_critical_enterprise_group',
      'member_added_to_critical_enterprise_group',
      'abnormal_destination_machine',
      'abnormal_source_machine',
      'abnormal_object_change_operation',
      'abnormal_group_membership_sensitive_operation',
      'abnormal_remote_destination_machine',
      'abnormal_site',
      'abnormal_reconnaissance_tool_executed'
    ],
    settings: singlePieSettings },
  aggregatedIndicatorWithTime: { anomalyTypeFieldName: ['AnomalyAggregatedEvent'], settings: null },
  lateralMovementIndicator: { anomalyTypeFieldName: ['VPN_user_lateral_movement'], settings: null }
};