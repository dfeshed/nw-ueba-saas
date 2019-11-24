package presidio.integration.performance.generators.printer;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static presidio.integration.performance.generators.printer.NamesConversionUtils.fixMachineName;

/**
 * Prints one AuthenticationEvent to a given file (printer writer) in log decoder format
 *
 * Each event has constant hard coded string format, where we substitute only values used by UEBA modeling engine.
 * This allows to test the flow as close as possible to real event logs processing.
 * The variety of messages can be extended, if needed.
 * **/
public class EventLogPrinterAuthentication extends EventLogPrinter {
    private static final String[] successReferenceIds = new String[]{"4769", "4624", "4648","rsaacesrv","rhlinux"};
    private static final String[] failureReferenceIds = new String[]{"4769", "4625", "4648","rsaacesrv","rhlinux"};

    private IStringGenerator successReferenceIdGenerator;
    private IStringGenerator failureReferenceIdGenerator;
    private DateTimeFormatter dateShortFormatter = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));
    private DateTimeFormatter dateLongFormatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss yyyy").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));
    private StringCyclicValuesGenerator rhlinuxEventTypeGen = new StringCyclicValuesGenerator(new String[] {"USER_LOGIN","CRED_ACQ", "USER_AUTH"});
    private StringCyclicValuesGenerator rhlinuxActionGen = new StringCyclicValuesGenerator(new String[] {"/usr/sbin/sshd", "/usr/bin/login"});

    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";

    EventLogPrinterAuthentication() {
        schema = "authentication";
        logsPath = EVENTS_LOGS_PATH + "windows/";
        successReferenceIdGenerator = new StringCyclicValuesGenerator(successReferenceIds);
        failureReferenceIdGenerator = new StringCyclicValuesGenerator(failureReferenceIds);

    }

    void print(Event event, PrintWriter writer) {

        AuthenticationEvent authenticationEvent = (AuthenticationEvent) event;
        String referenceId =  authenticationEvent.getResult().equalsIgnoreCase("success") ?
                successReferenceIdGenerator.getNext() : failureReferenceIdGenerator.getNext();
        switch (referenceId) {
            case "4624" : { writer.println(build4624(authenticationEvent)); break; }
            case "4625" : { writer.println(build4625(authenticationEvent)); break; }
            case "4769" : { writer.println(build4769(authenticationEvent)); break; }
            case "4776" : { writer.println(build4776(authenticationEvent)); break; }
            case "4648" : { writer.println(build4648(authenticationEvent)); break; }
            case "rhlinux" : { writer.println(build_rhlinux(authenticationEvent)); break;}
            // case "rsaacesrv" : { writer.println(""); break; } // TODO: implement event builder - need real event example
        }
    }

    private String build_rhlinux(AuthenticationEvent event) {
        DateTimeFormatter rhlinuxFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withLocale( Locale.getDefault() ).withZone( ZoneId.of("UTC"));

        String event_type =  rhlinuxEventTypeGen.getNext(); // USER_LOGIN|CRED_ACQ|USER_AUTH
        String header = "CEF:0|Unix|RedHat Linux|11.3|"+event_type+"|sadasfdsaffsd.central.test.grp "+event_type+"|9|";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(header);
        Map<String, String> outputVals = new LinkedHashMap<>();

        outputVals.put("event.time", rhlinuxFormatter.format(event.getDateTime()));
        outputVals.put("ip.src", event.getSrcMachineEntity().getMachineIp());
        outputVals.put("lc.cid","axinsavlc1");
        outputVals.put("forward.ip","127.0.0.1");
        outputVals.put("device.ip", event.getDstMachineEntity().getMachineIp());
        outputVals.put("medium","32");
        outputVals.put("header.id","2010");
        outputVals.put("client","audispd");
        outputVals.put("alias.host", event.getDstMachineEntity().getDomainFQDN());
        outputVals.put("msg","audispd:node:sadasfdsaffsd.central.test.grp");
        outputVals.put("type","CRED_ACQ");
        outputVals.put("msg.id","03810");
        outputVals.put("alias.host", event.getDstMachineEntity().getDomainFQDN());
        outputVals.put("user.src", event.getUser().getUserId());
        outputVals.put("action", rhlinuxActionGen.getNext()); // /usr/sbin/sshd | /usr/bin/login
        outputVals.put("host.src", event.getDstMachineEntity().getDomainFQDN());
        outputVals.put("net.block","ES_HQ_ARTEIXO_SERVIDORES_INTERNOS");
        outputVals.put("net.subnet","ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE");
        outputVals.put("dst.net.block","ES_HQ_ARTEIXO_SERVIDORES_INTERNOS");
        outputVals.put("dst.net.subnet","ES_HQ_ARTEIXO_OPENSHIFT_SERVERS_PRE");
        outputVals.put("process","ssh");
        outputVals.put("result","success");
        outputVals.put("event.cat.name","System.Audit");
        outputVals.put("device.disc","98");
        outputVals.put("device.disc.type","rhlinux");
        outputVals.put("alert","InternalSRCIP");
        outputVals.put("did","axinsadec1");
        outputVals.put("rid","164247018869");
        outputVals.put("ip.all","127.0.0.1");
        outputVals.put("ip.all","10.110.93.164");
        outputVals.put("host.all",event.getDstMachineEntity().getMachineDomain());
        outputVals.put("host.all", event.getDstMachineEntity().getDomainFQDN());
        outputVals.put("user.all",event.getUser().getUserId());
        outputVals.put("host.all","axpreglfs1n01.central.test.grp");
        outputVals.put("ip.all","10.110.93.161");

        outputVals.forEach((k,v) -> stringBuilder.append(k + "=" + v + " "));
        return stringBuilder.toString().trim();
    }

    /** Authentication events printer supports 4 different templates, one per event reference id - 4624, 4625, 4769, 4776.
     *  Methods will substitute relevant information from generated event object into each template accordingly.
     * **/
    private String build4624(AuthenticationEvent event) {
        String referenceId = "4624";
        String datetimeShort = dateShortFormatter.format(event.getEventTime());         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(event.getEventTime());          //Tue Jan 01 00:00:00 2019
        String computer = fixMachineName(event.getSrcMachineEntity().getMachineId());
        String user = event.getUser().getUserId();
        String group = fixMachineName(event.getSrcMachineEntity().getMachineDomain());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(computer);
        stringBuilder.append(" MSWinEventLog,1,Security,60578633,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,Success Audit,");
        stringBuilder.append("computer2");
        stringBuilder.append(",Logon,,An account was successfully logged on. Subject: Security ID: S-1-0-0 Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(group);
        stringBuilder.append(" Logon ID: 0x3E7 Logon Type: ");
        stringBuilder.append(logonType(event));
        stringBuilder.append(" Impersonation Level: Impersonation New Logon: Security ID: S-1-5-21-3673323544-2153791353-3985209930-70125 Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: RSA Logon ID: 0x46D93C6E Logon GUID: {0D302C3E-D60E-B37E-3DEE-C2F4E4029871} ");
        stringBuilder.append(" Process Information: Process ID: 0x0 Process Name: - Network Information: Workstation Name: ");
        stringBuilder.append("computer3");
        stringBuilder.append(" Source Network Address: 170.0.181.219 Source Port: 1059 ");
        stringBuilder.append("Detailed Authentication Information: Logon Process: Kerberos Authentication Package: Kerberos Transited Services: - Package Name (NTLM only): - Key Length: 0 This event is generated when a logon session is created. It is generated on the computer that was accessed. The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe. The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network). The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on. The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The impersonation level field indicates the extent to which a process in the logon session can impersonate. The authentication information fields provide detailed information about this specific logon request. - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event. - Transited services indicate which intermediate services have participated in this logon request. - Package name indicates which sub-protocol was used among the NTLM protocols. - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.,60359216");

        return stringBuilder.toString();
    }

    private String build4625(AuthenticationEvent event) {
        /**
         * "4625" : "{datetime} {computer}PC MSWinEventLog,1,Security,28346715,{datetime_full},4625,Microsoft-Windows-Security-Auditing,RSA\\{user},N/A,Failure Audit,{computer}PC,Logon,,An account failed to log on. Subject: Security ID: NULL SID Account Name: - Account Domain: - Logon ID: 0x0 Logon Type: {logontype} Account For Which Logon Failed: Security ID: NULL SID Account Name: {user} Account Domain: Failure Information: Failure Reason: Unknown user name or bad password. Status: 0xc000006d Sub Status: 0xc0000064 Process Information: Caller Process ID: 0x0 Caller Process Name: - Network Information: Workstation Name: {computer} Source Network Address: 10.42.42.201 Source Port: 53176 Detailed Authentication Information: Logon Process: NtLmSsp Authentication Package: NTLM Transited Services: - Package Name (NTLM only): - Key Length: 0 This event is generated when a logon request fails. It is generated on the computer where access was attempted. The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe. The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network). The Process Information fields indicate which account and process on the system requested the logon. The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The authentication information fields provide detailed information about this specific logon request. Transited services indicate which intermediate services have participated in this logon request. Package name indicates which sub-protocol was used among the NTLM protocols Key length indicates the length of the generated session key. This will be 0 if no session key was requested";
         */
        String referenceId = "4625";
        String datetimeShort = dateShortFormatter.format(event.getEventTime());         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(event.getEventTime());          //Tue Jan 01 00:00:00 2019
        String computer = fixMachineName(event.getSrcMachineEntity().getMachineId());
        String user = event.getUser().getUserId();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(computer);
        stringBuilder.append(" MSWinEventLog,1,Security,28346715,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,Failure Audit,");
        stringBuilder.append("computer2");
        stringBuilder.append(",Logon,,An account failed to log on.");
        stringBuilder.append(" Subject: Security ID: NULL SID Account Name: - Account Domain: -");
        stringBuilder.append(" Logon ID: 0x3E7 Logon Type: ");
        stringBuilder.append(logonType(event));
        stringBuilder.append(" Account For Which Logon Failed: Security ID: NULL SID");
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain:");
        stringBuilder.append(" Failure Information: Failure Reason: Unknown user name or bad password. Status: 0xc000006d Sub Status: 0xc0000064 ");
        stringBuilder.append(" Process Information: Caller Process ID: 0x0 Caller Process Name: - Network Information: Workstation Name: ");
        stringBuilder.append("computer3");
        stringBuilder.append(" Source Network Address: 10.42.42.201 Source Port: 53176");
        stringBuilder.append(" Detailed Authentication Information: Logon Process: NtLmSsp Authentication Package: NTLM Transited Services: - Package Name (NTLM only): - Key Length: 0 This event is generated when a logon request fails. It is generated on the computer where access was attempted. The Subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe. The Logon Type field indicates the kind of logon that was requested. The most common types are 2 (interactive) and 3 (network). The Process Information fields indicate which account and process on the system requested the logon. The Network Information fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases. The authentication information fields provide detailed information about this specific logon request. Transited services indicate which intermediate services have participated in this logon request. Package name indicates which sub-protocol was used among the NTLM protocols Key length indicates the length of the generated session key. This will be 0 if no session key was requested");

        return stringBuilder.toString();
    }

    private String build4769(AuthenticationEvent event) {
        /**
         * "4769" : "{datetime} {computer}PC MSWinEventLog,1,Security,28346715,{datetime_full},4769,Microsoft-Windows-Security-Auditing,RSA\\{user},N/A,Success Audit,{computer}PC,Logon,,A Kerberos service ticket was requested. Account Information: Account Name: {user}@ACME.COM Account Domain: ACME.COM Logon GUID: {4a5cfd43-84a6-c32e-b6a3-b634f57eafe7} Service Information: Service Name: WIN-PY3ZJZTXPIL$ Service ID: ACME\\WIN-PY3ZJZTXPIL$ Network Information: Client Address: ::ffff:10.42.42.224 Client Port: 50979 Additional Information: Ticket Options: 0x40810000 Ticket Encryption Type: 0x12 Failure Code: {failurecode} Transited Services: - This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.";
        **/
        String referenceId = "4769";
        String datetimeShort = dateShortFormatter.format(event.getEventTime());         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(event.getEventTime());          //Tue Jan 01 00:00:00 2019
        String computer = fixMachineName(event.getSrcMachineEntity().getMachineId());
        String user = event.getUser().getUserId();
        String failureCode = event.getResultCode();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(computer);
        stringBuilder.append(" MSWinEventLog,1,Security,28346715,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,Failure Audit,");
        stringBuilder.append(computer);
        stringBuilder.append(",Logon,,A Kerberos service ticket was requested.");
        stringBuilder.append(" Account Information: Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append("@ACME.COM Account Domain: ACME.COM Logon GUID: {4a5cfd43-84a6-c32e-b6a3-b634f57eafe7}");
        stringBuilder.append(" Service Information: Service Name: WIN-PY3ZJZTXPIL$ Service ID: ACME\\WIN-PY3ZJZTXPIL$");
        stringBuilder.append(" Network Information: Client Address: ::ffff:10.42.42.224 Client Port: 50979");
        stringBuilder.append(" Additional Information: Ticket Options: 0x40810000 Ticket Encryption Type: 0x12 Failure Code: ");
        stringBuilder.append(failureCode);
        stringBuilder.append(" Transited Services: - This event is generated every time access is requested to a resource such as a computer or a Windows service. The service name indicates the resource to which access was requested. This event can be correlated with Windows logon events by comparing the Logon GUID fields in each event. The logon event occurs on the machine that was accessed, which is often a different machine than the domain controller which issued the service ticket. Ticket options, encryption types, and failure codes are defined in RFC 4120.");

        return stringBuilder.toString();
    }

    private String build4776(AuthenticationEvent event) {
        /** <<< Currently not used by UEBA, values not verified >>>
         * "4776" : "{datetime} {computer}PC MSWinEventLog,1,Security,28346715,{day} {datetime} {year},4776,Microsoft-Windows-Security-Auditing,RSA\{user},N/A,Success Audit,{computer}PC,Logon,,The domain controller attempted to validate the credentials for an account. Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 Logon Account: guest Source Workstation: {computer} Error Code: 0xc0000072,1";
        **/
        String referenceId = "4776";
        String datetimeShort = dateShortFormatter.format(event.getEventTime());         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(event.getEventTime());          //Tue Jan 01 00:00:00 2019
        String computer = fixMachineName(event.getSrcMachineEntity().getMachineId());
        String user = event.getUser().getUserId();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(computer);
        stringBuilder.append(" MSWinEventLog,1,Security,28346715,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,Success Audit,");
        stringBuilder.append(computer);
        stringBuilder.append(",Logon,,The domain controller attempted to validate the credentials for an account. Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0 Logon Account: guest Source Workstation: ");
        stringBuilder.append(computer);
        stringBuilder.append(" Error Code: 0xc0000072,1");

        return stringBuilder.toString();
    }

    private String build4648(AuthenticationEvent event) {
        String referenceId = "4648";
        String datetimeShort = dateShortFormatter.format(event.getEventTime());         //Jan 01 00:00:00
        String datetimeLong  = dateLongFormatter.format(event.getEventTime());          //Tue Jan 01 00:00:00 2019
        String srcMachine = fixMachineName(event.getSrcMachineEntity().getMachineId());
        String dstMachine = fixMachineName(event.getDstMachineEntity().getMachineId());
        String user = event.getUser().getUserId();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datetimeShort);
        stringBuilder.append(" ");
        stringBuilder.append(srcMachine);
        stringBuilder.append(" MSWinEventLog,1,Security,28346715,");
        stringBuilder.append(datetimeLong);
        stringBuilder.append(",");
        stringBuilder.append(referenceId);
        stringBuilder.append(",Microsoft-Windows-Security-Auditing,RSA\\");
        stringBuilder.append(user);
        stringBuilder.append(",N/A,Success Audit,");
        stringBuilder.append(srcMachine);
        stringBuilder.append(",Logon,,A logon was attempted using explicit credentials. Subject: Security ID: RSA\\" + user);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append("Account Domain:  RSA Logon ID:  0x1ba0e Logon GUID:  {00000000-0000-0000-0000-000000000000} Account Whose Credentials Were Used: Account Name:  rsmith@mtg.com Account Domain:  WIN-R9H529RIO4Y Logon GUID:  {00000000-0000-0000-0000-000000000000} Target Server: Target Server Name: ");
        stringBuilder.append(dstMachine);
        stringBuilder.append(" Additional Information: ");
        stringBuilder.append(dstMachine);
        stringBuilder.append(" Process Information: Process ID:  0x77c Process Name:  C:\\Program Files\\Internet Explorer\\iexplore.exe Network Information: Network Address: - Port:   -");
        stringBuilder.append(" This event is generated when a process attempts to log on an account by explicitly specifying that account’s credentials.  This most commonly occurs in batch-type configurations such as scheduled tasks, or when using the RUNAS command.");

        return stringBuilder.toString();
    }

    private static String logonType (AuthenticationEvent event){
        /** Operations 4625 and 4624 have mandatory logon_type.
         * For scenario events with ...REMOTE_COMPUTER... operation types it will be 10,
         * for all other operation types - 2
         **/
        String operationType = event.getAuthenticationOperation().getOperationType().getName();
        return (operationType.equals("USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER") ||
                operationType.equals("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER")) ? "10" : "2";
    }

}

