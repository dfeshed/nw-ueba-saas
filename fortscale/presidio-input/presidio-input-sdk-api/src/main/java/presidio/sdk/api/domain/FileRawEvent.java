package presidio.sdk.api.domain;

import fortscale.domain.core.AbstractAuditableDocument;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document
public class FileRawEvent extends AbstractAuditableDocument {


    public static final String DATA_SOURCE_FIELD_NAME = "dataSource";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String NORMALIZED_USERNAME_FIELD_NAME = "normalizedUsername";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String DST_FILE_PATH_FIELD_NAME = "dstFilePath";
    public static final String SRC_FOLDER_PATH_FIELD_NAME = "srcFolderPath";
    public static final String DST_FOLDER_PATH_FIELD_NAME = "dstFolder";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD_NAME = "isDstDriveShared";


    @NotEmpty
    @Field(DATA_SOURCE_FIELD_NAME)
    private String dataSource;
    @NotEmpty
    @Field(OPERATION_TYPE_FIELD_NAME)
    private FileOperationType operationType;
    @NotEmpty
    @Field(NORMALIZED_USERNAME_FIELD_NAME)
    private String normalizedUsername;
    @Field(RESULT_FIELD_NAME)
    private EventResult result;
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
                        boolean isSrcDriveShared, boolean isDstDriveShared) {
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
    }

    public FileRawEvent(String[] record) {
        dateTime = Instant.parse(record[0]);
        this.dataSource = record[1];
        this.operationType = FileOperationType.valueOf(record[2]);
        this.normalizedUsername = record[3];
        this.result = EventResult.valueOf(record[4]);
        this.srcFilePath = record[5];
        this.dstFilePath = record[6];
        this.srcFolderPath = record[7];
        this.dstFolderPath = record[8];
        this.fileSize = Long.valueOf(record[9]);
        this.isSrcDriveShared = Boolean.getBoolean(record[10]);
        this.isDstDriveShared = Boolean.getBoolean(record[11]);
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public FileOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(FileOperationType operationType) {
        this.operationType = operationType;
    }

    public String getNormalizedUsername() {
        return normalizedUsername;
    }

    public void setNormalizedUsername(String normalizedUsername) {
        this.normalizedUsername = normalizedUsername;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
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
                "dataSource='" + dataSource + '\'' +
                ", operationType=" + operationType +
                ", normalizedUsername='" + normalizedUsername + '\'' +
                ", result=" + result +
                ", srcFilePath='" + srcFilePath + '\'' +
                ", dstFilePath='" + dstFilePath + '\'' +
                ", srcFolderPath='" + srcFolderPath + '\'' +
                ", dstFolderPath='" + dstFolderPath + '\'' +
                ", fileSize=" + fileSize +
                ", isSrcDriveShared=" + isSrcDriveShared +
                ", isDstDriveShared=" + isDstDriveShared +
                '}';
    }
}
