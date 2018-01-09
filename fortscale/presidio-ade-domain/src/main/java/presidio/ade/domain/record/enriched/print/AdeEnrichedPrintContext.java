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
    private String operationType;
    private String srcMachineId;
    private String srcMachineNameRegexCluster;
    private String dstMachineId;
    private String dstMachineNameRegexCluster;
    private String fileExtension;
    private EventResult result;

    public AdeEnrichedPrintContext() {
        super();
    }

    public AdeEnrichedPrintContext(EnrichedPrintRecord enrichedPrintRecord) {
        super(enrichedPrintRecord.getEventId());
        this.userId = enrichedPrintRecord.getUserId();
        this.operationType = enrichedPrintRecord.getOperationType();
        this.srcMachineId = enrichedPrintRecord.getSrcMachineId();
        this.srcMachineNameRegexCluster = enrichedPrintRecord.getSrcMachineNameRegexCluster();
        this.dstMachineId = enrichedPrintRecord.getDstMachineId();
        this.dstMachineNameRegexCluster = enrichedPrintRecord.getDstMachineNameRegexCluster();
        this.fileExtension = enrichedPrintRecord.getFileExtension();
        this.result = enrichedPrintRecord.getResult();
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

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }
}
