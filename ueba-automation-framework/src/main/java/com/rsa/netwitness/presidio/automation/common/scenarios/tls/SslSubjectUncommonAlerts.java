package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.model.SslSubjectTlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SslSubjectUncommonAlerts  implements Supplier<Stream<TlsAlert>> {
    private final int dataPeriod;
    private final int uncommonStartDay;
    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();

    public SslSubjectUncommonAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    @Override
    public Stream<TlsAlert> get() {
        return alertsHolder.getOrCompute(this::create).stream();
    }

    private List<TlsAlert> create() {
        List<TlsAlert> list = Lists.newLinkedList();

        TlsAlert ssl_subject_1 = new SslSubjectTlsAlert("4.sophosxl.net", dataPeriod, uncommonStartDay)
                .ssl_subject_abnormal_domain_for_ja3_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .ssl_subject_abnormal_ssl_subject_for_ja3_outbound()
                .ssl_subject_abnormal_dst_port_for_domain_outbound()
                .create();

        TlsAlert ssl_subject_2 = new SslSubjectTlsAlert("trend micro inc.", dataPeriod, uncommonStartDay)
                .ssl_subject_abnormal_country_for_ssl_subject_outbound()
                .ssl_subject_abnormal_domain_for_src_netname_outbound()
                .ssl_subject_abnormal_ssl_subject_for_src_netname_outbound()
                .ssl_subject_abnormal_dst_port_for_src_netname_outbound()
                .create();


        TlsAlert ssl_subject_3 = new SslSubjectTlsAlert("bitdefender srl", dataPeriod, uncommonStartDay)
                .ssl_subject_abnormal_dst_port_for_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_port_for_ja3_outbound()
                .ssl_subject_abnormal_dst_port_for_dst_org_outbound()
                .create();


        TlsAlert ssl_subject_4 = new SslSubjectTlsAlert("industria de diseno textil sa", dataPeriod, uncommonStartDay)
                .ssl_subject_abnormal_ssl_subject_day_time()
                .ssl_subject_abnormal_ja3_day_time()
                .ssl_subject_abnormal_domain_for_ja3_outbound()
                .create();

        TlsAlert ssl_subject_5 = new SslSubjectTlsAlert("industria de diseno textil sa", dataPeriod, uncommonStartDay)
                .ssl_subject_abnormal_ja3_day_time()
                .ssl_subject_abnormal_domain_for_ja3_outbound()
                .ssl_subject_abnormal_dst_port_for_dst_org_outbound()
                .create();

        list.add(ssl_subject_1);
        list.add(ssl_subject_2);
        list.add(ssl_subject_3);
        list.add(ssl_subject_4);
        list.add(ssl_subject_5);

        return list;
    }
}