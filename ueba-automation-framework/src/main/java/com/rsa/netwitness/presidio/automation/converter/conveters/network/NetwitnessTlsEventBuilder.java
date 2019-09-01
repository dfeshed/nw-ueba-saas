package com.rsa.netwitness.presidio.automation.converter.conveters.network;

import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.TlsEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.Location;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;
import static org.assertj.core.util.Lists.list;


class NetwitnessTlsEventBuilder extends TlsEvent {

    private final NetworkEvent networkEvent;


    NetwitnessTlsEventBuilder(NetworkEvent event) {
        super(event.getDateTime(), Schema.TLS);
        this.networkEvent = event;
        cefHeader = getCefHeader();
    }

    private CefHeader getCefHeader() {
        String cefVendor = "RSA";
        String cefProduct = "Netwitness";
        String eventDesc = "TLS";
        String eventType = "Network";
        return new CefHeader(cefVendor, cefProduct, eventType, eventDesc);
    }


    public NetwitnessTlsEventBuilder getTls() {
        event_source_id = networkEvent.getEventId();
        data_source = networkEvent.getDataSource();
        ip_src = networkEvent.getSourceIp();
        ip_dst = networkEvent.getDstIp();
        country_dst = getOrNull(networkEvent.getDstLocation(), Location::getCountry);
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = networkEvent.getNumOfBytesSent();
        payload_res = networkEvent.getNumOfBytesReceived();
        netname = getNetname();
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection().value;
        tcp_dstport = networkEvent.getDestinationPort();
        tcp_srcport = networkEvent.getSourcePort();
        ja3s = networkEvent.getJa3s();
        network = networkEvent.getDataSource();
        country_src = getOrNull(networkEvent.getSrcLocation(), Location::getCountry);
        alias_host = networkEvent.getFqdn();
        // todo: change to list
        ssl_ca = list(networkEvent.getSslCa());
        session_split = networkEvent.getSessionSplit();
        analysis_service = networkEvent.getIsSelfSigned();
        service_name = "443";

        return this;
    }

    private List<String> getNetname() {
        List<String> stringStream = Stream.of(getOrNull(networkEvent.getSourceNetname(), o -> o.concat(" src")),
                getOrNull(networkEvent.getDestinationNetname(), o -> o.concat(" dst")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (stringStream.isEmpty()) {
            return null;
        } else {
            return stringStream;
        }
    }

}
