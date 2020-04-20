package presidio.ade.domain.record.enriched.process;

import presidio.ade.domain.record.enriched.BaseEnrichedContext;

import java.util.List;


public class AdeEnrichedProcessContext extends BaseEnrichedContext {

    private String userId;
    private String machineId;
    private String machineOwner;
    private String srcProcessDirectory;
    private String srcProcessFileName;
    private List<String> srcProcessDirectoryGroups;
    private List<String> srcProcessCategories;
    private String srcProcessCertificateIssuer;
    private String dstProcessDirectory;
    private String dstProcessFileName;
    private List<String> dstProcessDirectoryGroups;
    private List<String> dstProcessCategories;
    private String dstProcessCertificateIssuer;


    public AdeEnrichedProcessContext() {
        super();
    }

    public AdeEnrichedProcessContext(EnrichedProcessRecord enrichedProcessRecord) {
        super(enrichedProcessRecord.getEventId());
        this.userId = enrichedProcessRecord.getUserId();
        this.machineId = enrichedProcessRecord.getMachineId();
        this.machineOwner = enrichedProcessRecord.getMachineOwner();
        this.srcProcessDirectory = enrichedProcessRecord.getSrcProcessDirectory();
        this.srcProcessFileName = enrichedProcessRecord.getSrcProcessFileName();
        this.srcProcessDirectoryGroups = enrichedProcessRecord.getSrcProcessDirectoryGroups();
        this.srcProcessCategories = enrichedProcessRecord.getSrcProcessCategories();
        this.srcProcessCertificateIssuer = enrichedProcessRecord.getSrcProcessCertificateIssuer();
        this.dstProcessDirectory = enrichedProcessRecord.getDstProcessDirectory();
        this.dstProcessFileName = enrichedProcessRecord.getDstProcessFileName();
        this.dstProcessDirectoryGroups = enrichedProcessRecord.getDstProcessDirectoryGroups();
        this.dstProcessCategories = enrichedProcessRecord.getDstProcessCategories();
        this.dstProcessCertificateIssuer = enrichedProcessRecord.getDstProcessCertificateIssuer();
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
}
