package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.events.WindowsEvent;
import fortscale.common.general.Schema;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import static org.assertj.core.util.Arrays.array;


class NetwitnessWindowsAuthenticationEventBuilder extends WindowsEvent {

    private final AuthenticationEvent event;

    NetwitnessWindowsAuthenticationEventBuilder(AuthenticationEvent event) {
        event_time = eventTimeFormatter.format(event.getDateTime());
        schema = Schema.AUTHENTICATION;
        eventTimeEpoch = event.getEventTime();
        this.event = event;
    }

    private static StringCyclicValuesGenerator rhlinuxEventTypeGen = new StringCyclicValuesGenerator(array("USER_LOGIN","CRED_ACQ", "USER_AUTH"));
    private static StringCyclicValuesGenerator rhlinuxActionGen = new StringCyclicValuesGenerator(array("/usr/sbin/sshd", "/usr/bin/login"));

    private static StringCyclicValuesGenerator logonType4625 = new StringCyclicValuesGenerator(array("2","3","10"));
    private static StringCyclicValuesGenerator logonType4624 = new StringCyclicValuesGenerator(array("2","10"));


    NetwitnessWindowsAuthenticationEventBuilder getWin_4769() {
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



    NetwitnessWindowsAuthenticationEventBuilder getWin_4624() {
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

    NetwitnessWindowsAuthenticationEventBuilder getWin_4625() {
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


    NetwitnessWindowsAuthenticationEventBuilder getWin_4648() {
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
