package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.google.common.collect.Lists;
import fortscale.common.general.Schema;
import presidio.data.domain.event.network.NetworkEvent;
import com.rsa.netwitness.presidio.automation.converter.events.MongoTlsEvent;

public class MongoTlsEventBuilder extends MongoTlsEvent {

    private NetworkEvent networkEvent;

    MongoTlsEventBuilder getTlsRawEvent() {

        ip_src = networkEvent.getSourceIp();
        ip_dst = networkEvent.getDstIp();
        country_dst = networkEvent.getDstLocation().getCountry();
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = networkEvent.getNumOfBytesSent();
        payload_res = networkEvent.getNumOfBytesReceived();
        netname = Lists.newArrayList(networkEvent.getSourceNetname().concat(" src"),
                networkEvent.getDestinationNetname().concat(" dst"));
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection().value;
        tcp_dstport = networkEvent.getDestinationPort();
        ja3s = networkEvent.getJa3s();
        country_src = networkEvent.getSrcLocation().getCountry();
        alias_host = networkEvent.getFqdn();
        ssl_ca = Lists.newArrayList(networkEvent.getSslCa());
        session_split = networkEvent.getSessionSplit();
        analysis_service = networkEvent.getIsSelfSigned();
        network = networkEvent.getDataSource();
        return this;
    }


    MongoTlsEventBuilder(NetworkEvent event) {
        this.networkEvent = event;

        dateTime = networkEvent.getDateTime();
        data_source = networkEvent.getDataSource();
        mongo_source_event_time = networkEvent.getDateTime();
        time =  String.valueOf(event.getDateTime().toEpochMilli());
        event_source_id = event.getEventId();
    }

    @Override
    public Schema mongoSchema() {
         return Schema.TLS;
    }

}
