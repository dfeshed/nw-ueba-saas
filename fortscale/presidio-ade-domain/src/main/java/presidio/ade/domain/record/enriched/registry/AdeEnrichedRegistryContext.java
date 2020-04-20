package presidio.ade.domain.record.enriched.registry;

import presidio.ade.domain.record.enriched.BaseEnrichedContext;
import java.util.List;

public class AdeEnrichedRegistryContext extends BaseEnrichedContext {

    private String userId;
    private String machineId;
    private String machineOwner;
    private String processDirectory;
    private String processFileName;
    private List<String> processDirectoryGroups;
    private List<String> processCategories;
    private String processCertificateIssuer;
    private String registryKeyGroup;
    private String registryKey;
    private String registryValueName;

    public AdeEnrichedRegistryContext() {
        super();
    }

    public AdeEnrichedRegistryContext(EnrichedRegistryRecord enrichedRegistryRecord) {
        super(enrichedRegistryRecord.getEventId());
        this.userId = enrichedRegistryRecord.getUserId();
        this.machineId = enrichedRegistryRecord.getMachineId();
        this.machineOwner = enrichedRegistryRecord.getMachineOwner();
        this.processDirectory = enrichedRegistryRecord.getProcessDirectory();
        this.processFileName = enrichedRegistryRecord.getProcessFileName();
        this.processDirectoryGroups = enrichedRegistryRecord.getProcessDirectoryGroups();
        this.processCategories = enrichedRegistryRecord.getProcessCategories();
        this.processCertificateIssuer = enrichedRegistryRecord.getProcessCertificateIssuer();
        this.registryKeyGroup = enrichedRegistryRecord.getRegistryKeyGroup();
        this.registryKey = enrichedRegistryRecord.getRegistryKey();
        this.registryValueName = enrichedRegistryRecord.getRegistryValueName();
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
}

