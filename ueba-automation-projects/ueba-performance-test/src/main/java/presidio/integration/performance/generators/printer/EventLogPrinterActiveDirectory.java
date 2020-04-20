package presidio.integration.performance.generators.printer;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;

import java.io.PrintWriter;

/**
 * Prints one ActiveDirectoryEvent to a given file (printer writer) in log decoder format
 *
 * Each event has constant hard coded string format, where we substitute only values used by UEBA modeling engine.
 * This allows to test the flow as close as possible to real event logs processing.
 * The variety of messages can be extended, if needed.
 * Not all event details are substituted with generated values, but mostly the values that participate in modeling.
 * **/
public class EventLogPrinterActiveDirectory extends EventLogPrinter {

    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";

    EventLogPrinterActiveDirectory() {
        schema = "active_directory";
        logsPath = EVENTS_LOGS_PATH + "windows/";
    }

    void print(Event event, PrintWriter writer) {

        ActiveDirectoryEvent activeDirectoryEvent = (ActiveDirectoryEvent) event;

        switch (chooseReferenceId(activeDirectoryEvent, "NA")) {
            case "4794" : { writer.println(build4794(activeDirectoryEvent)); break; }
            case "4742" : { writer.println(build4742(activeDirectoryEvent)); break; }
            case "4741" : { writer.println(build4741(activeDirectoryEvent)); break; }
            case "4743" : { writer.println(build4743(activeDirectoryEvent)); break; }
            case "5376" : { writer.println(build5376(activeDirectoryEvent)); break; }
            case "5377" : { writer.println(build5377(activeDirectoryEvent)); break; }
            case "5136" : { writer.println(build5136(activeDirectoryEvent)); break; }
            case "4739" : { writer.println(build4739(activeDirectoryEvent)); break; }
            case "4764" : { writer.println(build4764(activeDirectoryEvent)); break; }
            case "4728" : { writer.println(build4728(activeDirectoryEvent)); break; }
            case "4732" : { writer.println(build4732(activeDirectoryEvent)); break; }
            case "4756" : { writer.println(build4756(activeDirectoryEvent)); break; }
            case "4729" : { writer.println(build4729(activeDirectoryEvent)); break; }
            case "4733" : { writer.println(build4733(activeDirectoryEvent)); break; }
            case "4757" : { writer.println(build4757(activeDirectoryEvent)); break; }
            case "4670" : { writer.println(build4670(activeDirectoryEvent)); break; }
            case "4727" : { writer.println(build4727(activeDirectoryEvent)); break; }
            case "4730" : { writer.println(build4730(activeDirectoryEvent)); break; }
            case "4731" : { writer.println(build4731(activeDirectoryEvent)); break; }
            case "4734" : { writer.println(build4734(activeDirectoryEvent)); break; }
            case "4735" : { writer.println(build4735(activeDirectoryEvent)); break; }
            case "4754" : { writer.println(build4754(activeDirectoryEvent)); break; }
            case "4758" : { writer.println(build4758(activeDirectoryEvent)); break; }
            case "4717" : { writer.println(build4717(activeDirectoryEvent)); break; }
            case "4738" : { writer.println(build4738(activeDirectoryEvent)); break; }
            case "4720" : { writer.println(build4720(activeDirectoryEvent)); break; }
            case "4726" : { writer.println(build4726(activeDirectoryEvent)); break; }
            case "4725" : { writer.println(build4725(activeDirectoryEvent)); break; }
            case "4722" : { writer.println(build4722(activeDirectoryEvent)); break; }
            case "4740" : { writer.println(build4740(activeDirectoryEvent)); break; }
            case "4767" : { writer.println(build4767(activeDirectoryEvent)); break; }
            case "4723" : { writer.println(build4723(activeDirectoryEvent)); break; }
            case "4724" : { writer.println(build4724(activeDirectoryEvent)); break; }

            // 4757 - commented out in the converter.
        }
    }

    private String build4724(ActiveDirectoryEvent event) {

        String computer = NamesConversionUtils.fixMachineName(event.getSrcMachineEntity().getMachineId());
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4724", event.getEventTime(), computer, user, operationResult );

        stringBuilder.append(",Logon,,An attempt was made to reset an account's password. Subject: Security ID:  ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y ");

        return stringBuilder.toString();

    }

    private String build4723(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4723", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,An attempt was made to change an account's password. Subject: Security ID:  ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Additional Information: Privileges -");

        return stringBuilder.toString();
    }

    private String build4767(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4767", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was unlocked. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y");

        return stringBuilder.toString();
    }

    private String build4740(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4740", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was locked out. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Account That Was Locked Out: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Additional Information: Caller Computer Name: WIN-R9H529RIO4Y");

        return stringBuilder.toString();
    }

    private String build4722(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4722", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was enabled. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);

        return stringBuilder.toString();
    }

    private String build4725(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4725", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was disabled. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Domain: Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4726(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4726", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was deleted. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Account: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4720(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4720", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was changed. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79");
        stringBuilder.append(" New Account: Security ID: ACME-FR\\");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Domain:  ACME Attributes: SAM Account Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Display Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" User Principal Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append("@acme-fr.local Home Directory: - Home Drive: - " +
                "Script Path: - Profile Path: - User Workstations: - Password Last Set: - Account Expires:  - Primary Group ID: - AllowedToDelegateTo: - " +
                "Old UAC Value:  0x85 New UAC Value:  0x84 User Account Control: Account Enabled User Parameters: - SID History: - Logon Hours: - DNS Host Name: - " +
                "Service Principal Names: - Additional Information: Privileges: -");

        return stringBuilder.toString();
    }

    private String build4738(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4738", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A user account was changed. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79");
        stringBuilder.append(" Target Account: Security ID: ACME-FR\\");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Domain:  ACME");
        stringBuilder.append(" Changed Attributes: SAM Account Name: - Display Name: - User Principal Name: - Home Directory: - Home Drive: - " +
                "Script Path: - Profile Path: - User Workstations: - Password Last Set: - Account Expires:  - Primary Group ID: - AllowedToDelegateTo: - " +
                "Old UAC Value:  0x85 New UAC Value:  0x84 User Account Control: Account Enabled");
        stringBuilder.append(" User Parameters: - SID History: - Logon Hours: - DNS Host Name: - Service Principal Names: - Additional Information: Privileges: -");

        return stringBuilder.toString();
    }

    private String build4717(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetAccount = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4717", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,System security access was granted to an account. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Account Modified: Account Name: ");
        stringBuilder.append(targetAccount);
        stringBuilder.append(" Access Granted: Access Right:  SeNetworkLogonRight ");

        return stringBuilder.toString();
    }

    private String build4758(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetGroup = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4758", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled universal group was deleted. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Deleted Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Group Name: ");
        stringBuilder.append(targetGroup);
        stringBuilder.append(" Group Domain: ACME Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4754(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetGroup = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4754", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled universal group was created. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 New Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Group Name: ");
        stringBuilder.append(targetGroup);
        stringBuilder.append(" Group Domain: ACME Attributes: SAM Account Name: ");
        stringBuilder.append(targetGroup);
        stringBuilder.append(" SID History:  - Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4731(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetGroup = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4731", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled local group was created. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 New Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Group Name: ");
        stringBuilder.append(targetGroup);
        stringBuilder.append(" Group Domain: ACME Attributes: SAM Account Name: SalesReps SID History:  - Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4730(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetGroup = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4730", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled global group was deleted. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Deleted Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Group Name: ");
        stringBuilder.append(targetGroup);
        stringBuilder.append(" Group Domain: ACME Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4727(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String objectName = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4727", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append("A security-enabled global group was created. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79");
        stringBuilder.append(" New Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1108 Group Name: ");
        stringBuilder.append(objectName);
        stringBuilder.append(" Group Domain:  ACME Attributes: SAM Account Name: Historical Figures SID History:  - Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4670(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String objectName = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4670", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,Permissions on an object were changed. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Object: Object Server: Security Object Type: File Object Name: ");
        stringBuilder.append(objectName);
        stringBuilder.append(" Handle ID: 0x564 Process: Process ID: 0x8c0  Process Name: C:\\Windows\\explorer.exe Permissions Change: Original Security Descriptor: D:PAI(A;;FA;;;LA)(A;;FA;;;SY)   (A;;FA;;;BA) New Security Descriptor: D:PARAI(A;;FA;;;SY)(A;;FA;;;BA)");

        return stringBuilder.toString();
    }

    private String build4757(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4732", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A member was removed from a security-enabled universal group. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79");
        stringBuilder.append(" Member: Security ID: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name: xyz  Group: Security ID: BUILTIN\\Users  Group Name:  Users Group Domain:  Builtin Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4729(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String domainDN = event.getSrcMachineEntity().getMachineDomainDN();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4732", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A member was removed from security-enabled global group. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x27a79");
        stringBuilder.append(" Member: Security ID: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(domainDN);
        stringBuilder.append(" Group: Security ID: BUILTIN\\Users  Group Name:  Users Group Domain:  Builtin Additional Information: Privileges: - ");

        return stringBuilder.toString();
    }

    private String build4756(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4732", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A member was added to a security-enabled universal group. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x27a79");
        stringBuilder.append(" Member: Security ID: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name:  cn=Ghenghis Khan,CN=Users,DC=acme,DC=local Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1108 Group Name:  Historical Figures Group Domain:  ACME Additional Information: Privileges: - Expiration time:");

        return stringBuilder.toString();
    }

    private String build4732(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4732", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A member was added to a security-enabled local group. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x27a79");
        stringBuilder.append(" Member: Security ID: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name:  - Group: Security ID: BUILTIN\\Users  Group Name:  Users Group Domain:  Builtin Additional Information: Privileges: - Expiration time:");

        return stringBuilder.toString();
    }

    private String build4728(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4728", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,An account was successfully logged on. A member was added to a security-enabled global group. Subject: Security ID: ACME\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x27a79");
        stringBuilder.append(" Member: Security ID: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Name:  cn=Ghenghis Khan,CN=Users,DC=acme,DC=local Group: Security ID:  S-1-5-21-3108364787-189202583-342365621-1108 Group Name:  Historical Figures Group Domain:  ACME Additional Information: Privileges: - Expiration time:");

        return stringBuilder.toString();
    }

    private String build4764(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String group = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4764", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A group’s type was changed. Subject: Security ID:  SYSTEM Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x30999");
        stringBuilder.append(" Change Type: Security Enabled Global Group Changed to Security Enabled Universal Group. Group: Security ID:  ACME\\Dharma Institute Employees Group Name: ");
        stringBuilder.append(group);
        stringBuilder.append(" Domain:  ACME Additional Information: Privileges: -");

        return stringBuilder.toString();
    }

    private String build4739(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String computer2 = event.getDstMachineEntity().getMachineId(); // check if this correct
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4739", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,Domain Policy was changed. Change Type:  Lockout Policy modified. Subject: Security ID:  SYSTEM Account Name: ");
        stringBuilder.append(computer2);
        stringBuilder.append(" Account Domain: WORKGROUP Logon ID: 0x27a79");
        stringBuilder.append(" Domain: Domain Name:  WIN-R9H529RIO4Y Domain ID:  ACME\\ Changed Attributes: Min. Password Age: - Max. Password Age: - Force Logoff:  - Lockout Threshold: 7 Lockout Observation Window: 1800 Lockout Duration: 1800 Password Properties: - Min. Password Length: - Password History Length: - Machine Account Quota: - Mixed Domain Mode: - Domain Behavior Version: - OEM Information: - Additional Information: Privileges:  -");

        return stringBuilder.toString();
    }

    private String build5136(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String domain = event.getSrcMachineEntity().getMachineDomain();
        String domainDN = event.getSrcMachineEntity().getMachineDomainDN();
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("5136", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,Edit Of A Group Policy Object A directory service object was modified. Subject: Security ID:  ");
        stringBuilder.append(domain);
        stringBuilder.append("\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x27a79 Directory Service: Name: ");
        stringBuilder.append(domainDN);
        stringBuilder.append(" Type: Active Directory Domain Services Object: DN: cn={0AB54C97-8836-43BB-9B53-87556DD51F30},cn=policies,cn=system,DC=acme,DC=com GUID: CN={0AB54C97-8836-43BB-9B53-87556DD51F30},CN=Policies,CN=System,DC=acme,DC=com Class: groupPolicyContainer Attribute: LDAP Display Name: versionNumber Syntax (OID): 2.5.5.9 Value: 4 Operation: Type: Value Added Correlation ID: {ff320a1e-447a-4bb1-9196-bb3469a00b55}  Application Correlation ID: -");

        return stringBuilder.toString();
    }

    private String build5377(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("5377", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,Credential Manager credentials were restored from a backup. Subject: Security ID:  WIN-R9H529RIO4Y\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x27a79 BackupFileName: xyz");
        stringBuilder.append(" This event occurs when a user restores his Credential Manager credentials from a backup. A user (even an Administrator) cannot restore the credentials of an account other than his own.");

        return stringBuilder.toString();
    }

    private String build5376(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("5376", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,Credential Manager credentials were backed up. Subject: Security ID:  WIN-R9H529RIO4Y\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x27a79 BackupFileName: xyz");
        stringBuilder.append(" This event occurs when a user backs up their own Credential Manager credentials. A user (even an Administrator) cannot back up the credentials of an account other than his own.");

        return stringBuilder.toString();
    }

    private String build4743(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String targetComputer = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4743", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A computer account was deleted. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 Target Computer: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Account Name:");
        stringBuilder.append(targetComputer);
        stringBuilder.append(" Account Domain: Additional Information: Privileges: - ");

        return stringBuilder.toString();

    }

    private String build4741(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4741", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A computer account was created. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79 New Computer Account: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Account Name:");
        stringBuilder.append(computer);
        stringBuilder.append(" Account Domain:  ACME Attributes: SAM Account Name: ");

        return stringBuilder.toString();
    }

    private String build4742(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String user_src = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4742", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A computer account was changed. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user_src);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x27a79");
        stringBuilder.append(" Computer Account That Was Changed: Security ID:  S-1-5-21-3108364787-189202583-342365621-1109 Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain:  ACME");
        stringBuilder.append(" Changed Attributes: SAM Account Name: - Display Name: - User Principal Name: - Home Directory: - Home Drive: - " +
                "Script Path: - Profile Path: - User Workstations: - Password Last Set: - Account Expires:  - Primary Group ID: - AllowedToDelegateTo: - " +
                "Old UAC Value:  0x85 New UAC Value:  0x84 User Account Control: Account Enabled ");
        stringBuilder.append("User Parameters: - SID History: - Logon Hours: - DNS Host Name: - Service Principal Names: - Additional Information: Privileges: -");

        return stringBuilder.toString();
    }

    private String build4794(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4794", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,An attempt was made to set the Directory Services Restore Mode administrator password. Subject: Security ID:  ACME\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ACME Logon ID: 0x21a95 Additional Information: Caller Workstation: ");
        stringBuilder.append(computer);
        stringBuilder.append(" Status Code: 0x0");

        return stringBuilder.toString();
    }

    private String build4733(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String group = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4733", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A member was removed from a security-enabled local group. Subject: Security ID: WIN-R9H529RIO4Y\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x1fd23 Member: Security ID: WIN-R9H529RIO4Y\\bob Account Name: - Group: Security ID: BUILTIN\\Users Group Name: ");
        stringBuilder.append(group);
        stringBuilder.append(" Group Domain: Builtin Additional Information: Privileges: -");
        return stringBuilder.toString();
    }

    private String build4734(ActiveDirectoryEvent event) {

        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String group = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4734", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled local group was deleted. ");
        stringBuilder.append("Subject: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(user);
        stringBuilder.append(" Account Name:");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x1fd23 Group: Security ID: S-1-5-21-3108364787-189202583-342365621-1001 Group Name: ");
        stringBuilder.append(group);
        stringBuilder.append(" Group Domain: WIN-R9H529RIO4Y Additional Information: Privileges: -");

        return stringBuilder.toString();
    }

    private String build4735(ActiveDirectoryEvent event) {
        String computer = event.getSrcMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String group = event.getObjectName();
        String operationResult = event.getOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart("4735", event.getEventTime(), computer, user, operationResult);

        stringBuilder.append(",Logon,,A security-enabled local group was changed.");
        stringBuilder.append("Subject: Security ID: WIN-R9H529RIO4Y\\");
        stringBuilder.append(user);
        stringBuilder.append(" Account Name:");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x1fd23 Group: Security ID: S-1-5-21-3108364787-189202583-342365621-1001 Group Name: ");
        stringBuilder.append(group);
        stringBuilder.append(" Group Domain: WIN-R9H529RIO4Y Changed Attributes: SAM Account Name: - SID History: - Additional Information: Privileges: -");

        return stringBuilder.toString();
    }


    public static String chooseReferenceId(ActiveDirectoryEvent event, String customReferenceId) {
        String operationType = event.getOperation().getOperationType().getName();
        // noinspection IfCanBeSwitch
        if      (operationType.equals(AD_OPERATION_TYPE.ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD.value)) return("4794");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CHANGED.value)) return("4742");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_CREATED.value)) return("4741");
        if (operationType.equals(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DELETED.value)) return("4743");
        if (operationType.equals(AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP.value)) return("5376");
        if (operationType.equals(AD_OPERATION_TYPE.CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP.value)) return("5377");
        if (operationType.equals(AD_OPERATION_TYPE.DIRECTORY_SERVICE_OBJECT_MODIFIED.value)) return("5136");
        if (operationType.equals(AD_OPERATION_TYPE.DOMAIN_POLICY_CHANGED.value)) return("4739");
        if (operationType.equals(AD_OPERATION_TYPE.GROUP_TYPE_CHANGED.value)) return("4764");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP.value)) return("4728");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP.value)) return("4732");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP.value)) return("4756");
//        else if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_GROUP.value)) return("4757"); /** CA operation type - from generated events scenario **/
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP.value)) return("4729");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP.value)) return("4733");
        if (operationType.equals(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP.value)) return("4757");
        if (operationType.equals(AD_OPERATION_TYPE.PERMISSIONS_ON_OBJECT_CHANGED.value)) return("4670");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_CREATED.value)) return("4727");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_GLOBAL_GROUP_DELETED.value)) return("4730");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CREATED.value)) return("4731");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_DELETED.value)) return("4734");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_LOCAL_GROUP_CHANGED.value)) return("4735");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED.value)) return("4754");
        if (operationType.equals(AD_OPERATION_TYPE.SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED.value)) return("4758");
        if (operationType.equals(AD_OPERATION_TYPE.SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT.value)) return("4717");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_CHANGED.value)) return("4738");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_CREATED.value)) return("4720");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_DELETED.value)) return("4726");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value)) return("4725");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value)) return("4722");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value)) return("4740");
        if (operationType.equals(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value)) return("4767");
        if (operationType.equals(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value)) return("4723");
        if (operationType.equals(AD_OPERATION_TYPE.USER_PASSWORD_RESET.value)) return("4724");
        return customReferenceId;
    }
}

