package presidio.output.domain.records.events;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class RegistryEnrichedEvent extends EnrichedEvent {

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
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";


    @Field(MACHINE_ID_FIELD_NAME)
    private String machineId;

    @Field(MACHINE_NAME_FIELD_NAME)
    private String machineName;

    @Field(MACHINE_OWNER_FIELD_NAME)
    private String machineOwner;

    @Field(PROCESS_DIRECTORY_FIELD_NAME)
    private String processDirectory;

    @Field(PROCESS_FILE_NAME_FIELD_NAME)
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
    private String registryKey;

    @Field(REGISTRY_VALUE_NAME_FIELD_NAME)
    private String registryValueName;

    @Field(OPERATION_TYPE_FIELD_NAME)
    private String operationType;


    public RegistryEnrichedEvent() {
    }

    public RegistryEnrichedEvent(Instant createdDate, Instant eventDate, String eventId, String schema, String userId, String userName, String userDisplayName, String dataSource, String operationType, Map<String, String> additionalInfo, String machineId, String machineName, String machineOwner, String processDirectory, String processFileName, List<String> processDirectoryGroups, List<String> processCategories, String processCertificateIssuer, String registryKeyGroup, String registryKey, String registryValueName) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, additionalInfo);
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
        this.operationType = operationType;
    }

    public RegistryEnrichedEvent(String machineId, String machineName, String machineOwner,
                                 String processDirectory, String processFileName, List<String> processDirectoryGroups,
                                 List<String> processCategories, String processCertificateIssuer, String registryKeyGroup,
                                 String registryKey, String registryValueName, String operationType) {
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
        this.operationType = operationType;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
