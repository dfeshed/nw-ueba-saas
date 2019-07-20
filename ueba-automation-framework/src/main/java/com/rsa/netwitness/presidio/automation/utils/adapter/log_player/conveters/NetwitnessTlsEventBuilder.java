package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters;

import fortscale.common.general.Schema;
import presidio.data.domain.event.network.NetworkEvent;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.NetwitnessEvent;


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
        country_dst = networkEvent.getDstLocation().getCountry();
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = String.valueOf(networkEvent.getNumOfBytesSent());
        payload_res = String.valueOf(networkEvent.getNumOfBytesReceived());
        netname = networkEvent.getSourceNetname().concat(" src");
        netname$1 = networkEvent.getDestinationNetname().concat(" dst");
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection().value;
        tcp_dstport = String.valueOf(networkEvent.getDestinationPort());
        ja3s = networkEvent.getJa3s();
        network = networkEvent.getDataSource();
        country_src = networkEvent.getSrcLocation().getCountry();
        alias_host = networkEvent.getFqdn().get(0);
        alias_host$1 = networkEvent.getFqdn().get(1);
        alias_host$2 = networkEvent.getFqdn().get(2);
        ssl_ca = networkEvent.getSslCa();
        session_split = String.valueOf(networkEvent.getSessionSplit());
        analysis_service = String.valueOf(networkEvent.getIsSelfSigned());
        service_name = "443";
    }
}
