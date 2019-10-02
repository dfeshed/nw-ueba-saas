package com.rsa.netwitness.presidio.automation.data.tls;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.model.Ja3TlsAlertBuilder;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;

public class TlsAlerts {
    private final int dataPeriod;
    private final int uncommonStartDay;

    public TlsAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
        alerts = () -> alertsHolder.getOrCompute(() -> create(dataPeriod, uncommonStartDay));
    }

    public Supplier<List<TlsAlert>> alerts;

    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();


    private List<TlsAlert> create(int dataPeriod, int uncommonStartDay) {
        List<TlsAlert> list = Lists.newLinkedList();

        TlsAlert ja3_1 = new Ja3TlsAlertBuilder("ja3_test1", dataPeriod, uncommonStartDay)
                .abnormal_domain_for_ja3_outbound()
                // .abnormal_ja3_day_time()   //
                .ja3_abnormal_dst_port_for_dst_org_outbound()    //
                .ja3_abnormal_dst_org_for_src_netname_outbound()  //
                //     .ja3_abnormal_ssl_subject_day_time()
                .abnormal_ja3_for_source_netname_outbound()
                .ja3_abnormal_country_for_ssl_subject_outbound()
                .ja3_abnormal_ssl_subject_for_ja3_outbound();

        list.add(ja3_1);
        return list;

    }
}
