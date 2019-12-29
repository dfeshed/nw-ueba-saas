package com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.SslSubjectTlsAlert;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SslSubjectHighBytesSentAlerts implements Supplier<Stream<TlsAlert>> {
    private final int dataPeriod;
    private final int uncommonStartDay;
    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();

    public SslSubjectHighBytesSentAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    @Override
    public Stream<TlsAlert> get() {
        return alertsHolder.getOrCompute(this::create).stream();
    }

    private List<TlsAlert> create() {
        List<TlsAlert> list = Lists.newLinkedList();


        TlsAlert ssl_subject_10 = new SslSubjectTlsAlert("adobe systems incorporated", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound()
                .ssl_subject_abnormal_ja3_day_time()
                .ssl_subject_abnormal_ssl_subject_day_time()
                .create();


        TlsAlert ssl_subject_11 = new SslSubjectTlsAlert("oath inc", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_domain_ssl_subject_outbound()
                .create();


        TlsAlert ssl_subject_12 = new SslSubjectTlsAlert("*.crashlytics.com", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_13 = new SslSubjectTlsAlert("netflix, inc.", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound()
                .create();


        TlsAlert ssl_subject_17 = new SslSubjectTlsAlert("*.opswat.com", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_18 = new SslSubjectTlsAlert("samsung electronics co. ltd", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_14 = new SslSubjectTlsAlert("outbrain inc.", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_16 = new SslSubjectTlsAlert("ipass inc.", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_ssl_subject_outbound()
                .create();



        TlsAlert ssl_subject_19 = new SslSubjectTlsAlert("bytes new ssl subject", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_to_new_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_20 = new SslSubjectTlsAlert("bytes src ip new ssl subject", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_src_ip_to_new_ssl_subject_outbound()
                .create();

        TlsAlert ssl_subject_21 = new SslSubjectTlsAlert("distinct src ip new entity and ssl subject", dataPeriod, uncommonStartDay)
                .high_number_of_distinct_src_ip_for_new_entity()
                .create();

        TlsAlert ssl_subject_22 = new SslSubjectTlsAlert("new occurrences history", dataPeriod, uncommonStartDay)
                .new_occurrences_historical_data()
                .create();

        TlsAlert ssl_subject_23 = new SslSubjectTlsAlert("distinct src ip new ssl subject", dataPeriod, uncommonStartDay)
                .high_number_of_distinct_src_ip_for_new_ssl_subject()
                .create();

        list.add(ssl_subject_10);
        list.add(ssl_subject_11);
        list.add(ssl_subject_12);
        list.add(ssl_subject_13);
        list.add(ssl_subject_14);
        list.add(ssl_subject_16);
        list.add(ssl_subject_17);
        list.add(ssl_subject_18);
        list.add(ssl_subject_19);
        list.add(ssl_subject_20);
        list.add(ssl_subject_21);
        list.add(ssl_subject_22);
        list.add(ssl_subject_23);

        return list;
    }


}

