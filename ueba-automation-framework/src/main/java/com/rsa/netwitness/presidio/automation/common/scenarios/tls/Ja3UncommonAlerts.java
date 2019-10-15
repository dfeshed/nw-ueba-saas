package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.model.Ja3TlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Ja3UncommonAlerts implements Supplier<Stream<TlsAlert>> {
    private final int dataPeriod;
    private final int uncommonStartDay;
    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();

    public Ja3UncommonAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    @Override
    public Stream<TlsAlert> get() {
        return alertsHolder.getOrCompute(this::create).stream();
    }

    private List<TlsAlert> create() {
        List<TlsAlert> list = Lists.newLinkedList();


        TlsAlert ja3_1 = new Ja3TlsAlert("cf9d0d62f54f43d3a0073ea42d94c88", dataPeriod, uncommonStartDay)
                .ja3_abnormal_domain_for_ja3_outbound()
                .ja3_abnormal_dst_port_for_dst_org_outbound()    //
                .ja3_abnormal_dst_org_for_src_netname_outbound()  //
                .ja3_abnormal_ja3_for_source_netname_outbound()
                .ja3_abnormal_country_for_ssl_subject_outbound()
                .ja3_abnormal_ssl_subject_for_ja3_outbound()
                .create();

        TlsAlert ja3_2 = new Ja3TlsAlert("8c0caf5183294b18be514094d231139", dataPeriod, uncommonStartDay)
                .ja3_abnormal_dst_port_for_domain_outbound()
                .ja3_abnormal_ssl_subject_for_src_netname_outbound()
                .ja3_abnormal_domain_for_src_netname_outbound()
                .ja3_abnormal_dst_port_for_src_netname_outbound()
                .ja3_abnormal_dst_port_for_ssl_subject_outbound()
                .ja3_abnormal_dst_port_for_ja3_outbound()
                .create();

        TlsAlert ja3_3 = new Ja3TlsAlert("606ed7c3e0a54dbc8b6925750607f04", dataPeriod, uncommonStartDay)
                .ja3_abnormal_ja3_day_time()
                .ja3_abnormal_dst_port_for_domain_outbound()
                .ja3_abnormal_ssl_subject_for_src_netname_outbound()
                .create();

        TlsAlert ja3_4 = new Ja3TlsAlert("7e2779d3d28747d488721a84aa15906", dataPeriod, uncommonStartDay)
                .ja3_abnormal_ssl_subject_day_time()
                .ja3_abnormal_dst_port_for_ssl_subject_outbound()
                .ja3_abnormal_dst_port_for_ja3_outbound()
                .create();


        list.add(ja3_1);
        list.add(ja3_2);
        list.add(ja3_3);
        list.add(ja3_4);


        return list;
    }


}

