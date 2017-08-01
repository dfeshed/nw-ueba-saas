package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * The enriched file record POJO.
 */
@Document
//todo: add @AdeRecordMetadata annotation
public class EnrichedFileRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String ABSOLUTE_SRC_FILE_PATH_FIELD = "absoluteSrcFilePath";
    public static final String ABSOLUTE_DST_FILE_PATH_FIELD = "absoluteDstFilePath";
    public static final String ABSOLUTE_SRC_FOLDER_FILE_PATH_FIELD = "absoluteSrcFolderFilePath";
    public static final String ABSOLUTE_DST_FOLDER_FILE_PATH_FIELD = "absoluteDstFolderFilePath";
    public static final String FILE_SIZE_FIELD = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD = "isDstDriveShared";



    @Indexed
    @Field(USER_ID_FIELD)
    private String userId;
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


    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedFileRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAbsoluteSrcFilePath() {
        return absoluteSrcFilePath;
    }

    public void setAbsoluteSrcFilePath(String absoluteSrcFilePath) {
        this.absoluteSrcFilePath = absoluteSrcFilePath;
    }

    public String getAbsoluteDstFilePath() {
        return absoluteDstFilePath;
    }

    public void setAbsoluteDstFilePath(String absoluteDstFilePath) {
        this.absoluteDstFilePath = absoluteDstFilePath;
    }

    public String getAbsoluteSrcFolderFilePath() {
        return absoluteSrcFolderFilePath;
    }

    public void setAbsoluteSrcFolderFilePath(String absoluteSrcFolderFilePath) {
        this.absoluteSrcFolderFilePath = absoluteSrcFolderFilePath;
    }

    public String getAbsoluteDstFolderFilePath() {
        return absoluteDstFolderFilePath;
    }

    public void setAbsoluteDstFolderFilePath(String absoluteDstFolderFilePath) {
        this.absoluteDstFolderFilePath = absoluteDstFolderFilePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
    }

    public Boolean getDstDriveShared() {
        return isDstDriveShared;
    }

    public void setDstDriveShared(Boolean dstDriveShared) {
        isDstDriveShared = dstDriveShared;
    }


    @Override
    @Transient
    public String getAdeEventType() {
        return FileRecord.FILE_STR;
    }




    @Transient
    public AdeEnrichedFileContext getContext() {
        return new AdeEnrichedFileContext(this);
    }
}
