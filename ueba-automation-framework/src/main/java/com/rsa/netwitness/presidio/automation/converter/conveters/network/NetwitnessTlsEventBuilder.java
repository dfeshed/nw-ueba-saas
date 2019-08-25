package com.rsa.netwitness.presidio.automation.converter.conveters.network;

import com.rsa.netwitness.presidio.automation.converter.events.TlsEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.Location;
import presidio.data.domain.event.network.NetworkEvent;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getListElementOrNull;
import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.getOrNull;


class NetwitnessTlsEventBuilder extends TlsEvent {

    private final NetworkEvent event;

    NetwitnessTlsEventBuilder(NetworkEvent networkEvent) {

        event_time = eventTimeFormatter.format(networkEvent.getDateTime());

        schema = Schema.TLS;
        eventTimeEpoch = networkEvent.getDateTime();
        this.event = networkEvent;
        cefVendor = "RSA";
        cefProduct = "Netwitness";
        cefEventType = "Network";
        cefEventDesc = "TLS";
        device = "tls";
        group = "tls";


        ip_src = networkEvent.getSourceIp();
        ip_dst = networkEvent.getDstIp();
        country_dst = getOrNull(networkEvent.getDstLocation(), Location::getCountry);
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = getOrNull(networkEvent.getNumOfBytesSent(), String::valueOf);
        payload_res = getOrNull(networkEvent.getNumOfBytesReceived(), String::valueOf);
        netname = getOrNull(networkEvent.getSourceNetname(), o -> o.concat(" src"));
        netname$1 = getOrNull(networkEvent.getDestinationNetname(), o -> o.concat(" dst"));
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection().value;
        tcp_dstport = String.valueOf(networkEvent.getDestinationPort());
        tcp_srcport = String.valueOf(networkEvent.getSourcePort());
        ja3s = networkEvent.getJa3s();
        network = networkEvent.getDataSource();
        country_src = getOrNull(networkEvent.getSrcLocation(), Location::getCountry);
        alias_host = getListElementOrNull(networkEvent.getFqdn(), 0);
        alias_host$1 = getListElementOrNull(networkEvent.getFqdn(), 1);
        alias_host$2 = getListElementOrNull(networkEvent.getFqdn(), 2);
        ssl_ca = networkEvent.getSslCa();
        session_split = getOrNull(networkEvent.getSessionSplit(), String::valueOf);
        analysis_service = getOrNull(networkEvent.getIsSelfSigned(), String::valueOf);
        service_name = "443";
    }

}
