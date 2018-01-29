package presidio.ade.domain.record.enriched.print;

import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

/**
 * The context of the enriched Print record.
 *
 * @author Lior Govrin
 */
public class AdeEnrichedPrintContext extends BaseEnrichedContext {
    private String userId;
    private String srcMachineId;
    private String srcMachineNameRegexCluster;
    private String dstMachineId;
    private String dstMachineNameRegexCluster;
    private String absoluteFolderPath;
    private String fileExtension;
    private Boolean driveShared;
    private Long numOfPages;
    private EventResult result;

    public AdeEnrichedPrintContext() {
        super();
    }

    public AdeEnrichedPrintContext(EnrichedPrintRecord enrichedPrintRecord) {
        super(enrichedPrintRecord.getEventId());
        this.userId = enrichedPrintRecord.getUserId();
        this.srcMachineId = enrichedPrintRecord.getSrcMachineId();
        this.srcMachineNameRegexCluster = enrichedPrintRecord.getSrcMachineNameRegexCluster();
        this.dstMachineId = enrichedPrintRecord.getDstMachineId();
        this.dstMachineNameRegexCluster = enrichedPrintRecord.getDstMachineNameRegexCluster();
        this.absoluteFolderPath = enrichedPrintRecord.getAbsoluteFolderPath();
        this.fileExtension = enrichedPrintRecord.getFileExtension();
        this.driveShared = enrichedPrintRecord.getDriveShared();
        this.numOfPages = enrichedPrintRecord.getNumOfPages();
        this.result = enrichedPrintRecord.getResult();
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

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public String getAbsoluteFolderPath() {
        return absoluteFolderPath;
    }

    public void setAbsoluteFolderPath(String absoluteFolderPath) {
        this.absoluteFolderPath = absoluteFolderPath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Boolean getDriveShared() {
        return driveShared;
    }

    public void setDriveShared(Boolean driveShared) {
        this.driveShared = driveShared;
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
}
