package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;

@Document
public class FileRawEvent extends AbstractPresidioDocument {


    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String DST_FILE_PATH_FIELD_NAME = "dstFilePath";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD_NAME = "isDstDriveShared";

    @Field(SRC_FILE_PATH_FIELD_NAME)
    private String srcFilePath;

    @Field(IS_SRC_DRIVE_SHARED_FIELD_NAME)
    private boolean isSrcDriveShared;

    @Field(DST_FILE_PATH_FIELD_NAME)
    private String dstFilePath;

    @Field(IS_DST_DRIVE_SHARED_FIELD_NAME)
    private boolean isDstDriveShared;

    @Field(FILE_SIZE_FIELD_NAME)
    private Long fileSize;

    public FileRawEvent() {
    }

    public FileRawEvent(String[] event) {
        this.dateTime = Instant.parse(event[0]);
        this.eventId = event[1];
        this.dataSource = event[2];
        this.userId = event[3];
        this.operationType = event[4];
        this.result = EventResult.valueOf(event[5]);
        this.srcFilePath = event[6];
        this.isDstDriveShared = Boolean.parseBoolean(event[7]);
        this.dstFilePath = event[7];
        this.isDstDriveShared = Boolean.parseBoolean(event[8]);
        this.fileSize = Long.valueOf(event[9]);
        this.operationTypeCategory = new ArrayList<>();
        for (int i = 8; i <= event.length; i++) {
            this.operationTypeCategory.add(event[i]);
        }
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getDstFilePath() {
        return dstFilePath;
    }

    public void setDstFilePath(String dstFilePath) {
        this.dstFilePath = dstFilePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean getIsSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setIsSrcDriveShared(boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public boolean getIsDstDriveShared() {
        return isDstDriveShared;
    }

    public void setIsDstDriveShared(boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }

    public boolean isSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public boolean isDstDriveShared() {
        return isDstDriveShared;
    }

    public void setDstDriveShared(boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }
}
