package com.rsa.netwitness.presidio.automation.data.preparation.tls.scenarios;

import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.Ja3TlsAlert;
import com.rsa.netwitness.presidio.automation.data.preparation.tls.model.TlsAlert;
import presidio.data.domain.event.network.TlsEvent;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Stream;

public class AlertsSpecialCases {
    private final int dataPeriod;
    private final int uncommonStartDay;

    public AlertsSpecialCases(int dataPeriod, int uncommonStartDay) {
        this.dataPeriod = dataPeriod;
        this.uncommonStartDay = uncommonStartDay;
    }


    public Stream<TlsEvent> generateAll() {
        return Stream.of(
                createJa3AlertOnlyNullSslSubjects(),
                createJa3AlertContainNullSslSubjects()
        ).flatMap(e -> e);
    }


    private Stream<TlsEvent> createJa3AlertOnlyNullSslSubjects() {

        TlsAlert ja3AlertWithOnlyNullSslSubjects = new Ja3TlsAlert("cf9d0d62f54f43d3a008nullsslsubjectall", dataPeriod, uncommonStartDay)
                .ja3_abnormal_domain_for_ja3_outbound()
                .ja3_abnormal_dst_port_for_dst_org_outbound()
                .ja3_abnormal_dst_org_for_src_netname_outbound()
                .ja3_abnormal_ja3_for_source_netname_outbound()
                .create();

        return ja3AlertWithOnlyNullSslSubjects.getIndicators().parallelStream()
                .flatMap(indicator -> indicator.generateEvents().stream())
                .peek(event -> event.setSslSubject(null));

    }

    private Stream<TlsEvent> createJa3AlertContainNullSslSubjects() {
        Function<Double, Boolean> needToModify = probability -> ThreadLocalRandom.current().nextDouble() < probability;

        TlsAlert ja3AlertWithOnlyNullSslSubjects = new Ja3TlsAlert("cf9d0d62f54f43d3a008nullsslsubjectpart", dataPeriod, uncommonStartDay)
                .ja3_abnormal_dst_port_for_dst_org_outbound()
                .ja3_abnormal_dst_port_for_domain_outbound()
                .ja3_abnormal_domain_for_src_netname_outbound()
                .create();

        return ja3AlertWithOnlyNullSslSubjects.getIndicators().parallelStream()
                .flatMap(indicator -> indicator.generateEvents().stream())
                .peek(event -> { if (needToModify.apply(0.6)) event.setSslSubject(null); } );
    }


}

