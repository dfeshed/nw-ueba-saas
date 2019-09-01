package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.LinuxEvent;
import fortscale.common.general.Schema;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.random.RandomListElementGenerator;

import java.util.List;


class NetwitnessLinuxAuthenticationEventBuilder extends LinuxEvent {

    private final AuthenticationEvent event;
    private static final List<String> rhlinuxEventType = Lists.newArrayList("USER_LOGIN", "CRED_ACQ", "USER_AUTH");
    private static final List<String> rhlinuxAction = Lists.newArrayList("/usr/sbin/sshd", "/usr/bin/login");

    private RandomListElementGenerator<String> rhlinuxEventTypeGen = new RandomListElementGenerator<>(rhlinuxEventType);
    private RandomListElementGenerator<String> rhlinuxActionGen = new RandomListElementGenerator<>(rhlinuxAction);

    NetwitnessLinuxAuthenticationEventBuilder(AuthenticationEvent event) {
        super(event.getDateTime(), Schema.AUTHENTICATION);
        this.event = event;
        cefHeader = getCefHeader();
    }

    NetwitnessLinuxAuthenticationEventBuilder getRhlinux() {
        user_dst = event.getUser().getUserId();
        result_code = event.getResultCode();
        device_type = "rhlinux";
        sessionid = event.getEventId();
        action = rhlinuxActionGen.getNext(); // /usr/sbin/sshd | /usr/bin/login
        user_src = event.getUser().getUserId();
        event_type = event.getAuthenticationOperation().getOperationType().getName();
        result = event.getResult().toLowerCase();
        host_src = event.getSrcMachineEntity().getMachineId();

        group = "Unix";
        ip_src = event.getSrcMachineEntity().getMachineIp();
        lc_cid = "axinsavlc1";
        forward_ip = "127.0.0.1";
        device_ip = event.getDstMachineEntity().getMachineIp();
        medium = "32";
        header_id = "2010";
        client = "audispd";
        msg = "audispd:node:sadasfdsaffsd.central.test.grp";
        type = "CRED_ACQ";
        msg_id = "03810";
        net_block = "ES_HQ_ARTEIXO_SERVIDORES_INTERNOS";
        net_subnet = "ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE";
        dst_net_block = "ES_HQ_ARTEIXO_SERVIDORES_INTERNOS";
        dst_net_subnet = "ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE";
        process = "ssh";
        event_cat_name = "System_Audit";
        device_disc = "98";
        device_disc_type = "rhlinux";
        alert = "InternalSRCIP";
        did = "axinsadec1";
        rid = "164247018869";

        return this;
    }

    private CefHeader getCefHeader() {
        String cefVendor = "Unix";
        String cefProduct = "RedHat Linux";
        String eventType = rhlinuxEventTypeGen.getNext();
        String eventDesc = event.getDstMachineEntity().getDomainFQDN() + " " + eventType;
        return new CefHeader(cefVendor, cefProduct, eventType, eventDesc);
    }

}
