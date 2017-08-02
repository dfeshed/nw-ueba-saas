package fortscale.domain.core;

import fortscale.common.general.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class FileRawEvent extends AbstractPresidioDocument {


    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String DST_FILE_PATH_FIELD_NAME = "dstFilePath";
    public static final String SRC_FOLDER_PATH_FIELD_NAME = "srcFolderPath";
    public static final String DST_FOLDER_PATH_FIELD_NAME = "dstFolder";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD_NAME = "isDstDriveShared";


    @Field(OPERATION_TYPE_FIELD_NAME)
    private FileOperationType operationType;
    @Field(SRC_FILE_PATH_FIELD_NAME)
    private String srcFilePath;
    @Field(DST_FILE_PATH_FIELD_NAME)
    private String dstFilePath;
    @Field(SRC_FOLDER_PATH_FIELD_NAME)
    private String srcFolderPath;
    @Field(DST_FOLDER_PATH_FIELD_NAME)
    private String dstFolderPath;
    @Field(FILE_SIZE_FIELD_NAME)
    private Long fileSize;
    @Field(IS_SRC_DRIVE_SHARED_FIELD_NAME)
    private boolean isSrcDriveShared;
    @Field(IS_DST_DRIVE_SHARED_FIELD_NAME)
    private boolean isDstDriveShared;

    public FileRawEvent() {
    }

    public FileRawEvent(String dataSource, FileOperationType operationType, String normalizedUsername, EventResult result,
                        String srcFilePath, String dstFilePath, String srcFolderPath, String dstFolderPath, Long fileSize,
                        boolean isSrcDriveShared, boolean isDstDriveShared, String eventId) {
        this.dataSource = dataSource;
        this.operationType = operationType;
        this.normalizedUsername = normalizedUsername;
        this.result = result;
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.srcFolderPath = srcFolderPath;
        this.dstFolderPath = dstFolderPath;
        this.fileSize = fileSize;
        this.isSrcDriveShared = isSrcDriveShared;
        this.isDstDriveShared = isDstDriveShared;
        this.eventId = eventId;
    }

    public FileRawEvent(String[] record) {
        dateTime = Instant.parse(record[0]);
        this.eventId = record[1];
        this.dataSource = record[2];
        this.operationType = FileOperationType.valueOf(record[3]);
        this.normalizedUsername = record[4];
        this.result = EventResult.valueOf(record[5]);
        this.srcFilePath = record[6];
        this.dstFilePath = record[7];
        this.srcFolderPath = record[8];
        this.dstFolderPath = record[9];
        this.fileSize = Long.valueOf(record[10]);
        this.isSrcDriveShared = Boolean.getBoolean(record[11]);
        this.isDstDriveShared = Boolean.getBoolean(record[12]);
    }

    public FileRawEvent(Instant dateTime, String dataSource, String normalizedUsername, String eventId, EventResult result,
                        FileOperationType operationType, String srcFilePath, String dstFilePath, String srcFolderPath,
                        String dstFolderPath, Long fileSize, boolean isSrcDriveShared, boolean isDstDriveShared) {
        super(dateTime, dataSource, normalizedUsername, eventId, result);
        this.operationType = operationType;
        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.srcFolderPath = srcFolderPath;
        this.dstFolderPath = dstFolderPath;
        this.fileSize = fileSize;
        this.isSrcDriveShared = isSrcDriveShared;
        this.isDstDriveShared = isDstDriveShared;
    }

    public FileOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(FileOperationType operationType) {
        this.operationType = operationType;
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

    public String getSrcFolderPath() {
        return srcFolderPath;
    }

    public void setSrcFolderPath(String srcFolderPath) {
        this.srcFolderPath = srcFolderPath;
    }

    public String getDstFolderPath() {
        return dstFolderPath;
    }

    public void setDstFolderPath(String dstFolderPath) {
        this.dstFolderPath = dstFolderPath;
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

    @Override
    public String toString() {
        return "FileRawEvent{" +
                "dataSource='" + dataSource + '\'' +
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", eventId='" + eventId + '\'' +
                ", result=" + result +
                ", operationType=" + operationType +
                ", srcFilePath='" + srcFilePath + '\'' +
                ", dstFilePath='" + dstFilePath + '\'' +
                ", dateTime=" + dateTime +
                ", srcFolderPath='" + srcFolderPath + '\'' +
                ", dstFolderPath='" + dstFolderPath + '\'' +
                ", fileSize=" + fileSize +
                ", isSrcDriveShared=" + isSrcDriveShared +
                ", isDstDriveShared=" + isDstDriveShared +
                '}';
    }
}
