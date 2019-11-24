package presidio.integration.performance.generators.printer;

import fortscale.common.general.Schema;

import java.util.HashMap;
import java.util.Map;

public class EventLogPrinterFactory {
    private final Map<Schema, EventLogPrinter> schemaToPrinterMap = new HashMap<>();

    public EventLogPrinterFactory() {
        schemaToPrinterMap.put(Schema.AUTHENTICATION, new EventLogPrinterAuthentication());
        schemaToPrinterMap.put(Schema.ACTIVE_DIRECTORY, new EventLogPrinterActiveDirectory());
        schemaToPrinterMap.put(Schema.FILE, new EventLogPrinterFile());
        schemaToPrinterMap.put(Schema.PROCESS, new EventLogPrinterProcess());
        schemaToPrinterMap.put(Schema.REGISTRY, new EventLogPrinterRegistry());
    }

    public EventLogPrinter getPrinter(Schema schema) {
        return schemaToPrinterMap.get(schema);
    }
}
