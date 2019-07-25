package com.rsa.netwitness.presidio.automation.static_content;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class IndicatorSchemaDictionary {

    private static HashMap<String, String> dictionary;

    private static String file = "FILE";
    private static String ad = "ACTIVE_DIRECTORY";
    private static String authentication = "AUTHENTICATION";
    private static String process = "PROCESS";
    private static String registry = "REGISTRY";
    private static String ioc = "IOC";
    private static String tls = "TLS";

    static {
        dictionary = new HashMap<String, String>();

        dictionary.put("abnormal_file_day_time", file);
        dictionary.put("abnormal_file_permision_change_operation_type", file);
        dictionary.put("abnormal_file_action_operation_type", file);
        dictionary.put("high_number_of_successful_file_permission_change", file);
        dictionary.put("high_number_of_successful_file_action_operations", file);
        dictionary.put("high_number_of_failed_file_permission_change_attempts", file);
        dictionary.put("high_number_of_failed_file_action_attempts", file);
        dictionary.put("high_number_of_distinct_files_opened_attempts", file);
        dictionary.put("high_number_of_distinct_folders_opened_attempts", file);
        dictionary.put("high_number_of_deletions", file);
        dictionary.put("high_number_of_successful_file_rename_operations", file);
        dictionary.put("high_number_of_file_move_operations", file);
        dictionary.put("high_number_of_file_move_operations_from_shared_drive", file);
        dictionary.put("high_number_of_file_move_operations_to_shared_drive", file);
        dictionary.put("abnormal_active_directory_day_time_operation", ad);
        dictionary.put("abnormal_group_membership_sensitive_operation", ad);
        dictionary.put("high_number_of_senesitive_group_membership_events", ad);
        dictionary.put("high_number_of_group_membership_events", ad);
        dictionary.put("high_number_of_successful_object_change_operations", ad);
        dictionary.put("high_number_of_successful_user_change_security_sensitive_operations", ad);
        dictionary.put("high_number_of_failed_active_directory_events", ad);
        dictionary.put("admin_changed_his_own_password", ad);
        dictionary.put("user_account_enabled", ad);
        dictionary.put("user_account_disabled", ad);
        dictionary.put("user_account_unlocked", ad);
        dictionary.put("user_account_type_changed", ad);
        dictionary.put("user_account_locked", ad);
        dictionary.put("user_password_never_expires_option_changed", ad);
        dictionary.put("user_password_changed_by_non-owner", ad);
        dictionary.put("user_password_changed", ad);
        dictionary.put("user_password_reset", ad);
        dictionary.put("member_added_to_critical_enterprise_group", ad);
        dictionary.put("nested_member_added_to_critical_enterprise_group", ad);
        dictionary.put("abnormal_logon_day_time", authentication);
        dictionary.put("abnormal_destination_machine", authentication);
        dictionary.put("abnormal_source_machine", authentication);
        dictionary.put("high_number_of_successful_authentications", authentication);
        dictionary.put("high_number_of_failed_authentications", authentication);
        dictionary.put("high_number_of_distinct_src_computers", authentication);
        dictionary.put("high_number_of_distinct_dst_computers", authentication);
        dictionary.put("abnormal_object_change_operation", ad);
        dictionary.put("abnormal_site", authentication);
        dictionary.put("high_number_of_distinct_sites", authentication);
        dictionary.put("high_number_of_distinct_src_computer_clusters", authentication);
        dictionary.put("abnormal_process_injects_into_lsass", process);
        dictionary.put("abnormal_reconnaissance_tool_executed", process);
        dictionary.put("abnormal_process_executed_a_scripting_tool", process);
        dictionary.put("user_abnormal_process_executed_a_scripting_tool", process);
        dictionary.put("abnormal_application_triggered_by_scripting_tool", process);
        dictionary.put("user_abnormal_application_triggered_by_scripting_tool", process);
        dictionary.put("abnormal_process_opened_by_scripting_tool", process);
        dictionary.put("user_abnormal_process_opened_by_scripting_tool", process);
        dictionary.put("abnormal_process_injects_into_windows_process", process);
        dictionary.put("high_number_of_distinct_reconnaissance_tools_executed", process);
        dictionary.put("high_number_of_reconnaissance_tool_activities_executed", process);
        dictionary.put("high_number_of_reconnaissance_tools_executed_process", process);
        dictionary.put("abnormal_process_modified_registry_key_group", registry);

        dictionary.put("high_number_of_bytes_sent_by_ja3_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_to_domain_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_to_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_distinct_src_ip_for_ja3_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound", tls);
        dictionary.put("high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound", tls);
        dictionary.put("ja3_abnormal_country_for_ssl_subject_outbound", tls);
        dictionary.put("ja3_abnormal_domain_for_ja3_outbound", tls);
        dictionary.put("ja3_abnormal_domain_for_src_netname_outbound", tls);
        dictionary.put("ja3_abnormal_dst_org_for_src_netname_outbound", tls);
        dictionary.put("ja3_abnormal_ja3_day_time", tls);
        dictionary.put("ja3_abnormal_ja3_for_source_netname_outbound", tls);
        dictionary.put("ja3_abnormal_ssl_subject_day_time", tls);
        dictionary.put("ja3_abnormal_ssl_subject_for_ja3_outbound", tls);
        dictionary.put("ja3_abnormal_ssl_subject_for_src_netname_outbound", tls);
        dictionary.put("ja3_abnormal_dst_port_for_domain_outbound", tls);
        dictionary.put("ja3_abnormal_dst_port_for_dst_org_outbound", tls);
        dictionary.put("ja3_abnormal_dst_port_for_ja3_outbound", tls);
        dictionary.put("ja3_abnormal_dst_port_for_ssl_subject_outbound", tls);
        dictionary.put("ja3_abnormal_dst_port_for_src_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_country_for_ssl_subject_outbound", tls);
        dictionary.put("ssl_subject_abnormal_domain_for_ja3_outbound", tls);
        dictionary.put("ssl_subject_abnormal_domain_for_src_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_org_for_src_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_ja3_day_time", tls);
        dictionary.put("ssl_subject_abnormal_ja3_for_source_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_ssl_subject_day_time", tls);
        dictionary.put("ssl_subject_abnormal_ssl_subject_for_ja3_outbound", tls);
        dictionary.put("ssl_subject_abnormal_ssl_subject_for_src_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_port_for_ssl_subject_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_port_for_src_netname_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_port_for_ja3_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_port_for_dst_org_outbound", tls);
        dictionary.put("ssl_subject_abnormal_dst_port_for_domain_outbound", tls);
    }

    public static String getIndicatorSchema(String indicator) { return dictionary.get(indicator); }

    public static List<String> getIndicatorsBySchema(String schema) {
        return dictionary.entrySet().stream()
                .filter(e -> e.getValue().equals(schema))
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }
}
