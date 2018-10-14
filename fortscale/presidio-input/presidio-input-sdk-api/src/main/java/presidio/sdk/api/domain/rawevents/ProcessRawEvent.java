package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document
public class ProcessRawEvent extends AbstractInputDocument {

    public static final String MACHINE_ID_FIELD_NAME = "machineId";
    public static final String MACHINE_NAME_FIELD_NAME = "machineName";
    public static final String MACHINE_OWNER_FIELD_NAME = "machineOwner";
    public static final String SRC_PROCESS_DIRECTORY_FIELD_NAME = "srcProcessDirectory";
    public static final String SRC_PROCESS_FILE_NAME_FIELD_NAME = "srcProcessFileName";
    public static final String SRC_PROCESS_DIRECTORY_GROUPS_FIELD_NAME = "srcProcessDirectoryGroups";
    public static final String SRC_PROCESS_CATEGORIES_FIELD_NAME = "srcProcessCategories";
    public static final String SRC_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME = "srcProcessCertificateIssuer";
    public static final String DST_PROCESS_DIRECTORY_FIELD_NAME = "dstProcessDirectory";
    public static final String DST_PROCESS_FILE_NAME_FIELD_NAME = "dstProcessFileName";
    public static final String DST_PROCESS_DIRECTORY_GROUPS_FIELD_NAME = "dstProcessDirectoryGroups";
    public static final String DST_PROCESS_CATEGORIES_FIELD_NAME = "dstProcessCategories";
    public static final String DST_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME = "dstProcessCertificateIssuer";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";


    @Field(MACHINE_ID_FIELD_NAME)
    @NotEmpty
    private String machineId;

    @Field(MACHINE_NAME_FIELD_NAME)
    @NotEmpty
    private String machineName;

    @Field(MACHINE_OWNER_FIELD_NAME)
    private String machineOwner;

    @Field(SRC_PROCESS_DIRECTORY_FIELD_NAME)
    @NotEmpty
    private String srcProcessDirectory;

    @Field(SRC_PROCESS_FILE_NAME_FIELD_NAME)
    @NotEmpty
    private String srcProcessFileName;

    @Field(SRC_PROCESS_DIRECTORY_GROUPS_FIELD_NAME)
    private List<String> srcProcessDirectoryGroups;

    @Field(SRC_PROCESS_CATEGORIES_FIELD_NAME)
    private List<String> srcProcessCategories;

    @Field(SRC_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME)
    private String srcProcessCertificateIssuer;

    @Field(DST_PROCESS_DIRECTORY_FIELD_NAME)
    @NotEmpty
    private String dstProcessDirectory;

    @Field(DST_PROCESS_FILE_NAME_FIELD_NAME)
    @NotEmpty
    private String dstProcessFileName;

    @Field(DST_PROCESS_DIRECTORY_GROUPS_FIELD_NAME)
    private List<String> dstProcessDirectoryGroups;

    @Field(DST_PROCESS_CATEGORIES_FIELD_NAME)
    private List<String> dstProcessCategories;

    @Field(DST_PROCESS_CERTIFICATE_ISSUER_FIELD_NAME)
    private String dstProcessCertificateIssuer;

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    protected String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    protected String operationType;

    @Field(USER_NAME_FIELD_NAME)
    protected String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    protected String userDisplayName;

    public ProcessRawEvent() {
    }


    public ProcessRawEvent(ProcessRawEvent other) {
        super(other);
        this.machineId = other.machineId;
        this.machineName = other.machineName;
        this.machineOwner = other.machineOwner;
        this.srcProcessDirectory = other.srcProcessDirectory;
        this.srcProcessFileName = other.srcProcessFileName;
        this.srcProcessDirectoryGroups = other.srcProcessDirectoryGroups;
        this.srcProcessCategories = other.srcProcessCategories;
        this.srcProcessCertificateIssuer = other.srcProcessCertificateIssuer;
        this.dstProcessDirectory = other.dstProcessDirectory;
        this.dstProcessFileName = other.dstProcessFileName;
        this.dstProcessDirectoryGroups = other.dstProcessDirectoryGroups;
        this.dstProcessCategories = other.dstProcessCategories;
        this.dstProcessCertificateIssuer = other.dstProcessCertificateIssuer;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
    }

    public ProcessRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType, String userName, String userDisplayName,
                           Map<String, String> additionalInfo, String machineId, String machineName, String machineOwner, String srcProcessDirectory, String srcProcessFileName, List<String> srcProcessDirectoryGroups, List<String> srcProcessCategories, String srcProcessCertificateIssuer, String dstProcessDirectory, String dstProcessFileName, List<String> dstProcessDirectoryGroups, List<String> dstProcessCategories, String dstProcessCertificateIssuer) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineOwner = machineOwner;
        this.srcProcessDirectory = srcProcessDirectory;
        this.srcProcessFileName = srcProcessFileName;
        this.srcProcessDirectoryGroups = srcProcessDirectoryGroups;
        this.srcProcessCategories = srcProcessCategories;
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
        this.dstProcessDirectory = dstProcessDirectory;
        this.dstProcessFileName = dstProcessFileName;
        this.dstProcessDirectoryGroups = dstProcessDirectoryGroups;
        this.dstProcessCategories = dstProcessCategories;
        this.dstProcessCertificateIssuer = dstProcessCertificateIssuer;
        this.userId = userId;
        this.operationType = operationType;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineOwner() {
        return machineOwner;
    }

    public void setMachineOwner(String machineOwner) {
        this.machineOwner = machineOwner;
    }

    public String getSrcProcessDirectory() {
        return srcProcessDirectory;
    }

    public void setSrcProcessDirectory(String srcProcessDirectory) {
        this.srcProcessDirectory = srcProcessDirectory;
    }

    public String getSrcProcessFileName() {
        return srcProcessFileName;
    }

    public void setSrcProcessFileName(String srcProcessFileName) {
        this.srcProcessFileName = srcProcessFileName;
    }

    public List<String> getSrcProcessDirectoryGroups() {
        return srcProcessDirectoryGroups;
    }

    public void setSrcProcessDirectoryGroups(List<String> srcProcessDirectoryGroups) {
        this.srcProcessDirectoryGroups = srcProcessDirectoryGroups;
    }

    public List<String> getSrcProcessCategories() {
        return srcProcessCategories;
    }

    public void setSrcProcessCategories(List<String> srcProcessCategories) {
        this.srcProcessCategories = srcProcessCategories;
    }

    public String getSrcProcessCertificateIssuer() {
        return srcProcessCertificateIssuer;
    }

    public void setSrcProcessCertificateIssuer(String srcProcessCertificateIssuer) {
        this.srcProcessCertificateIssuer = srcProcessCertificateIssuer;
    }

    public String getDstProcessDirectory() {
        return dstProcessDirectory;
    }

    public void setDstProcessDirectory(String dstProcessDirectory) {
        this.dstProcessDirectory = dstProcessDirectory;
    }

    public String getDstProcessFileName() {
        return dstProcessFileName;
    }

    public void setDstProcessFileName(String dstProcessFileName) {
        this.dstProcessFileName = dstProcessFileName;
    }

    public List<String> getDstProcessDirectoryGroups() {
        return dstProcessDirectoryGroups;
    }

    public void setDstProcessDirectoryGroups(List<String> dstProcessDirectoryGroups) {
        this.dstProcessDirectoryGroups = dstProcessDirectoryGroups;
    }

    public List<String> getDstProcessCategories() {
        return dstProcessCategories;
    }

    public void setDstProcessCategories(List<String> dstProcessCategories) {
        this.dstProcessCategories = dstProcessCategories;
    }

    public String getDstProcessCertificateIssuer() {
        return dstProcessCertificateIssuer;
    }

    public void setDstProcessCertificateIssuer(String dstProcessCertificateIssuer) {
        this.dstProcessCertificateIssuer = dstProcessCertificateIssuer;
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
