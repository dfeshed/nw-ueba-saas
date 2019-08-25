package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.events.LinuxEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import static org.assertj.core.util.Arrays.array;


class NetwitnessLinuxAuthenticationEventBuilder extends LinuxEvent {

    private final AuthenticationEvent event;

    private static StringCyclicValuesGenerator rhlinuxEventTypeGen = new StringCyclicValuesGenerator(array("USER_LOGIN","CRED_ACQ", "USER_AUTH"));
    private static StringCyclicValuesGenerator rhlinuxActionGen = new StringCyclicValuesGenerator(array("/usr/sbin/sshd", "/usr/bin/login"));


    NetwitnessLinuxAuthenticationEventBuilder(AuthenticationEvent event) {
        event_time = eventTimeFormatter.format(event.getDateTime());
        schema = Schema.AUTHENTICATION;
        eventTimeEpoch = event.getEventTime();
        this.event = event;
    }

    NetwitnessLinuxAuthenticationEventBuilder getRhlinux() {
        cefVendor = "Unix";
        cefProduct = "RedHat Linux";
        device = "rhlinux";
        group = "Unix";
        cefEventType = rhlinuxEventTypeGen.getNext();
        cefEventDesc = event.getDstMachineEntity().getDomainFQDN() + " " +cefEventType;

        ip_src =  event.getSrcMachineEntity().getMachineIp();
        lc_cid = "axinsavlc1";
        forward_ip = "127.0.0.1";
        device_ip =  event.getDstMachineEntity().getMachineIp();
        medium = "32";
        header_id = "2010";
        client = "audispd";
        alias_host =  event.getDstMachineEntity().getDomainFQDN();
        msg = "audispd:node:sadasfdsaffsd.central.test.grp";
        type = "CRED_ACQ";
        msg_id = "03810";
        alias_host$1 =  event.getDstMachineEntity().getDomainFQDN();
        user_src =  event.getUser().getUserId();
        action =  rhlinuxActionGen.getNext(); // /usr/sbin/sshd | /usr/bin/login
        host_src =  event.getSrcMachineEntity().getDomainFQDN();
        net_block = "ES_HQ_ARTEIXO_SERVIDORES_INTERNOS";
        net_subnet = "ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE";
        dst_net_block = "ES_HQ_ARTEIXO_SERVIDORES_INTERNOS";
        dst_net_subnet = "ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE";
        process = "ssh";
        result = event.getResult().toLowerCase();
        event_cat_name = "System_Audit";
        device_disc = "98";
        device_disc_type = "rhlinux";
        alert = "InternalSRCIP";
        did = "axinsadec1";
        rid = "164247018869";
        ip_all = "127.0.0.1";
        ip_all$1 = "10.110.93.164";
        host_all = event.getDstMachineEntity().getMachineDomain();
        host_all$1 =  event.getDstMachineEntity().getDomainFQDN();
        user_all$2 = event.getUser().getUserId();
        host_all$3 = "axpreglfs1n01.central.test.grp";
        ip_all$2 = "10.110.93.161";

        return this;
    }

}
