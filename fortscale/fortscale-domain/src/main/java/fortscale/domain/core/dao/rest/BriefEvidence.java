package fortscale.domain.core.dao.rest;

/**
 * Created by shays on 01/07/2015.
 *
 * This DTO is subset of evidence.
 * Used to fetch and return only certain attributes of evidence
 */
public class BriefEvidence {

    private String evidenceId;
    private String evidenceName;

    public BriefEvidence(String evidenceId, String evidenceName) {
        this.evidenceId = evidenceId;
        this.evidenceName = evidenceName;
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(String evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public void setEvidenceName(String evidenceName) {
        this.evidenceName = evidenceName;
    }
}