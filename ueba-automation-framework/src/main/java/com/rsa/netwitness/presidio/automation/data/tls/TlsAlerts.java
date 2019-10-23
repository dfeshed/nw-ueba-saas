package com.rsa.netwitness.presidio.automation.data.tls;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.model.Ja3TlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.SslSubjectTlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;

public class TlsAlerts implements Supplier<List<TlsAlert>>  {
    private final int dataPeriod;
    private final int uncommonStartDay;
    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();

    public TlsAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    @Override
    public List<TlsAlert> get() {
        return alertsHolder.getOrCompute(this::create);
    }

    private List<TlsAlert> create() {
        List<TlsAlert> list = Lists.newLinkedList();


        TlsAlert ssl_subject_10 = new SslSubjectTlsAlert("ssl_subject_10", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound()
                .ssl_subject_abnormal_ja3_day_time()
                .ssl_subject_abnormal_ssl_subject_day_time()
                .create();

        TlsAlert ssl_subject_11 = new SslSubjectTlsAlert("ssl_subject_11", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_domain_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();




        TlsAlert ssl_subject_12 = new SslSubjectTlsAlert("ssl_subject_12", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();

        TlsAlert ssl_subject_13 = new SslSubjectTlsAlert("ssl_subject_13", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();




        TlsAlert ssl_subject_14 = new SslSubjectTlsAlert("ssl_subject_14", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();

        TlsAlert ssl_subject_15 = new SslSubjectTlsAlert("ssl_subject_15", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();


        TlsAlert ssl_subject_16 = new SslSubjectTlsAlert("ssl_subject_16", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_ssl_subject_outbound()
                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
                .create();


        TlsAlert ja3_10 = new Ja3TlsAlert("ja3_10", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_ja3_outbound()
                .create();

        list.add(ssl_subject_10);
        list.add(ssl_subject_11);
        list.add(ssl_subject_12);
        list.add(ssl_subject_13);
        list.add(ssl_subject_14);
        list.add(ssl_subject_15);
        list.add(ssl_subject_16);

//
//
//        TlsAlert ja3_1 = new Ja3TlsAlert("cf9d0d62f54f43d3a0073ea42d94c88", dataPeriod, uncommonStartDay)
//                .ja3_abnormal_domain_for_ja3_outbound()
//                // .abnormal_ja3_day_time()   //
//                .ja3_abnormal_dst_port_for_dst_org_outbound()    //
//                .ja3_abnormal_dst_org_for_src_netname_outbound()  //
//                //     .ja3_abnormal_ssl_subject_day_time()
//                .ja3_abnormal_ja3_for_source_netname_outbound()
//                .ja3_abnormal_country_for_ssl_subject_outbound()
//                .ja3_abnormal_ssl_subject_for_ja3_outbound()
//                .create();
//
//        TlsAlert ja3_2 = new Ja3TlsAlert("8c0caf5183294b18be514094d231139", dataPeriod, uncommonStartDay)
//                .ja3_abnormal_dst_port_for_domain_outbound()
//                .ja3_abnormal_ssl_subject_for_src_netname_outbound()
//                .ja3_abnormal_domain_for_src_netname_outbound()
//                .ja3_abnormal_dst_port_for_src_netname_outbound()
//                // .abnormal_ja3_day_time()   //
//                //     .ja3_abnormal_ssl_subject_day_time()
//                .ja3_abnormal_dst_port_for_ssl_subject_outbound()
//                .ja3_abnormal_dst_port_for_ja3_outbound()
//                .create();
//
//        TlsAlert ja3_3 = new Ja3TlsAlert("606ed7c3e0a54dbc8b6925750607f04", dataPeriod, uncommonStartDay)
//                .ja3_abnormal_ja3_day_time()
//                .ja3_abnormal_dst_port_for_domain_outbound()
//                .ja3_abnormal_ssl_subject_for_src_netname_outbound()
//                .create();
//
//        TlsAlert ja3_4 = new Ja3TlsAlert("7e2779d3d28747d488721a84aa15906", dataPeriod, uncommonStartDay)
//                .ja3_abnormal_ssl_subject_day_time()
//                .ja3_abnormal_dst_port_for_ssl_subject_outbound()
//                .ja3_abnormal_dst_port_for_ja3_outbound()
//                .create();
//
//
//        list.add(ja3_1);
//        list.add(ja3_2);
//        list.add(ja3_3);
//        list.add(ja3_4);
//
//
//        TlsAlert ssl_subject_1 = new SslSubjectTlsAlert("4.sophosxl.net", dataPeriod, uncommonStartDay)
//                //.ssl_subject_abnormal_country_for_ssl_subject_outbound()
//                .ssl_subject_abnormal_domain_for_ja3_outbound()
//                //.ssl_subject_abnormal_domain_for_src_netname_outbound()
//                .ssl_subject_abnormal_dst_org_for_src_netname_outbound()
//                .ssl_subject_abnormal_ja3_for_source_netname_outbound()
//                .ssl_subject_abnormal_ssl_subject_for_ja3_outbound()
//                //.ssl_subject_abnormal_ssl_subject_for_src_netname_outbound()
//               // .ssl_subject_abnormal_dst_port_for_ssl_subject_outbound()
//                //.ssl_subject_abnormal_dst_port_for_src_netname_outbound()
//                //.ssl_subject_abnormal_dst_port_for_ja3_outbound()
//                //.ssl_subject_abnormal_dst_port_for_dst_org_outbound()
//                .ssl_subject_abnormal_dst_port_for_domain_outbound()
//                .create();
//
//        TlsAlert ssl_subject_2 = new SslSubjectTlsAlert("trend micro inc.", dataPeriod, uncommonStartDay)
//                .ssl_subject_abnormal_country_for_ssl_subject_outbound()
//                .ssl_subject_abnormal_domain_for_src_netname_outbound()
//                .ssl_subject_abnormal_ssl_subject_for_src_netname_outbound()
//                .ssl_subject_abnormal_dst_port_for_src_netname_outbound()
//                .create();
//
//
//        TlsAlert ssl_subject_3 = new SslSubjectTlsAlert("bitdefender srl", dataPeriod, uncommonStartDay)
//                .ssl_subject_abnormal_dst_port_for_ssl_subject_outbound()
//                .ssl_subject_abnormal_dst_port_for_ja3_outbound()
//                .ssl_subject_abnormal_dst_port_for_dst_org_outbound()
//                .create();
//
//
//        TlsAlert ssl_subject_4 = new SslSubjectTlsAlert("industria de diseno textil sa", dataPeriod, uncommonStartDay)
//                .ssl_subject_abnormal_ssl_subject_day_time()
//                .ssl_subject_abnormal_ja3_day_time()
//                .ssl_subject_abnormal_domain_for_ja3_outbound()
//                .create();
//
//        TlsAlert ssl_subject_5 = new SslSubjectTlsAlert("industria de diseno textil sa", dataPeriod, uncommonStartDay)
//                .ssl_subject_abnormal_ja3_day_time()
//                .ssl_subject_abnormal_domain_for_ja3_outbound()
//                .ssl_subject_abnormal_dst_port_for_dst_org_outbound()
//                .create();
//
//        list.add(ssl_subject_1);
//        list.add(ssl_subject_2);
//        list.add(ssl_subject_3);
//        list.add(ssl_subject_4);
//        list.add(ssl_subject_5);

        return list;
    }


}
