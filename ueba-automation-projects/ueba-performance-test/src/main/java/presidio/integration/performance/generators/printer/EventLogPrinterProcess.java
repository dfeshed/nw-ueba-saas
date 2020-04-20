package presidio.integration.performance.generators.printer;

import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;

import java.io.PrintWriter;
import java.util.List;

/**
 * Prints one ProcessEvent to a given file (printer writer) in log decoder format
 * **/
public class EventLogPrinterProcess extends EventLogPrinter {
    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";

    EventLogPrinterProcess() {
        schema = "process";
        logsPath = EVENTS_LOGS_PATH + "endpoint/";
    }

    void print(Event event, PrintWriter writer) {
        ProcessEvent processEvent = (ProcessEvent) event;

        writer.print("CEF:0|RSA|endpoint|11.3|Process event|process event|9|category=Process Event");
        writer.print(" event.source.id=" + processEvent.getEventId());
        writer.print(" event.time=" + processEvent.getDateTime().toString().replace("T"," ").replace("Z",""));
        writer.print(" device.type=nwendpoint");
        writer.print(" user.src=" + processEvent.getUser().getUserId());
        writer.print(" action=" + NamesConversionUtils.revertProcessOperationType(processEvent.getProcessOperation().getOperationType().getName()));
        writer.print(" alias.host=" + processEvent.getMachineEntity().getMachineId());
        if (processEvent.getMachineEntity().getOwner() != null) { writer.print(" owner=" + processEvent.getMachineEntity().getOwner()); }
        writer.print(" directory.src=" + processEvent.getProcessOperation().getSourceProcess().getProcessDirectory().replace("\\","\\\\"));
        writer.print(" filename.src=" + processEvent.getProcessOperation().getSourceProcess().getProcessFileName());
        printDirGroups(" dir.path.src=", processEvent.getProcessOperation().getSourceProcess().getProcessDirectoryGroups(), writer);
        printDirGroups(" dir.path.dst=", processEvent.getProcessOperation().getDestinationProcess().getProcessDirectoryGroups(), writer);

        writer.print(" cert.common=" + processEvent.getProcessOperation().getSourceProcess().getProcessCertificateIssuer());
        writer.print(" directory.dst=" + processEvent.getProcessOperation().getDestinationProcess().getProcessDirectory().replace("\\","\\\\"));
        writer.print(" filename.dst=" + processEvent.getProcessOperation().getDestinationProcess().getProcessFileName());

        // Fields for fake links to "investigate", not used by ADE
        writer.print(" nwe.callback_id=" + "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b");
        writer.print(" checksum.src=" + "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57");
        writer.print(" checksum.dst=" + "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57");
        writer.print(" process.vid.src=-363757471601907552");

        writer.print(" agent.id=" + "807E600E-FBBB-E21B-FB17-7E44BFB6186B");
        writer.print(" os=" + processEvent.getMachineEntity().getOsVersion());

        writer.println();
    }

    private void printDirGroups(String meta, List<String> groups, PrintWriter writer) {
        if (groups == null || groups.isEmpty()) return;
        for (String group : NamesConversionUtils.revertDirGroups(groups)) {
            writer.print(meta + group);
        }
    }
}
