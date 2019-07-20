package com.rsa.netwitness.presidio.automation.static_content;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AlertsMandatoryIndicators {

    private static final List<String> alertsTestLegacyIndicatorNames = Lists.newArrayList(

            /**             AUTHENTICATION            **/

            "abnormal_logon_day_time",                            /**Auth, Abnormal Logon Time **/
            "high_number_of_distinct_src_computer_clusters",      /**?Auth,  Logon Attempts to Multiple Source Computers **/
            "high_number_of_successful_authentications",          /**Auth, Multiple Successful Authentications **/
            "abnormal_source_machine",                            /**Auth, Abnormal Computer **/

            "abnormal_destination_machine",                       /**Auth, Abnormal Remote Computer **/

            "high_number_of_failed_authentications",              /**Auth, Multiple Failed Authentications **/

            /**            ACTIVE DIRECTORY           **/

            "abnormal_active_directory_day_time_operation",       /**AD, Abnormal AD Change Time**/
            "high_number_of_failed_active_directory_events",      /** AD, Multiple Failed Changes **/
            "high_number_of_group_membership_events",             /** AD, Multiple Object Changes **/

            // TODO: Fix when ready: sensitive group membership indicators not created currently because insufficient data in the events
//                "high_number_of_senesitive_group_membership_events",  /** AD, Multiple Group Changes **/
//                "high_number_of_successful_user_change_security_sensitive_operations", /** AD, Multiple User Changes **/
//                "abnormal_group_membership_sensitive_operation",       /** AD, Abnormal Group Changes**/

            "abnormal_object_change_operation",                   /** AD, Abnormal Object Change **/

            /**                  FILE                **/

            "abnormal_file_day_time",                            /** File, Abnormal File Access Time **/
            "high_number_of_deletions",                           /**File, 	Multiple Delete Events**/
            "high_number_of_successful_file_permission_change",   /** File, Multiple File Access Permission Changes **/
            "high_number_of_failed_file_permission_change_attempts", /** File, 	Multiple Failed File Access Permission Changes**/
            "high_number_of_successful_file_action_operations",   /**File, Multiple Access Events  **/
            "high_number_of_failed_file_action_attempts",         /** File, Multiple Failed File Access Events**/
            "high_number_of_distinct_files_opened_attempts",      /** File, 	Multiple Open Events **/


            /**                PROCESS               **/

            // In v11.3 we don't want to show time anomaly on process
            //                "abnormal_process_day_time",
            "abnormal_process_injects_into_lsass",
            "abnormal_reconnaissance_tool_executed",
            "abnormal_process_executed_a_scripting_tool",
            "user_abnormal_process_executed_a_scripting_tool",
            "abnormal_application_triggered_by_scripting_tool",
            "user_abnormal_application_triggered_by_scripting_tool",
            "abnormal_process_opened_by_scripting_tool",
            "user_abnormal_process_opened_by_scripting_tool",
            "abnormal_process_injects_into_windows_process",  // indicator name TBD
            "high_number_of_distinct_reconnaissance_tools_executed",
            "high_number_of_reconnaissance_tool_activities_executed",
            "high_number_of_reconnaissance_tools_executed_process",

            /**               REGISTRY               **/

            // In v11.3 we don't want to show time anomaly on registry
            //                "abnormal_registry_day_time",
            "abnormal_process_modified_registry_key_group"

            /**                 IOC                 **/

    );

    public static final List<String> TLS_INDICATORS = Lists.newArrayList(
            "high_number_of_bytes_sent_by_ja3_outbound",
            "high_number_of_bytes_sent_by_ja3_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_ssl_subject_outbound",
            "high_number_of_distinct_src_ip_for_ja3_outbound",
            "ja3_abnormal_country_for_ssl_subject_outbound",
            "ja3_abnormal_domain_for_ja3_outbound",
            "ja3_abnormal_domain_for_src_netname_outbound",
            "ja3_abnormal_dst_org_for_src_netname_outbound",
            "ja3_abnormal_dst_port_for_domain_outbound",
            "ja3_abnormal_dst_port_for_dst_org_outbound",
            "ja3_abnormal_dst_port_for_ja3_outbound",
            "ja3_abnormal_dst_port_for_src_netname_outbound",
            "ja3_abnormal_dst_port_for_ssl_subject_outbound",
            "ja3_abnormal_ja3_day_time",
            "ja3_abnormal_ja3_for_source_netname_outbound",
            "ja3_abnormal_ssl_subject_day_time",
            "ja3_abnormal_ssl_subject_for_ja3_outbound",
            "ja3_abnormal_ssl_subject_for_src_netname_outbound",
            "ssl_subject_abnormal_country_for_ssl_subject_outbound",
            "ssl_subject_abnormal_domain_for_ja3_outbound",
            "ssl_subject_abnormal_domain_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_org_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_port_for_domain_outbound",
            "ssl_subject_abnormal_dst_port_for_dst_org_outbound",
            "ssl_subject_abnormal_dst_port_for_ja3_outbound",
            "ssl_subject_abnormal_dst_port_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_port_for_ssl_subject_outbound",
            "ssl_subject_abnormal_ja3_day_time",
            "ssl_subject_abnormal_ja3_for_source_netname_outbound",
            "ssl_subject_abnormal_ssl_subject_day_time",
            "ssl_subject_abnormal_ssl_subject_for_ja3_outbound",
            "ssl_subject_abnormal_ssl_subject_for_src_netname_outbound"
    );

    public static final List<String> ALERTS_TEST_MANDATORY_INDICATOR_NAMES = Stream.concat(
            alertsTestLegacyIndicatorNames.stream(), IndicatorSchemaDictionary.getIndicatorsBySchema("TLS").stream())
            .collect(toList());

}
