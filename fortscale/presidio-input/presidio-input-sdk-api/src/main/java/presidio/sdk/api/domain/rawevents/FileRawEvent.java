package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class FileRawEvent extends AbstractInputDocument {


    public static final String SRC_FILE_PATH_FIELD_NAME = "srcFilePath";
    public static final String DST_FILE_PATH_FIELD_NAME = "dstFilePath";
    public static final String FILE_SIZE_FIELD_NAME = "fileSize";
    public static final String IS_SRC_DRIVE_SHARED_FIELD_NAME = "isSrcDriveShared";
    public static final String IS_DST_DRIVE_SHARED_FIELD_NAME = "isDstDriveShared";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";

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

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    protected String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    protected String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD_NAME)
    protected List<String> operationTypeCategories;

    @Field(RESULT_FIELD_NAME)
    protected EventResult result;

    @Field(USER_NAME_FIELD_NAME)
    protected String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    protected String userDisplayName;

    @Field(RESULT_CODE_FIELD_NAME)
    protected String resultCode;

    {
        additionalInfo = new HashMap<>();
        additionalInfo.put(IS_USER_ADMIN_FIELD_NAME, Boolean.toString(false));
    }

    public FileRawEvent() {
    }

    public FileRawEvent(FileRawEvent other) {
        super(other);
        this.srcFilePath = other.srcFilePath;
        this.isSrcDriveShared = other.isSrcDriveShared;
        this.dstFilePath = other.dstFilePath;
        this.isDstDriveShared = other.isDstDriveShared;
        this.fileSize = other.fileSize;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.operationTypeCategories = other.operationTypeCategories;
        this.result = other.result;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
        this.resultCode = other.resultCode;
    }

    public FileRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                        List<String> operationTypeCategories, EventResult result, String userName, String userDisplayName,
                        Map<String, String> additionalInfo, String srcFilePath, Boolean isSrcDriveShared,
                        String dstFilePath, Boolean isDstDriveShared, Long fileSize, String resultCode) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.srcFilePath = srcFilePath;
        this.isSrcDriveShared = isSrcDriveShared;
        this.dstFilePath = dstFilePath;
        this.isDstDriveShared = isDstDriveShared;
        this.fileSize = fileSize;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.resultCode = resultCode;
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
                ", operationTypeCategories=" + operationTypeCategories +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                '}';
    }
}
