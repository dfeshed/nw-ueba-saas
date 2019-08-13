package com.rsa.netwitness.presidio.automation.converter.conveters;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.converter.events.MongoTlsEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.network.NetworkEvent;

import java.util.List;

public class MongoTlsEventBuilder extends MongoTlsEvent {

    private NetworkEvent networkEvent;

    MongoTlsEventBuilder getTlsRawEvent() {

        ip_src = networkEvent.getSourceIp();
        ip_dst = networkEvent.getDstIp();
        country_dst = networkEvent.getDstLocation() != null ? networkEvent.getDstLocation().getCountry() : null;
        ssl_subject = networkEvent.getSslSubject();
        org_dst = networkEvent.getDestinationOrganization();
        asn_dst = networkEvent.getDestinationASN();
        payload_req = networkEvent.getNumOfBytesSent();
        payload_res = networkEvent.getNumOfBytesReceived();
        netname = setNetname();
        ja3 = networkEvent.getJa3();
        direction = networkEvent.getDirection() != null ? networkEvent.getDirection().value : null;
        tcp_dstport = networkEvent.getDestinationPort();
        tcp_srcport = networkEvent.getSourcePort();
        ja3s = networkEvent.getJa3s();
        country_src = networkEvent.getSrcLocation() != null ? networkEvent.getSrcLocation().getCountry() : null;
        alias_host = networkEvent.getFqdn();
        ssl_ca = setSslCa();
        session_split = networkEvent.getSessionSplit();
        analysis_service = networkEvent.getIsSelfSigned();
        network = networkEvent.getDataSource();
        return this;
    }

    private List<String> setSslCa() {
        if (networkEvent.getSslCa() != null) {
            return Lists.newArrayList(networkEvent.getSslCa());
        }
        return null;
    }

    MongoTlsEventBuilder(NetworkEvent event) {
        this.networkEvent = event;

        dateTime = networkEvent.getDateTime();
        data_source = networkEvent.getDataSource();
        mongo_source_event_time = networkEvent.getDateTime();
        time = String.valueOf(event.getDateTime().toEpochMilli());
        event_source_id = event.getEventId();
    }

    private List<String> setNetname() {
        if (networkEvent.getSourceNetname() == null) {
            return null;
        } else {
            return Lists.newArrayList(networkEvent.getSourceNetname().concat(" src"),
                    networkEvent.getDestinationNetname().concat(" dst"));
        }
    }


    @Override
    public Schema mongoSchema() {
        return Schema.TLS;
    }

}
