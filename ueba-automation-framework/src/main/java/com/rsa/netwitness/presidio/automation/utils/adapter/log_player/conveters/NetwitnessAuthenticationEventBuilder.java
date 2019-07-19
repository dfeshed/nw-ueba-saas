package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.conveters;

import fortscale.common.general.Schema;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.NetwitnessEvent;


class NetwitnessAuthenticationEventBuilder extends NetwitnessEvent {

    private final AuthenticationEvent event;

    NetwitnessAuthenticationEventBuilder(AuthenticationEvent event) {
        event_time = eventTimeFormatter.format(event.getDateTime());
        schema = Schema.AUTHENTICATION;
        eventTimeEpoch = event.getEventTime();
        this.event = event;
    }

    private static StringCyclicValuesGenerator rhlinuxEventTypeGen = new StringCyclicValuesGenerator(new String[] {"USER_LOGIN","CRED_ACQ", "USER_AUTH"});
    private static StringCyclicValuesGenerator rhlinuxActionGen = new StringCyclicValuesGenerator(new String[] {"/usr/sbin/sshd", "/usr/bin/login"});

    private static StringCyclicValuesGenerator logonType4625 = new StringCyclicValuesGenerator(new String[] {"2","3","10"});
    private static StringCyclicValuesGenerator logonType4624 = new StringCyclicValuesGenerator(new String[] {"2","10"});


    NetwitnessAuthenticationEventBuilder getWin_4769() {
        commonWindowsModifier();
        reference_id = "4769";
        ip_src = event.getSrcMachineEntity().getMachineIp();
        medium = "32";
        header_id = "0004";
        event_source = "Microsoft-Windows-Security-Auditing";
        event_user = event.getUser().getUserId();
        cefEventType = getWindowsAuthEventType();
        event_computer = event.getUser().getUserId();
        category = "Logon";
        cefEventDesc = "a kerberos service ticket was requested.";
        user_dst = event.getUser().getEmail();
        domain = event.getSrcMachineEntity().getMachineDomain();
        service_name = "WIN-PY3ZJZTXPIL$";
        netname = "private src";
        // result_code = event.getResultCode();
        msg_id = "security_4769_microsoft-windows-security-auditing";
        event_cat_name = "System.Normal Conditions.Services";
        device_disc = "83";
        did = "nw-06-endpoint";
        rid = "67869";
        return this;
    }



    NetwitnessAuthenticationEventBuilder getWin_4624() {
        commonWindowsModifier();
        reference_id = "4624";
        alias_host = event.getSrcMachineEntity().getMachineId();

        ip_src = event.getSrcMachineEntity().getMachineIp();
        event_source = "Microsoft-Windows-Security-Auditing";
        cefEventType = "Success Audit";
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        cefEventDesc = "An account was successfully logged on.";
        event_user = event.getUser().getUsername();
        logon_type = putLogonType(logonType4624);
        obj_name = "Impersonation";
        user_dst = "SYSTEM";
        domain = "NT AUTHORITY";

        obj_type = "Impersonation Level";
        msg_id = "security_4624_microsoft-windows-security-auditing";
        device_disc = "78";
        did = " nw-06-endpoint";

        event_cat_name = "User.Activity.Successful Logins";
        host_src = "";
        process = "Advapi";
        ec_theme = "Authentication";
        ec_subject = "User";
        ec_activity = "Logon";
        ec_outcome = "Success";

        return this;
    }

    NetwitnessAuthenticationEventBuilder getWin_4625() {
        commonWindowsModifier();
        reference_id = "4625";
        alias_host = event.getSrcMachineEntity().getMachineId();

        ip_src = event.getSrcMachineEntity().getMachineIp();
        event_source = "Microsoft-Windows-Security-Auditing";
        cefEventType = "Failure Audit";
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        cefEventDesc = "An account failed to log on.";
        event_user = event.getUser().getUsername();
        logon_type = putLogonType(logonType4625);
        user_dst = event.getUser().getUsername();
        result = "Unknown user name or bad password.";
        // result_code = "0xc000006d";
        context = "0xc0000064";
        host_src = event.getSrcMachineEntity().getMachineId();
        netname = "private src";
        process = "NtLmSsp";
        ec_theme = "Authentication";
        ec_subject = "User";
        ec_activity = "Logon";
        ec_outcome = "Failure";

        msg_id = "security_4625_microsoft-windows-security-auditing";
        event_cat_name = "User.Activity.Failed Logins";

        return this;
    }


    NetwitnessAuthenticationEventBuilder getWin_4648() {
        commonWindowsModifier();
        reference_id = "4648";
        ip_src = event.getSrcMachineEntity().getMachineIp();
        device_ip = event.getDstMachineEntity().getMachineIp();
        medium = "32";
        header_id = "1001";
        alias_host = event.getSrcMachineEntity().getMachineId();
        event_source = "Microsoft-Windows-Security-Auditing";
        event_user = event.getUser().getUsername();
        cefEventType = getWindowsAuthEventType();
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        cefEventDesc = "a logon was attempted using explicit credentials.";
        user_dst = event.getUser().getUsername();
        domain = "CORP";
        user_src = event.getUser().getEmail();
        host_dst = event.getDstMachineEntity().getMachineId();
        process = "c:\\program files\\microsoft office\\root\\office16\\lync.exe";
        netname = "private src";
        ec_theme = "Authentication";
        ec_subject = "User";
        ec_activity = "Logon";
        msg_id = "security_4648_microsoft-windows-security-auditing";
        event_cat_name = "User.Activity";
        device_disc = "78";
        did = "nw-06-endpoint";
        rid = "3612765";

        return this;
    }


    NetwitnessAuthenticationEventBuilder getRhlinux() {
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


    private void commonWindowsModifier(){
        cefVendor = "Microsoft";
        cefProduct = "Windows Snare";

        sessionid = event.getEventId();
        user_dst = event.getUser().getUserId();
        result_code =  event.getResultCode();
        event_source_id =  event.getEventId();
        device_type = "winevent_snare";

    }

    private String putLogonType(StringCyclicValuesGenerator defaultValues) {
        /** Operations 4625 and 4624 have mandatory logon_type. If it not filled, the event will be filtered by adapter/
         * For scenario events with ...REMOTE_COMPUTER... operation types it will be 10,
         * for all other operation types - from defaultValues
         * Set rsaacesrv Logon type to be Logon
         **/
        String operationType = event.getAuthenticationOperation().getOperationType().getName();
        if (operationType.equals("USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER") ||
                operationType.equals("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER"))
            return "10";
        else return defaultValues.getNext();
    }

    private String getWindowsAuthEventType() {
       if (event.getResult().equalsIgnoreCase("success")) return "Success Audit";
       else return "Failure Audit";
    }

}
