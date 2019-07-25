package com.rsa.netwitness.presidio.automation.static_content;

import com.google.common.collect.Lists;

import java.util.List;

class NetworkIndicators {

    static final List<String> TLS_MANDATORY_INDICATORS = Lists.newArrayList(
            "high_number_of_bytes_sent_by_ja3_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_ssl_subject_outbound",
            "high_number_of_distinct_src_ip_for_ja3_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound",
            "ja3_abnormal_country_for_ssl_subject_outbound",
            "ja3_abnormal_domain_for_ja3_outbound",
            "ja3_abnormal_domain_for_src_netname_outbound",
            "ja3_abnormal_dst_org_for_src_netname_outbound",
            "ja3_abnormal_ja3_day_time",
            "ja3_abnormal_ja3_for_source_netname_outbound",
            "ja3_abnormal_ssl_subject_day_time",
            "ja3_abnormal_ssl_subject_for_ja3_outbound",
            "ja3_abnormal_ssl_subject_for_src_netname_outbound",
            "ja3_abnormal_dst_port_for_domain_outbound",
            "ja3_abnormal_dst_port_for_dst_org_outbound",
            "ja3_abnormal_dst_port_for_ja3_outbound",
            "ja3_abnormal_dst_port_for_ssl_subject_outbound",
            "ja3_abnormal_dst_port_for_src_netname_outbound",
            "ssl_subject_abnormal_country_for_ssl_subject_outbound",
            "ssl_subject_abnormal_domain_for_ja3_outbound",
            "ssl_subject_abnormal_domain_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_org_for_src_netname_outbound",
            "ssl_subject_abnormal_ja3_day_time",
            "ssl_subject_abnormal_ja3_for_source_netname_outbound",
            "ssl_subject_abnormal_ssl_subject_day_time",
            "ssl_subject_abnormal_ssl_subject_for_ja3_outbound",
            "ssl_subject_abnormal_ssl_subject_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_port_for_ssl_subject_outbound",
            "ssl_subject_abnormal_dst_port_for_src_netname_outbound",
            "ssl_subject_abnormal_dst_port_for_ja3_outbound",
            "ssl_subject_abnormal_dst_port_for_dst_org_outbound",
            "ssl_subject_abnormal_dst_port_for_domain_outbound"
    );
}
