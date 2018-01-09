package presidio.ade.domain.record.enriched.print;

import fortscale.common.general.Schema;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;

/**
 * The enriched Print record POJO.
 *
 * @author Lior Govrin
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.PRINT)
public class EnrichedPrintRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD = "srcMachineNameRegexCluster";
    public static final String DST_MACHINE_ID_FIELD = "dstMachineId";
    public static final String DST_MACHINE_NAME_REGEX_CLUSTER_FIELD = "dstMachineNameRegexCluster";
    public static final String ABSOLUTE_FILE_PATH_FIELD = "absoluteFilePath";
    public static final String ABSOLUTE_FOLDER_PATH_FIELD = "absoluteFolderPath";
    public static final String FILE_EXTENSION_FIELD = "fileExtension";
    public static final String DRIVE_SHARED_FIELD = "driveShared";
    public static final String FILE_SIZE_IN_BYTES_FIELD = "fileSizeInBytes";
    public static final String NUM_OF_PAGES_FIELD = "numOfPages";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;
    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String srcMachineNameRegexCluster;
    @Field(DST_MACHINE_ID_FIELD)
    private String dstMachineId;
    @Field(DST_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String dstMachineNameRegexCluster;
    @Field(ABSOLUTE_FILE_PATH_FIELD)
    private String absoluteFilePath;
    @Field(ABSOLUTE_FOLDER_PATH_FIELD)
    private String absoluteFolderPath;
    @Field(FILE_EXTENSION_FIELD)
    private String fileExtension;
    @Field(DRIVE_SHARED_FIELD)
    private Boolean driveShared;
    @Field(FILE_SIZE_IN_BYTES_FIELD)
    private Long fileSizeInBytes;
    @Field(NUM_OF_PAGES_FIELD)
    private Long numOfPages;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical date and time
     */
    public EnrichedPrintRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.PRINT.getName();
    }

    @Transient
    public AdeEnrichedPrintContext getContext() {
        return new AdeEnrichedPrintContext(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public String getAbsoluteFolderPath() {
        return absoluteFolderPath;
    }

    public void setAbsoluteFolderPath(String absoluteFolderPath) {
        this.absoluteFolderPath = absoluteFolderPath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Boolean getDriveShared() {
        return driveShared;
    }

    public void setDriveShared(Boolean driveShared) {
        this.driveShared = driveShared;
    }

    public Long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(Long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public Long getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(Long numOfPages) {
        this.numOfPages = numOfPages;
    }
}
