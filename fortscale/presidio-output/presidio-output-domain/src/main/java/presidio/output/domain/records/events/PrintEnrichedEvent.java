package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class PrintEnrichedEvent extends EnrichedUserEvent {

    public static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    public static final String PRINTER_ID_FIELD_NAME = "printerId";
    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String NUM_OF_PAGES_FIELD_NAME = "numOfPages";
    public static final String SRC_MACHINE_CLUSTER_FIELD_NAME = "srcMachineCluster";
    public static final String SRC_FOLDER_PATH_FIELD_NAME = "srcFolderPath";
    public static final String SRC_FILE_EXTENSION_FIELD_NAME = "srcFileExtension";
    public static final String PRINTER_CLUSTER_FIELD_NAME = "printerCluster";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";

    @Field(SRC_MACHINE_ID_FIELD_NAME)
    private String srcMachineId;

    @Field(SRC_MACHINE_CLUSTER_FIELD_NAME)
    private String srcMachineCluster;

    @Field(PRINTER_ID_FIELD_NAME)
    private String printerId;

    @Field(PRINTER_CLUSTER_FIELD_NAME)
    private String printerCluster;

    @Field(SRC_FILE_PATH_FIELD_NAME)
    private String srcFilePath;

    @Field(SRC_FOLDER_PATH_FIELD_NAME)
    private String srcFolderPath;

    @Field(SRC_FILE_EXTENSION_FIELD_NAME)
    private String srcFileExtension;

    @Field(IS_SRC_DRIVE_SHARED_FIELD_NAME)
    private Boolean isSrcDriveShared;

    @Field(FILE_SIZE_FIELD_NAME)
    private Long fileSize;

    @Field(NUM_OF_PAGES_FIELD_NAME)
    private Long numOfPages;

    @Field(OPERATION_TYPE_FIELD_NAME)
    private String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD_NAME)
    private List<String> operationTypeCategories;

    @Field(RESULT_FIELD_NAME)
    private EventResult result;

    @Field(RESULT_CODE_FIELD_NAME)
    private String resultCode;

    public PrintEnrichedEvent() {
    }

    public PrintEnrichedEvent(Instant createdDate, Instant eventDate, String eventId, String schema, String userId,
                              String userName, String userDisplayName, String dataSource, String operationType,
                              List<String> operationTypeCategories, EventResult result, String resultCode,
                              Map<String, String> additionalInfo, String srcMachineId, String srcMachineCluster,
                              String printerId, String printerCluster, String srcFilePath, String srcFolderPath,
                              String srcFileExtension, Boolean isSrcDriveShared, Long fileSize, Long numOfPages) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, additionalInfo);
        this.srcMachineId = srcMachineId;
        this.srcMachineCluster = srcMachineCluster;
        this.printerId = printerId;
        this.printerCluster = printerCluster;
        this.srcFilePath = srcFilePath;
        this.srcFolderPath = srcFolderPath;
        this.srcFileExtension = srcFileExtension;
        this.isSrcDriveShared = isSrcDriveShared;
        this.fileSize = fileSize;
        this.numOfPages = numOfPages;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.resultCode = resultCode;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineCluster() {
        return srcMachineCluster;
    }

    public void setSrcMachineCluster(String srcMachineCluster) {
        this.srcMachineCluster = srcMachineCluster;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterCluster() {
        return printerCluster;
    }

    public void setPrinterCluster(String printerCluster) {
        this.printerCluster = printerCluster;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public String getSrcFolderPath() {
        return srcFolderPath;
    }

    public void setSrcFolderPath(String srcFolderPath) {
        this.srcFolderPath = srcFolderPath;
    }

    public String getSrcFileExtension() {
        return srcFileExtension;
    }

    public void setSrcFileExtension(String srcFileExtension) {
        this.srcFileExtension = srcFileExtension;
    }

    public Boolean getIsSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setIsSrcDriveShared(Boolean isSrcDriveShared) {
        this.isSrcDriveShared = isSrcDriveShared;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(Long numOfPages) {
        this.numOfPages = numOfPages;
    }

    public String getOperationType() {
        return operationType;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public EventResult getResult() {
        return result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
