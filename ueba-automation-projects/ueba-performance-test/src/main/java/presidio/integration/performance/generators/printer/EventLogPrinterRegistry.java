package presidio.integration.performance.generators.printer;

import edu.emory.mathcs.backport.java.util.Arrays;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.registry.RegistryEvent;

import java.io.PrintWriter;
import java.util.List;

/**
 * Prints one RegistryEvent to a given file (printer writer) in log decoder format
 * **/
public class EventLogPrinterRegistry extends EventLogPrinter {
    public static final String EVENTS_LOGS_PATH = "/var/netwitness/presidio/event_logs/";


    EventLogPrinterRegistry() {
        schema = "registry";
        logsPath = EVENTS_LOGS_PATH + "endpoint/";
    }

    void print(Event event, PrintWriter writer) {
        RegistryEvent registryEvent = (RegistryEvent) event;

        writer.print("CEF:0|RSA|endpoint|11.3|Registry event|registry event|9|category=Registry Event");
        writer.print(" event.source.id=" + registryEvent.getEventId());
        writer.print(" event.time=" + registryEvent.getDateTime().toString().replace("T"," ").replace("Z",""));
        writer.print(" device.type=nwendpoint");
        writer.print(" user.src=" + registryEvent.getUser().getUserId());
        writer.print(" action=" + NamesConversionUtils.revertProcessOperationType(NamesConversionUtils.fixRegistryOperation(registryEvent.getRegistryOperation().getOperationType().getName())));
        writer.print(" alias.host=" + registryEvent.getMachineEntity().getMachineId());
        writer.print(" owner=" + registryEvent.getMachineEntity().getOwner());
        writer.print(" directory.src=" + registryEvent.getRegistryOperation().getProcess().getProcessDirectory().replace("\\","\\\\"));
        writer.print(" filename.src=" + registryEvent.getRegistryOperation().getProcess().getProcessFileName());

        List<String> groups = Arrays.asList(new String[] {"WINDOWS_SYSTEM32","WINDOWS"});
        for (String group : groups) {
            writer.print(" dir.path.src=" + group);
        }

        List<String> processCategories = registryEvent.getRegistryOperation().getProcess().getProcessCategories();
        if(processCategories != null && !processCategories.isEmpty()) {
            writer.print(" file.cat=" + NamesConversionUtils.revertCategories(processCategories));
        } else {
            writer.print(" file.cat=" + NamesConversionUtils.revertCategories(Arrays.asList(new String[] {"WINDOWS_PROCESS"})));
        }

        writer.print(" cert.common=" + registryEvent.getRegistryOperation().getProcess().getProcessCertificateIssuer());
        writer.print(" ec.subject=" + NamesConversionUtils.revert2LowerCamel(registryEvent.getRegistryOperation().getRegistryEntry().getKeyGroup()));
        writer.print(" registry.key=" + (registryEvent.getRegistryOperation().getRegistryEntry().getKey() + registryEvent.getRegistryOperation().getRegistryEntry().getValueName()).replace("\\","\\\\")) ;

        // Fields for fake links to "investigate", not used by ADE
        writer.print(" nwe.callback_id=" + "nwe://64edb0e7-eda6-496f-8889-6c239b32bb5b");
        writer.print(" checksum.src=" + "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57");
        writer.print(" checksum.dst=" + "1fa5a6c8438a4e6d373d39c96b77c0c84540d38b80628effdec89e77d02d7e57");
        writer.print(" process.vid.src=-363757471601907552");
        writer.print(" process.vid.dst=8014783080838410532");
        writer.print(" agent.id=" + "807E600E-FBBB-E21B-FB17-7E44BFB6186B");
        writer.print(" os=" + registryEvent.getMachineEntity().getOsVersion());

        writer.println();
    }

}
