package com.rsa.netwitness.presidio.automation.converter.conveters.authentication;

import com.rsa.netwitness.presidio.automation.converter.events.CefHeader;
import com.rsa.netwitness.presidio.automation.converter.events.WindowsEvent;
import fortscale.common.general.Schema;
import org.assertj.core.util.Lists;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.random.RandomListElementGenerator;

import java.util.List;


class NetwitnessWindowsAuthenticationEventBuilder extends WindowsEvent {

    private static final List<String> logonType4625 = Lists.newArrayList("2", "3", "10");
    private static final List<String> logonType4624 = Lists.newArrayList("2", "10");

    private RandomListElementGenerator<String> logonType4625Gen = new RandomListElementGenerator<>(logonType4625);
    private RandomListElementGenerator<String> logonType4624Gen = new RandomListElementGenerator<>(logonType4624);


    private final AuthenticationEvent event;
    private String eventType;
    private String eventDesc;


    NetwitnessWindowsAuthenticationEventBuilder(AuthenticationEvent event) {
        super(event.getDateTime(), Schema.AUTHENTICATION);
        this.event = event;
    }

    private CefHeader getCefHeader() {
        String cefVendor = "Microsoft";
        String cefProduct = "Windows Snare";
        return new CefHeader(cefVendor, cefProduct, eventType, eventDesc);
    }


    private void commonWindowsModifier() {
        user_dst = event.getUser().getUserId();
        result_code = event.getResultCode();
        device_type = "winevent_nic";
        event_source_id = event.getEventId();
        event_type = event.getResult();
    }

    NetwitnessWindowsAuthenticationEventBuilder getWin_4769() {
        commonWindowsModifier();
        reference_id = "4769";
        this.eventType = getWindowsAuthEventType();
        this.eventDesc = "a kerberos service ticket was requested.";
        service_name = "WIN-PY3ZJZTXPIL$";

        /** optional */
        ip_src = event.getSrcMachineEntity().getMachineIp();
        medium = "32";
        header_id = "0004";
        event_source = "Microsoft-Windows-Security-Auditing";
        event_user = event.getUser().getUserId();
        event_computer = event.getUser().getUserId();
        category = "Logon";
        domain = event.getSrcMachineEntity().getMachineDomain();
        netname = "private src";
        msg_id = "security_4769_microsoft-windows-security-auditing";
        event_cat_name = "System.Normal Conditions.Services";
        device_disc = "83";
        did = "nw-06-endpoint";
        rid = "67869";

        cefHeader = getCefHeader();
        return this;
    }


    NetwitnessWindowsAuthenticationEventBuilder getWin_4624() {
        commonWindowsModifier();
        this.eventType = "Success Audit";
        this.eventDesc = "An account was successfully logged on.";
        reference_id = "4624";
        alias_host = Lists.newArrayList(event.getSrcMachineEntity().getMachineId());
        logon_type = putLogonType(logonType4624Gen);


        /** optional */
        ip_src = event.getSrcMachineEntity().getMachineIp();
        event_source = "Microsoft-Windows-Security-Auditing";
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        event_user = event.getUser().getUsername();
        obj_name = "Impersonation";
        domain = "NT AUTHORITY";
        obj_type = "Impersonation Level";
        msg_id = "security_4624_microsoft-windows-security-auditing";
        device_disc = "78";
        did = " nw-06-endpoint";
        event_cat_name = "User.Activity.Successful Logins";
        process = "Advapi";
        ec_theme = "Authentication";
        ec_subject = "User";
        ec_activity = "Logon";
        ec_outcome = "Success";

        cefHeader = getCefHeader();
        return this;
    }

    NetwitnessWindowsAuthenticationEventBuilder getWin_4625() {
        commonWindowsModifier();
        this.eventType = "Failure Audit";
        this.eventDesc = "An account failed to log on.";
        reference_id = "4625";
        alias_host = Lists.newArrayList(event.getSrcMachineEntity().getMachineId());
        logon_type = putLogonType(logonType4625Gen);

        /** optional */
        ip_src = event.getSrcMachineEntity().getMachineIp();
        event_source = "Microsoft-Windows-Security-Auditing";
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        event_user = event.getUser().getUsername();
        result = "Unknown user name or bad password.";
        context = "0xc0000064";
        netname = "private src";
        process = "NtLmSsp";
        ec_theme = "Authentication";
        ec_subject = "User";
        ec_activity = "Logon";
        ec_outcome = "Failure";

        msg_id = "security_4625_microsoft-windows-security-auditing";
        event_cat_name = "User.Activity.Failed Logins";

        cefHeader = getCefHeader();
        return this;
    }


    NetwitnessWindowsAuthenticationEventBuilder getWin_4648() {
        commonWindowsModifier();
        reference_id = "4648";
        this.eventType = getWindowsAuthEventType();
        this.eventDesc = "a logon was attempted using explicit credentials.";
        host_dst = event.getDstMachineEntity().getMachineId();
        alias_host = Lists.newArrayList(event.getSrcMachineEntity().getMachineId());

        /** optional */
        ip_src = event.getSrcMachineEntity().getMachineIp();
        device_ip = event.getDstMachineEntity().getMachineIp();
        medium = "32";
        header_id = "1001";
        event_source = "Microsoft-Windows-Security-Auditing";
        event_user = event.getUser().getUsername();
        event_computer = event.getSrcMachineEntity().getMachineId();
        category = "Logon";
        domain = "CORP";
        user_src = event.getUser().getEmail();
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

        cefHeader = getCefHeader();
        return this;
    }


    private String putLogonType(RandomListElementGenerator<String> defaultValues) {
        /** Operations 4625 and 4624 have mandatory logon_type. If it not filled, the event will be filtered by adapter/
         * For scenario events with ...REMOTE_COMPUTER... operation types it will be 10,
         * for all other operation types - from defaultValues
         * Set rsaacesrv Logon type to be Logon
         **/
        String operationType = event.getAuthenticationOperation().getOperationType().getName();
        if (operationType.equals("USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER")
                || operationType.equals("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER")
                || operationType.equals("REMOTE_INTERACTIVE")) {
            return "10";
        }
        if (operationType.equals("INTERACTIVE")) {
            return "2";
        }
        if (operationType.equals("NETWORK")) {
            return "3";
        }

        return defaultValues.getNext();
    }

    private String getWindowsAuthEventType() {
        return (event.getResult().equalsIgnoreCase("success")) ? "Success Audit" : "Failure Audit";
    }

}
