package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.FileRawEvent;

public class FileTransformedEvent extends FileRawEvent {

    public static final String SRC_FOLDER_PATH_FIELD_NAME = "srcFolderPath";
    public static final String DST_FOLDER_PATH_FIELD_NAME = "dstFolderPath";

    private String srcFolderPath;
    private String dstFolderPath;

    public FileTransformedEvent(FileRawEvent other) {
        super(other);
    }

    public String getDstFolderPath() {
        return dstFolderPath;
    }

    public void setDstFolderPath(String dstFolderPath) {
        this.dstFolderPath = dstFolderPath;
    }

    public String getSrcFolderPath() {
        return srcFolderPath;
    }

    public void setSrcFolderPath(String srcFolderPath) {
        this.srcFolderPath = srcFolderPath;
    }
}
