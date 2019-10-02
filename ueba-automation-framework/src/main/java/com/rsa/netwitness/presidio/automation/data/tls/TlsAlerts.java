package com.rsa.netwitness.presidio.automation.data.tls;

import com.rsa.netwitness.presidio.automation.data.tls.model.Ja3TlsAlertBuilder;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;

import java.util.List;

public enum  TlsAlerts {
    INSTANCE;

    private List<TlsAlert> alerts;

    TlsAlerts() {
        TlsAlert ja3_1 = new Ja3TlsAlertBuilder("ja3_test1")
                .abnormal_domain_for_ja3_outbound()
                // .abnormal_ja3_day_time()   //
                .ja3_abnormal_dst_port_for_dst_org_outbound()    //
                .ja3_abnormal_dst_org_for_src_netname_outbound()  //
                //     .ja3_abnormal_ssl_subject_day_time()
                .abnormal_ja3_for_source_netname_outbound()
                .ja3_abnormal_country_for_ssl_subject_outbound()
                .ja3_abnormal_ssl_subject_for_ja3_outbound();

    }
}
