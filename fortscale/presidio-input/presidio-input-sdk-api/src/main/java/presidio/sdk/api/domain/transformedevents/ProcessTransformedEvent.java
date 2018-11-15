package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.ProcessRawEvent;

public class ProcessTransformedEvent extends ProcessRawEvent {

    public static final String SRC_PROCESS_FILE_PATH_FIELD_NAME = "srcProcessFilePath";
    public static final String DST_PROCESS_FILE_PATH_FIELD_NAME = "dstProcessFilePath";

    private String srcProcessFilePath;
    private String dstProcessFilePath;

    public ProcessTransformedEvent(ProcessRawEvent rawEvent) {
        super(rawEvent);
    }

    public String getSrcProcessFilePath() {
        return srcProcessFilePath;
    }

    public void setSrcProcessFilePath(String srcProcessFilePath) {
        this.srcProcessFilePath = srcProcessFilePath;
    }

    public String getDstProcessFilePath() {
        return dstProcessFilePath;
    }

    public void setDstProcessFilePath(String dstProcessFilePath) {
        this.dstProcessFilePath = dstProcessFilePath;
    }
}
