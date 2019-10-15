package presidio.integration.performance.generators.printer;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import java.io.PrintWriter;

/**
 * Prints one FileEvent to a given file (printer writer) in log decoder format
 *
 * Each event has hard coded string format, where we substitute only values used by UEBA modeling engine.
 * This allows to test the flow as close as possible to real event logs processing.
 * The variety of messages can be extended, if needed.
 * **/
public class EventLogPrinterFile extends EventLogPrinter {

    private static final String[] fileOpenedReferenceIds = new String[]{"4663", "5145"};
    private IStringGenerator fileOpenedReferenceIdGenerator;
    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";

    EventLogPrinterFile() {
        schema = "file";
        logsPath = EVENTS_LOGS_PATH + "windows/";
    }

    void print(Event event, PrintWriter writer) {

        fileOpenedReferenceIdGenerator = new StringCyclicValuesGenerator(fileOpenedReferenceIds);
        FileEvent fileEvent = (FileEvent) event;

        switch (fileEvent.getFileOperation().getOperationType().getName().toUpperCase().replaceAll(" ","_")) {
            case "FILE_DELETED" :{
                writer.println(build4660(fileEvent));
                break;
            }
            case "FOLDER_DELETED" : {
                writer.println(build4660(fileEvent));
                break;
            }
            case "FOLDER_ACCESS_RIGHTS_CHANGED" :
            case "FOLDER_CLASSIFICATION_CHANGED" :
            case "FILE_OWNERSHIP_CHANGED" : {
                writer.println(build4670(fileEvent));
                break;
            }
            default:
            {   String referenceId = fileOpenedReferenceIdGenerator.getNext();
                if (referenceId.equalsIgnoreCase("5145")) { writer.print(build5145(fileEvent)); }
                else { writer.println(build4663(fileEvent)); }
            }
        }
    }

    private String build4670(FileEvent event) {
        String referenceId = "4670";
        String computer = event.getMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String fileName = event.getFileOperation().getSourceFile().getAbsoluteFilePath();
        String operationResult = event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart(referenceId, event.getDateTime(), computer, user, operationResult);

        // TODO: find how to differentiate the event per operation type
        // see examples: https://docs.microsoft.com/en-us/windows/security/threat-protection/auditing/event-4670
        stringBuilder.append(",File System,,Permissions on an object were changed. Subject: Security ID: WIN-R9H529RIO4Y\\Administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: WIN-R9H529RIO4Y Logon ID: 0x3DE02 Object: Object Server: Security Object Type: File Object Name: ");
        stringBuilder.append(fileName);
        stringBuilder.append(" Handle ID: 0x178 Process: Process ID: 0x113c Process Name: C:\\Windows\\explorer.exe ");
        stringBuilder.append(" Permissions Change: Original Security Descriptor: D:PAI(A;;FA;;;LA)(A;;FA;;;SY) (A;;FA;;;BA) New Security Descriptor: D:PARAI(A;;FA;;;SY)(A;;FA;;;BA) ");

        return stringBuilder.toString();
    }

    private String build4660(FileEvent event) {
        String referenceId = "4660";
        String computer = event.getMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String operationResult = event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart(referenceId, event.getDateTime(), computer, user, operationResult);

        stringBuilder.append(",File System,,An object was deleted. Subject: Security ID: ");
        stringBuilder.append(computer);
        stringBuilder.append("\\");
        stringBuilder.append(user);
        stringBuilder.append(" Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(computer);
        stringBuilder.append(" Logon ID: 0x3DE02 Object: Object Server: Security Handle ID: 0x178 Process Information: Process ID: 0x113c Process Name: C:\\Windows\\System32\\cmd.exe Transaction ID: {00000000-0000-0000-0000-000000000000}");

        return stringBuilder.toString();
    }

    private String build4663(FileEvent event) {

        String referenceId = "4663";
        String computer = event.getMachineEntity().getMachineId();
        String user = event.getUser().getUserId();
        String fileName = event.getFileOperation().getSourceFile().getAbsoluteFilePath();
        String operationResult = event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart(referenceId, event.getDateTime(), computer, user, operationResult);

        stringBuilder.append(",File System,,An attempt was made to access an object. Subject: Security ID: LB\\administrator Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: LB Logon ID: 0x3DE02 Object: Object Server: Security Object Type: File Object Name: ");
        stringBuilder.append(fileName);
        stringBuilder.append(" Handle ID: 0x178 Resource Attributes: S:AI Process Information: Process ID: 0x113c Process Name: C:\\Windows\\System32\\notepad.exe");
        stringBuilder.append(getAccesses4663(event.getFileOperation().getOperationType().getName()));

        return stringBuilder.toString();
    }

    private String build5145(FileEvent event) {
        String referenceId = "5145";
        String computer = event.getMachineEntity().getMachineId();
        String domain = event.getMachineEntity().getMachineDomain(); //? {group}
        String user = event.getUser().getUserId();
        String fileName = event.getFileOperation().getSourceFile().getAbsoluteFilePath();
        String operationResult = event.getFileOperation().getOperationResult().equalsIgnoreCase("SUCCESS")?"SUCCESS":"FAILURE";

        StringBuilder stringBuilder = buildCommonPart(referenceId, event.getDateTime(), computer, user, operationResult);
        stringBuilder.append(",File System,,A network share object was checked to see whether client can be granted desired access. Subject: Security ID: SYSTEM Account Name: ");
        stringBuilder.append(user);
        stringBuilder.append(" Account Domain: ");
        stringBuilder.append(domain);
        stringBuilder.append(" Logon ID: 0x86d584 Network Information: Object Type: File System Source Address: fe80::507a:5bf7:2a72:c046 Source Port: 55490 Share Information: Share Name: \\\\*\\SYSVOL Share Path: \\??\\C:\\Windows\\SYSVOL\\sysvol Relative Target Name: w8r2.com\\Policies\\{6AC1786C-016F-11D2-945F-00C04fB984F9}\\Machine\\Microsoft\\Windows NT\\Audit\\");
        stringBuilder.append(fileName);
        stringBuilder.append(" Handle ID: 0x178 Resource Attributes: S:AI Process Information: Process ID: 0x113c Process Name: C:\\Windows\\System32\\notepad.exe Access Request Information: Accesses: ");
        stringBuilder.append("Access Request Information: Access Mask: 0x120089 Accesses: READ_CONTROL SYNCHRONIZE ReadData (or ListDirectory) ReadEA ReadAttributes Access Check Results: READ_CONTROL: Granted by Ownership SYNCHRONIZE: Granted by D:(A;;0x1200a9;;;WD) ReadData (or ListDirectory): Granted by D:(A;;0x1200a9;;;WD) ReadEA: Granted by D:(A;;0x1200a9;;;WD) ReadAttributes: Granted by D:(A;;0x1200a9;;;WD)");

        return stringBuilder.toString();
    }

    private String getAccesses4663(String operationTypeName) {
        /** This is minimal implementation of simplest file accesses - one access with it's mask per event, by operation type.
            In real logs, several access types may appear in the same event. In this case the access type strings should be concatenated, access mask should be summed.
            See example in documentation: https://docs.microsoft.com/en-us/windows/security/threat-protection/auditing/event-4663
        * **/
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Access Request Information: Accesses: ");
        switch (operationTypeName.toUpperCase().replaceAll(" ","_")) {
            case "FILE_OPENED":
            case "FOLDER_OPENED": {
                stringBuilder.append("ReadData (or ListDirectory) Access Mask: 0x1");
                break;
            }

            case "FILE_CREATED":
            case "FOLDER_CREATED": {
                stringBuilder.append("WriteData (or AddFile) Access Mask: 0x2");
                break;
            }
            case "FILE_MODIFIED":
            case "FOLDER_MODIFIED": {
                stringBuilder.append("AppendData (or AddSubdirectory or CreatePipeInstance) Access Mask: 0x4");
                break;
            }
            case "FILE_WRITE_DAC_CHANGED":
            case "FOLDER_WRITE_DAC_CHANGED": {
                stringBuilder.append("WRITE_DAC Access Mask: 0x40000");
                break;
            }
            case "FILE_WRITE_OWNERSHIP_CHANGED":
            case "FOLDER_WRITE_OWNERSHIP_CHANGED": {
                stringBuilder.append("WRITE_OWNER Access Mask: 0x80000");
                break;
            }
            default: { // All operation types from scenario that not covered in NW will be converted as following:
                stringBuilder.append("ReadAttributes: 0x80");
                break;
            }
        }
        return stringBuilder.toString();
    }

}

