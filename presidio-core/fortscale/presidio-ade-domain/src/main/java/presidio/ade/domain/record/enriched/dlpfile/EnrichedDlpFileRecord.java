package presidio.ade.domain.record.enriched.dlpfile;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.List;

/**
 * The enriched DLP file event POJO.
 * <p>
 * Created by Lior Govrin on 05/06/2017.
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.DLPFILE)
public class EnrichedDlpFileRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String SOURCE_PATH_FIELD = "sourcePath";
    public static final String SOURCE_FILE_NAME_FIELD = "sourceFileName";
    public static final String SOURCE_DRIVE_TYPE_FIELD = "sourceDriveType";
    public static final String DESTINATION_PATH_FIELD = "destinationPath";
    public static final String DESTINATION_FILE_NAME_FIELD = "destinationFileName";
    public static final String DESTINATION_DRIVE_TYPE_FIELD = "destinationDriveType";
    public static final String FILE_SIZE_FIELD = "fileSize";
    public static final String WAS_BLOCKED_FIELD = "wasBlocked";
    public static final String WAS_CLASSIFIED_FIELD = "wasClassified";
    public static final String MALWARE_SCAN_RESULT_FIELD = "malwareScanResult";
    public static final String EXECUTING_APPLICATION_FIELD = "executingApplication";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;
    @Field(SOURCE_PATH_FIELD)
    private String sourcePath;
    @Field(SOURCE_FILE_NAME_FIELD)
    private String sourceFileName;
    @Field(SOURCE_DRIVE_TYPE_FIELD)
    private String sourceDriveType;
    @Field(DESTINATION_PATH_FIELD)
    private String destinationPath;
    @Field(DESTINATION_FILE_NAME_FIELD)
    private String destinationFileName;
    @Field(DESTINATION_DRIVE_TYPE_FIELD)
    private String destinationDriveType;
    @Field(FILE_SIZE_FIELD)
    private double fileSize;
    @Field(WAS_BLOCKED_FIELD)
    private boolean wasBlocked;
    @Field(WAS_CLASSIFIED_FIELD)
    private boolean wasClassified;
    @Field(MALWARE_SCAN_RESULT_FIELD)
    private String malwareScanResult;
    @Field(EXECUTING_APPLICATION_FIELD)
    private String executingApplication;
    @Field(OPERATION_TYPE_FIELD)
    private String operationType;
    @Field(OPERATION_TYPE_CATEGORIES_FIELD)
    private List<String> operationTypeCategories;
    @Field(RESULT_FIELD)
    private EventResult result;
    @Field(RESULT_CODE_FIELD)
    private String resultCode;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedDlpFileRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.DLPFILE.getName();
    }

    @Override
    @Transient
    public String getDataSource() {
        return getAdeEventType();
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

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getSourceDriveType() {
        return sourceDriveType;
    }

    public void setSourceDriveType(String sourceDriveType) {
        this.sourceDriveType = sourceDriveType;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationFileName() {
        return destinationFileName;
    }

    public void setDestinationFileName(String destinationFileName) {
        this.destinationFileName = destinationFileName;
    }

    public String getDestinationDriveType() {
        return destinationDriveType;
    }

    public void setDestinationDriveType(String destinationDriveType) {
        this.destinationDriveType = destinationDriveType;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isWasBlocked() {
        return wasBlocked;
    }

    public void setWasBlocked(boolean wasBlocked) {
        this.wasBlocked = wasBlocked;
    }

    public boolean isWasClassified() {
        return wasClassified;
    }

    public void setWasClassified(boolean wasClassified) {
        this.wasClassified = wasClassified;
    }

    public String getMalwareScanResult() {
        return malwareScanResult;
    }

    public void setMalwareScanResult(String malwareScanResult) {
        this.malwareScanResult = malwareScanResult;
    }

    public String getExecutingApplication() {
        return executingApplication;
    }

    public void setExecutingApplication(String executingApplication) {
        this.executingApplication = executingApplication;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Transient
    public AdeEnrichedDlpFileContext getContext() {
        return new AdeEnrichedDlpFileContext(this);
    }
}
