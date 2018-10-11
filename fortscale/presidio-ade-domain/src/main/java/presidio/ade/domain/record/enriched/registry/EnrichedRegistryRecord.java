package presidio.ade.domain.record.enriched.registry;

import fortscale.common.general.Schema;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;


import java.util.List;

/**
 * The enriched Registry record POJO.
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.REGISTRY)
public class EnrichedRegistryRecord extends EnrichedRecord {

    public static final String USER_ID_FIELD = "userId";
    public static final String MACHINE_ID_FIELD = "machineId";
    public static final String PROCESS_DIRECTORY_FIELD = "processDirectory";
    public static final String PROCESS_FILE_NAME_FIELD = "processFileName";
    public static final String PROCESS_DIRECTORY_GROUPS_FIELD = "processDirectoryGroups";
    public static final String PROCESS_CATEGORIES_FIELD = "processCategories";
    public static final String PROCESS_CERTIFICATE_ISSUER_FIELD = "processCertificateIssuer";
    public static final String REGISTRY_KEY_GROUP_FIELD = "registryKeyGroup";
    public static final String REGISTRY_KEY_FIELD = "registryKey";
    public static final String REGISTRY_VALUE_NAME_FIELD = "registryValueName";


    @Field(USER_ID_FIELD)
    private String userId;
    @Field(MACHINE_ID_FIELD)
    private String machineId;
    @Field(PROCESS_DIRECTORY_FIELD)
    private String processDirectory;
    @Field(PROCESS_FILE_NAME_FIELD)
    private String processFileName;
    @Field(PROCESS_DIRECTORY_GROUPS_FIELD)
    private List<String> processDirectoryGroups;
    @Field(PROCESS_CATEGORIES_FIELD)
    private List<String> processCategories;
    @Field(PROCESS_CERTIFICATE_ISSUER_FIELD)
    private String processCertificateIssuer;
    @Field(REGISTRY_KEY_GROUP_FIELD)
    private String registryKeyGroup;
    @Field(REGISTRY_KEY_FIELD)
    private String registryKey;
    @Field(REGISTRY_VALUE_NAME_FIELD)
    private String registryValueName;


    /**
     * C'tor.
     *
     * @param startInstant The record's logical date and time
     */
    public EnrichedRegistryRecord(Instant startInstant) {
        super(startInstant);
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.REGISTRY.getName();
    }

    @Transient
    public AdeEnrichedRegistryContext getContext() {
        return new AdeEnrichedRegistryContext(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
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
}
