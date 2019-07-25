package com.rsa.netwitness.presidio.automation.static_content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Deprecated
public class AlertClassificationIndicatorDictionary {

    private static HashMap<String, String> dictionary;

    private static String nonStandardHours = "non_standard_hours";
    private static String massPermissionChanges = "mass_permission_changes";
    private static String abnormalFileAccess = "abnormal_file_access";
    private static String snoopingUser = "snooping_user";
    private static String dataExfiltration = "data_exfiltration";
    private static String massFileRename = "mass_file_rename";
    private static String abnormalADChanges = "abnormal_ad_changes";
    private static String massChangestoCriticalEnterpriseGroups = "mass_changes_to_critical_enterprise_groups";
    private static String massChangestoGroups = "mass_changes_to_groups";
    private static String adminPasswordChange = "admin_password_change";
    private static String sensitiveUserStatusChanges = "sensitive_user_status_changes";
    private static String elevatedPrivilegesGranted = "elevated_privileges_granted";
    private static String userLogintoAbnormalRemoteHost = "user_login_to_abnormal_remote_host";
    private static String userLogintoAbnormalHost = "user_login_to_abnormal_host";
    private static String multipleLogonsbyUser = "multiple_logons_by_user";
    private static String userLoginstoMultipleDomains = "user_logins_to_multiple_domains";
    private static String userLoggedintoMultipleHosts = "user_logged_into_multiple_hosts";
    private static String grand = "grand";
    private static String suspiciousUserActivity = "suspicious_user_activity";
    private static String bruteForceAuthentication = "brute_force_authentication";
    private static String abnormalSiteAccess = "abnormal_site_access";
    private static String credentialDumping = "credential_dumping";
    private static String discoveryReconnaissance = "discovery_reconnaissance";
    private static String powershellScripting = "powershell_scripting";
    private static String registryRunKeys = "registry_run_keys";
    private static String processInjection = "process_injection";

    /** tls **/
    private static String exfiltration = "exfiltration";
    private static String phishing = "phishing";


    static {
        dictionary = new HashMap<>();

        dictionary.put("abnormal_file_day_time",nonStandardHours);
        dictionary.put("abnormal_file_permision_change_operation_type",massPermissionChanges);
        dictionary.put("abnormal_file_action_operation_type",abnormalFileAccess);
        dictionary.put("high_number_of_successful_file_permission_change",massPermissionChanges);
        dictionary.put("high_number_of_successful_file_action_operations",snoopingUser);
        dictionary.put("high_number_of_failed_file_permission_change_attempts",massPermissionChanges);
        dictionary.put("high_number_of_failed_file_action_attempts",snoopingUser);
        dictionary.put("high_number_of_distinct_files_opened_attempts",snoopingUser);
        dictionary.put("high_number_of_distinct_folders_opened_attempts",snoopingUser);
        dictionary.put("high_number_of_deletions",abnormalFileAccess);
        dictionary.put("high_number_of_successful_file_rename_operations",massFileRename);
        dictionary.put("high_number_of_file_move_operations",dataExfiltration);
        dictionary.put("abnormal_active_directory_day_time_operation",nonStandardHours);
        dictionary.put("abnormal_group_membership_sensitive_operation",abnormalADChanges);
        dictionary.put("high_number_of_senesitive_group_membership_events",massChangestoCriticalEnterpriseGroups);
        dictionary.put("high_number_of_group_membership_events",massChangestoGroups);
        dictionary.put("high_number_of_successful_object_change_operations",abnormalADChanges);
        dictionary.put("high_number_of_successful_user_change_security_sensitive_operations",abnormalADChanges);
        dictionary.put("high_number_of_failed_active_directory_events",abnormalADChanges);
        dictionary.put("admin_changed_his_own_password", adminPasswordChange);
        dictionary.put("user_account_enabled", sensitiveUserStatusChanges);
        dictionary.put("user_account_disabled", sensitiveUserStatusChanges);
        dictionary.put("user_account_unlocked", sensitiveUserStatusChanges);
        dictionary.put("user_account_type_changed", sensitiveUserStatusChanges);
        dictionary.put("user_account_locked", sensitiveUserStatusChanges);
        dictionary.put("user_password_never_expires_option_changed", sensitiveUserStatusChanges);
        dictionary.put("user_password_changed_by_non-owner", sensitiveUserStatusChanges);
        dictionary.put("user_password_changed", sensitiveUserStatusChanges);
//        dictionary.put("nested_member_added_to_critical_enterprise_group",elevatedPrivilegesGranted);
        dictionary.put("member_added_to_critical_enterprise_group", elevatedPrivilegesGranted);
        dictionary.put("abnormal_logon_day_time", nonStandardHours);
        dictionary.put("abnormal_destination_machine", userLogintoAbnormalRemoteHost);
        dictionary.put("abnormal_source_machine", userLogintoAbnormalHost);
        dictionary.put("high_number_of_successful_authentications", multipleLogonsbyUser);
        dictionary.put("high_number_of_failed_authentications", bruteForceAuthentication);
//        dictionary.put("high_number_of_distinct_destination_domains", userLoginstoMultipleDomains);
//        dictionary.put("high_number_of_distinct_src_computers", userLoggedintoMultipleHosts);
        dictionary.put("high_number_of_distinct_dst_computers", userLoggedintoMultipleHosts);
//        dictionary.put("high_number_of_distinct_dst_computer_clusters", userLoggedintoMultipleHosts);
        dictionary.put("high_number_of_distinct_src_computer_clusters", userLoggedintoMultipleHosts);
        dictionary.put("high_number_of_file_move_operations_to_shared_drive", dataExfiltration);
        dictionary.put("high_number_of_file_move_operations_from_shared_drive", dataExfiltration);
        dictionary.put("abnormal_object_change_operation", abnormalADChanges);
        dictionary.put("abnormal_site", abnormalSiteAccess);
        dictionary.put("high_number_of_distinct_sites", abnormalSiteAccess);
        dictionary.put("abnormal_process_day_time", nonStandardHours);
        dictionary.put("abnormal_reconnaissance_tool_executed", discoveryReconnaissance);
        dictionary.put("abnormal_process_executed_a_scripting_tool", powershellScripting);
        dictionary.put("abnormal_application_triggered_by_scripting_tool", powershellScripting);
        dictionary.put("abnormal_process_opened_by_scripting_tool", powershellScripting);
        dictionary.put("abnormal_process_injects_into_windows_process", processInjection);
        dictionary.put("high_number_of_reconnaissance_tool_activities_executed", discoveryReconnaissance);
        dictionary.put("high_number_of_distinct_reconnaissance_tools_executed", discoveryReconnaissance);
        dictionary.put("high_number_of_reconnaissance_tools_executed_process", discoveryReconnaissance);
        dictionary.put("abnormal_process_injects_into_lsass", credentialDumping);
        dictionary.put("abnormal_process_modified_registry_key_group", registryRunKeys);
        dictionary.put("abnormal_registry_day_time", nonStandardHours);
        dictionary.put("user_abnormal_process_executed_a_scripting_tool", powershellScripting);
        dictionary.put("user_abnormal_application_triggered_by_scripting_tool", powershellScripting);
        dictionary.put("user_abnormal_process_opened_by_scripting_tool", powershellScripting);

        /** tls **/
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_to_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_to_domain_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound", exfiltration);
        dictionary.put("high_number_of_bytes_sent_by_ja3_outbound", exfiltration);
        dictionary.put("high_number_of_distinct_src_ip_for_ja3_outbound", phishing);
        dictionary.put("ssl_subject_abnormal_ssl_subject_for_src_netname_outbound", exfiltration);
        dictionary.put("ja3_abnormal_ssl_subject_for_src_netname_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_domain_for_src_netname_outbound", exfiltration);
        dictionary.put("ja3_abnormal_domain_for_src_netname_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_dst_port_for_src_netname_outbound", exfiltration);
        dictionary.put("ja3_abnormal_dst_port_for_src_netname_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_dst_org_for_src_netname_outbound", exfiltration);
        dictionary.put("ja3_abnormal_dst_org_for_src_netname_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_dst_port_for_ssl_subject_outbound", exfiltration);
        dictionary.put("ja3_abnormal_dst_port_for_ssl_subject_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_dst_port_for_domain_outbound", exfiltration);
        dictionary.put("ja3_abnormal_dst_port_for_domain_outbound", exfiltration);
        dictionary.put("ssl_subject_abnormal_dst_port_for_dst_org_outbound", exfiltration);
        dictionary.put("ja3_abnormal_dst_port_for_dst_org_outbound", exfiltration);

        dictionary.put("ssl_subject_abnormal_ssl_subject_day_time", nonStandardHours);
        dictionary.put("ja3_abnormal_ssl_subject_day_time", nonStandardHours);
        dictionary.put("ssl_subject_abnormal_ja3_day_time", nonStandardHours);
        dictionary.put("ja3_abnormal_ja3_day_time", nonStandardHours);

        dictionary.put("ssl_subject_abnormal_country_for_ssl_subject_outbound", phishing);
        dictionary.put("ja3_abnormal_country_for_ssl_subject_outbound", phishing);
        dictionary.put("ssl_subject_abnormal_ja3_for_source_netname_outbound", phishing);
        dictionary.put("ja3_abnormal_ja3_for_source_netname_outbound", phishing);
        dictionary.put("ssl_subject_abnormal_ssl_subject_for_ja3_outbound", phishing);
        dictionary.put("ja3_abnormal_ssl_subject_for_ja3_outbound", phishing);
        dictionary.put("ssl_subject_abnormal_domain_for_ja3_outbound", phishing);
        dictionary.put("ja3_abnormal_domain_for_ja3_outbound", phishing);
        dictionary.put("ssl_subject_abnormal_dst_port_for_ja3_outbound", phishing);
        dictionary.put("ja3_abnormal_dst_port_for_ja3_outbound", phishing);

    }

    public static String getIndicatorClassification(String indicator){
        return dictionary.get(indicator);
    }

    public static HashMap<String, String> getAll(){
        return dictionary;
    }


    public static List<String> getClassificationListByPrioritizedOrder(){
        List<String> classifications = new ArrayList<>();
        classifications.add(credentialDumping);
        classifications.add(discoveryReconnaissance);
        classifications.add(powershellScripting);
        classifications.add(registryRunKeys);
        classifications.add(processInjection);
        classifications.add(massChangestoCriticalEnterpriseGroups);
        classifications.add(massChangestoGroups);
        classifications.add(elevatedPrivilegesGranted);
        classifications.add(bruteForceAuthentication);
        classifications.add(userLoginstoMultipleDomains);
        classifications.add(userLogintoAbnormalRemoteHost);
        classifications.add(userLogintoAbnormalHost);
        classifications.add(dataExfiltration);
        classifications.add(massFileRename);
        classifications.add(snoopingUser);
        classifications.add(multipleLogonsbyUser);
        classifications.add(userLoggedintoMultipleHosts);
        classifications.add(adminPasswordChange);
        classifications.add(massPermissionChanges);
        classifications.add(abnormalSiteAccess);
        classifications.add(abnormalADChanges);
        classifications.add(sensitiveUserStatusChanges);
        classifications.add(abnormalFileAccess);
        classifications.add(nonStandardHours);
        classifications.add(suspiciousUserActivity);
        classifications.add(phishing);
        classifications.add(exfiltration);

        return classifications;
    }
}
