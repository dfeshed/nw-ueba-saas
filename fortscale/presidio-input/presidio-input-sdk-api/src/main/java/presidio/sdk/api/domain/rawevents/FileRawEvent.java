package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document
public class FileRawEvent extends AbstractInputDocument {


    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String DST_FILE_PATH_FIELD_NAME = "dstFilePath";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD_NAME = "isDstDriveShared";

    @Field(SRC_FILE_PATH_FIELD_NAME)
    private String srcFilePath;

    @Field(IS_SRC_DRIVE_SHARED_FIELD_NAME)
    private Boolean isSrcDriveShared;

    @Field(DST_FILE_PATH_FIELD_NAME)
    private String dstFilePath;

    @Field(IS_DST_DRIVE_SHARED_FIELD_NAME)
    private Boolean isDstDriveShared;

    @Field(FILE_SIZE_FIELD_NAME)
    private Long fileSize;

    public FileRawEvent() {
    }

    public FileRawEvent(FileRawEvent other) {
        super(other);
        this.srcFilePath = other.srcFilePath;
        this.isSrcDriveShared = other.isSrcDriveShared;
        this.dstFilePath = other.dstFilePath;
        this.isDstDriveShared = other.isDstDriveShared;
        this.fileSize = other.fileSize;
    }

    public FileRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                        List<String> operationTypeCategory, EventResult result, String userName, String userDisplayName,
                        Map<String, String> additionalInfo, String srcFilePath, Boolean isSrcDriveShared,
                        String dstFilePath, Boolean isDstDriveShared, Long fileSize, String resultCode) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo, resultCode);
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

    public Boolean getIsSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setIsSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public Boolean getIsDstDriveShared() {
        return isDstDriveShared;
    }

    public void setIsDstDriveShared(Boolean dstDriveShared) {
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
