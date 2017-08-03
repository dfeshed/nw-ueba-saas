package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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

    public FileRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                        List<String> operationTypeCategory, EventResult result, String userName, String userDisplayName,
                        Map<String, String> additionalInfo, String srcFilePath, boolean isSrcDriveShared,
                        String dstFilePath, boolean isDstDriveShared, Long fileSize) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo);
        this.srcFilePath = srcFilePath;
        this.isSrcDriveShared = isSrcDriveShared;
        this.dstFilePath = dstFilePath;
        this.isDstDriveShared = isDstDriveShared;
        this.fileSize = fileSize;
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


    @Override
    public String toString() {
        return "FileRawEvent{" +
                "srcFilePath='" + srcFilePath + '\'' +
                ", isSrcDriveShared=" + isSrcDriveShared +
                ", dstFilePath='" + dstFilePath + '\'' +
                ", isDstDriveShared=" + isDstDriveShared +
                ", fileSize=" + fileSize +
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategory=" + operationTypeCategory +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}
