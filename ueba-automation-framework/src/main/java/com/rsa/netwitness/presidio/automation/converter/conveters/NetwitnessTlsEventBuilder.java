package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;


class NetwitnessTlsEventBuilder extends NetwitnessEvent {

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
        country_dst = networkEvent.getDstLocation() != null ? networkEvent.getDstLocation().getCountry() : null;
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = String.valueOf(networkEvent.getNumOfBytesSent());
        payload_res = String.valueOf(networkEvent.getNumOfBytesReceived());
        netname = networkEvent.getSourceNetname() != null ? networkEvent.getSourceNetname().concat(" src") : null;
        netname$1 = networkEvent.getDestinationNetname() != null ? networkEvent.getDestinationNetname().concat(" dst") : null;
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection().value;
        tcp_dstport = String.valueOf(networkEvent.getDestinationPort());
        ja3s = networkEvent.getJa3s();
        network = networkEvent.getDataSource();
        country_src = networkEvent.getSrcLocation() != null ? networkEvent.getSrcLocation().getCountry() : null;
        alias_host =  arrayLengthMatch(networkEvent.getFqdn(), 0) ? networkEvent.getFqdn().get(0) : null;
        alias_host$1 = arrayLengthMatch(networkEvent.getFqdn(), 1) ? networkEvent.getFqdn().get(1) : null;
        alias_host$2 = arrayLengthMatch(networkEvent.getFqdn(), 2) ? networkEvent.getFqdn().get(2) : null;
        ssl_ca = networkEvent.getSslCa();
        session_split = String.valueOf(networkEvent.getSessionSplit());
        analysis_service = String.valueOf(networkEvent.getIsSelfSigned());
        service_name = "443";
    }

    private boolean arrayLengthMatch(List list, int index) {
        return (list != null) && list.size() >= index-1;
    }
}
