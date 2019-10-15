package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.data.tls.model.Ja3TlsAlert;
import com.rsa.netwitness.presidio.automation.data.tls.model.TlsAlert;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Ja3HighBytesSentAlerts implements Supplier<Stream<TlsAlert>> {
    private final int dataPeriod;
    private final int uncommonStartDay;
    private Lazy<List<TlsAlert>> alertsHolder = new Lazy<>();

    public Ja3HighBytesSentAlerts(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }

    @Override
    public Stream<TlsAlert> get() {
        return alertsHolder.getOrCompute(this::create).stream();
    }

    private List<TlsAlert> create() {
        List<TlsAlert> list = Lists.newLinkedList();


        TlsAlert ja3_10 = new Ja3TlsAlert("ae87bfd12c6f4b9aa26ef8805ec04bc", dataPeriod, uncommonStartDay)
                .high_number_of_distinct_src_ip_for_ja3_outbound()
                .create();

        TlsAlert ja3_11 = new Ja3TlsAlert("a2751ebbf35e42d89b954d7275c5082", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_ja3_outbound()
                .create();

        TlsAlert ja3_12 = new Ja3TlsAlert("f320b81984cc432fa3aedf04234211e", dataPeriod, uncommonStartDay)
                .high_number_of_bytes_sent_by_ja3_outbound()
                .high_number_of_distinct_src_ip_for_ja3_outbound()
                .create();

        list.add(ja3_10);
        list.add(ja3_11);
        list.add(ja3_12);


        return list;
    }
}

