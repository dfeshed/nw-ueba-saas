package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class PrintRawEvent extends AbstractInputDocument {

    public static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    public static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    public static final String PRINTER_ID_FIELD_NAME = "printerId";
    public static final String PRINTER_NAME_FIELD_NAME = "printerName";
    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String NUM_OF_PAGES_FIELD_NAME = "numOfPages";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";


    @Field(SRC_MACHINE_ID_FIELD_NAME)
    private String srcMachineId;

    @Field(SRC_MACHINE_NAME_FIELD_NAME)
    private String srcMachineName;

    @Field(PRINTER_ID_FIELD_NAME)
    private String printerId;

    @Field(PRINTER_NAME_FIELD_NAME)
    private String printerName;

    @Field(SRC_FILE_PATH_FIELD_NAME)
    private String srcFilePath;

    @Field(IS_SRC_DRIVE_SHARED_FIELD_NAME)
    private Boolean isSrcDriveShared;

    @Field(FILE_SIZE_FIELD_NAME)
    private Long fileSize;

    @Field(NUM_OF_PAGES_FIELD_NAME)
    private Long numOfPages;

    public PrintRawEvent() {
        super();
    }

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    private String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    private String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD_NAME)
    private List<String> operationTypeCategories;

    @Field(RESULT_FIELD_NAME)
    private EventResult result;

    @Field(USER_NAME_FIELD_NAME)
    private String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    @Field(RESULT_CODE_FIELD_NAME)
    private String resultCode;

    public PrintRawEvent(PrintRawEvent other) {
        super(other);
        this.srcMachineId = other.srcMachineId;
        this.srcMachineName = other.srcMachineName;
        this.printerId = other.printerId;
        this.printerName = other.printerName;
        this.srcFilePath = other.srcFilePath;
        this.isSrcDriveShared = other.isSrcDriveShared;
        this.fileSize = other.fileSize;
        this.numOfPages = other.numOfPages;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.operationTypeCategories = other.operationTypeCategories;
        this.result = other.result;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
        this.resultCode = other.resultCode;
    }

    public PrintRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                         List<String> operationTypeCategories, EventResult result, String userName, String userDisplayName,
                         Map<String, String> additionalInfo, String resultCode, String srcMachineId, String srcMachineName,
                         String printerId, String printerName, String srcFilePath, Boolean isSrcDriveShared, Long fileSize, Long numOfPages) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.srcMachineId = srcMachineId;
        this.srcMachineName = srcMachineName;
        this.printerId = printerId;
        this.printerName = printerName;
        this.srcFilePath = srcFilePath;
        this.isSrcDriveShared = isSrcDriveShared;
        this.fileSize = fileSize;
        this.numOfPages = numOfPages;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.resultCode = resultCode;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getSrcFilePath() {
        return srcFilePath;
    }

    public void setSrcFilePath(String srcFilePath) {
        this.srcFilePath = srcFilePath;
    }

    public Boolean getSrcDriveShared() {
        return isSrcDriveShared;
    }

    public void setSrcDriveShared(Boolean srcDriveShared) {
        isSrcDriveShared = srcDriveShared;
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

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("srcMachineId", srcMachineId)
                .append("srcMachineName", srcMachineName)
                .append("printerId", printerId)
                .append("printerName", printerName)
                .append("srcFilePath", srcFilePath)
                .append("isSrcDriveShared", isSrcDriveShared)
                .append("fileSize", fileSize)
                .append("numOfPages", numOfPages)
                .append("eventId", getEventId())
                .append("dataSource", getDataSource())
                .append("userId", userId)
                .append("operationType", operationType)
                .append("operationTypeCategories", operationTypeCategories)
                .append("result", result)
                .append("userName", userName)
                .append("userDisplayName", userDisplayName)
                .append("additionalInfo", getAdditionalInfo())
                .append("resultCode", resultCode)
                .append("dateTime", dateTime)
                .toString();
    }
}
