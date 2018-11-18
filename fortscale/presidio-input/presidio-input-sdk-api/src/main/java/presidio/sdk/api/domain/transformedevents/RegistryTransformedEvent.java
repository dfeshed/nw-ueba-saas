package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.RegistryRawEvent;

public class RegistryTransformedEvent extends RegistryRawEvent {

    public static final String PROCESS_FILE_PATH_FIELD_NAME = "processFilePath";

    private String processFilePath;

    public RegistryTransformedEvent(RegistryRawEvent rawEvent) {
        super(rawEvent);
    }

    public String getProcessFilePath() {
        return processFilePath;
    }

    public void setProcessFilePath(String processFilePath) {
        this.processFilePath = processFilePath;
    }
}
