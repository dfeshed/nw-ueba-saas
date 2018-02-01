package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 02/08/2017.
 */
@Document
public class FileEnrichedEvent extends EnrichedEvent {

    public static final String ABSOLUTE_SRC_FILE_PATH_FIELD = "absoluteSrcFilePath";
    public static final String ABSOLUTE_DST_FILE_PATH_FIELD = "absoluteDstFilePath";
    public static final String ABSOLUTE_SRC_FOLDER_FILE_PATH_FIELD = "absoluteSrcFolderFilePath";
    public static final String ABSOLUTE_DST_FOLDER_FILE_PATH_FIELD = "absoluteDstFolderFilePath";
    public static final String FILE_SIZE_FIELD = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD = "isDstDriveShared";

    @Field(ABSOLUTE_SRC_FILE_PATH_FIELD)
    private String absoluteSrcFilePath;

    @Field(ABSOLUTE_DST_FILE_PATH_FIELD)
    private String absoluteDstFilePath;

    @Field(ABSOLUTE_SRC_FOLDER_FILE_PATH_FIELD)
    private String absoluteSrcFolderFilePath;

    @Field(ABSOLUTE_DST_FOLDER_FILE_PATH_FIELD)
    private String absoluteDstFolderFilePath;

    @Field(FILE_SIZE_FIELD)
    private Long fileSize;

    @Field(IS_SRC_DRIVE_SHARED_FIELD)
    private Boolean isSrcDriveShared;

    @Field(IS_DST_DRIVE_SHARED_FIELD)
    private Boolean isDstDriveShared;

    public FileEnrichedEvent() {
    }

    public FileEnrichedEvent(String absoluteSrcFilePath,
                             String absoluteDstFilePath,
                             String absoluteSrcFolderFilePath,
                             String absoluteDstFolderFilePath,
                             Long fileSize,
                             Boolean isSrcDriveShared,
                             Boolean isDstDriveShared) {
        this.absoluteSrcFilePath = absoluteSrcFilePath;
        this.absoluteDstFilePath = absoluteDstFilePath;
        this.absoluteSrcFolderFilePath = absoluteSrcFolderFilePath;
        this.absoluteDstFolderFilePath = absoluteDstFolderFilePath;
        this.fileSize = fileSize;
        this.isSrcDriveShared = isSrcDriveShared;
        this.isDstDriveShared = isDstDriveShared;
    }

    public FileEnrichedEvent(Instant createdDate,
                             Instant eventDate,
                             String eventId,
                             String schema,
                             String userId,
                             String userName,
                             String userDisplayName,
                             String dataSource,
                             String operationType,
                             List<String> operationTypeCategories,
                             EventResult result,
                             String resultCode,
                             Map<String, String> additionalInfo,
                             String absoluteSrcFilePath,
                             String absoluteDstFilePath,
                             String absoluteSrcFolderFilePath,
                             String absoluteDstFolderFilePath,
                             Long fileSize,
                             Boolean isSrcDriveShared,
                             Boolean isDstDriveShared) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, operationType, operationTypeCategories, result, resultCode, additionalInfo);
        this.absoluteSrcFilePath = absoluteSrcFilePath;
        this.absoluteDstFilePath = absoluteDstFilePath;
        this.absoluteSrcFolderFilePath = absoluteSrcFolderFilePath;
        this.absoluteDstFolderFilePath = absoluteDstFolderFilePath;
        this.fileSize = fileSize;
        this.isSrcDriveShared = isSrcDriveShared;
        this.isDstDriveShared = isDstDriveShared;
    }

    public String getAbsoluteSrcFilePath() {
        return absoluteSrcFilePath;
    }

    public String getAbsoluteDstFilePath() {
        return absoluteDstFilePath;
    }

    public String getAbsoluteSrcFolderFilePath() {
        return absoluteSrcFolderFilePath;
    }

    public String getAbsoluteDstFolderFilePath() {
        return absoluteDstFolderFilePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public Boolean getIsSrcDriveShared() {
        return isSrcDriveShared;
    }

    public Boolean getIsDstDriveShared() {
        return isDstDriveShared;
    }

    public void setAbsoluteSrcFilePath(String absoluteSrcFilePath) {
        this.absoluteSrcFilePath = absoluteSrcFilePath;
    }

    public void setAbsoluteDstFilePath(String absoluteDstFilePath) {
        this.absoluteDstFilePath = absoluteDstFilePath;
    }

    public void setAbsoluteSrcFolderFilePath(String absoluteSrcFolderFilePath) {
        this.absoluteSrcFolderFilePath = absoluteSrcFolderFilePath;
    }

    public void setAbsoluteDstFolderFilePath(String absoluteDstFolderFilePath) {
        this.absoluteDstFolderFilePath = absoluteDstFolderFilePath;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setIsSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public void setIsDstDriveShared(Boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }
}
