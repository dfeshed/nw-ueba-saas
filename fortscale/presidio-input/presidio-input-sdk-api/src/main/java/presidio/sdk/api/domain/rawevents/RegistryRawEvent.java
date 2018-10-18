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
public class RegistryRawEvent extends AbstractInputDocument {

    public static final String MACHINE_ID_FIELD_NAME = "machineId";
    public static final String MACHINE_NAME_FIELD_NAME = "machineName";
    public static final String MACHINE_OWNER_FIELD_NAME = "machineOwner";
    public static final String PROCESS_DIRECTORY_FIELD_NAME = "processDirectory";
    public static final String PROCESS_FILE_NAME_FIELD_NAME = "processFileName";
    public static final String PROCESS_DIRECTORY_GROUPS_FIELD_NAME = "processDirectoryGroups";
    public static final String PROCESS_CATEGORIES_FIELD_NAME = "processCategories";
    public static final String PROCESS_CERTIFICATE_ISSUER_FIELD_NAME = "processCertificateIssuer";
    public static final String REGISTRY_KEY_GROUP_FIELD_NAME = "registryKeyGroup";
    public static final String REGISTRY_KEY_FIELD_NAME = "registryKey";
    public static final String REGISTRY_VALUE_NAME_FIELD_NAME = "registryValueName";
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

    @Field(PROCESS_DIRECTORY_FIELD_NAME)
    @NotEmpty
    private String processDirectory;

    @Field(PROCESS_FILE_NAME_FIELD_NAME)
    @NotEmpty
    private String processFileName;

    @Field(PROCESS_DIRECTORY_GROUPS_FIELD_NAME)
    private List<String> processDirectoryGroups;

    @Field(PROCESS_CATEGORIES_FIELD_NAME)
    private List<String> processCategories;

    @Field(PROCESS_CERTIFICATE_ISSUER_FIELD_NAME)
    private String processCertificateIssuer;

    @Field(REGISTRY_KEY_GROUP_FIELD_NAME)
    private String registryKeyGroup;

    @Field(REGISTRY_KEY_FIELD_NAME)
    @NotEmpty
    private String registryKey;

    @Field(REGISTRY_VALUE_NAME_FIELD_NAME)
    @NotEmpty
    private String registryValueName;

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    private String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    private String operationType;

    @Field(USER_NAME_FIELD_NAME)
    private String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    private String userDisplayName;

    public RegistryRawEvent() {
        super();
    }

    public RegistryRawEvent(RegistryRawEvent other) {
        super(other);
        this.machineId = other.machineId;
        this.machineName = other.machineName;
        this.machineOwner = other.machineOwner;
        this.processDirectory = other.processDirectory;
        this.processFileName = other.processFileName;
        this.processDirectoryGroups = other.processDirectoryGroups;
        this.processCategories = other.processCategories;
        this.processCertificateIssuer = other.processCertificateIssuer;
        this.registryKeyGroup = other.registryKeyGroup;
        this.registryKey = other.registryKey;
        this.registryValueName = other.registryValueName;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
    }


    public RegistryRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType, String userName, String userDisplayName, Map<String, String> additionalInfo, String machineId, String machineName, String machineOwner, String processDirectory, String processFileName, List<String> processDirectoryGroups, List<String> processCategories, String processCertificateIssuer, String registryKeyGroup, String registryKey, String registryValueName) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.machineId = machineId;
        this.machineName = machineName;
        this.machineOwner = machineOwner;
        this.processDirectory = processDirectory;
        this.processFileName = processFileName;
        this.processDirectoryGroups = processDirectoryGroups;
        this.processCategories = processCategories;
        this.processCertificateIssuer = processCertificateIssuer;
        this.registryKeyGroup = registryKeyGroup;
        this.registryKey = registryKey;
        this.registryValueName = registryValueName;
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

    public String getProcessDirectory() {
        return processDirectory;
    }

    public void setProcessDirectory(String processDirectory) {
        this.processDirectory = processDirectory;
    }

    public String getProcessFileName() {
        return processFileName;
    }

    public void setProcessFileName(String processFileName) {
        this.processFileName = processFileName;
    }

    public List<String> getProcessDirectoryGroups() {
        return processDirectoryGroups;
    }

    public void setProcessDirectoryGroups(List<String> processDirectoryGroups) {
        this.processDirectoryGroups = processDirectoryGroups;
    }

    public List<String> getProcessCategories() {
        return processCategories;
    }

    public void setProcessCategories(List<String> processCategories) {
        this.processCategories = processCategories;
    }

    public String getProcessCertificateIssuer() {
        return processCertificateIssuer;
    }

    public void setProcessCertificateIssuer(String processCertificateIssuer) {
        this.processCertificateIssuer = processCertificateIssuer;
    }

    public String getRegistryKeyGroup() {
        return registryKeyGroup;
    }

    public void setRegistryKeyGroup(String registryKeyGroup) {
        this.registryKeyGroup = registryKeyGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValueName() {
        return registryValueName;
    }

    public void setRegistryValueName(String registryValueName) {
        this.registryValueName = registryValueName;
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
