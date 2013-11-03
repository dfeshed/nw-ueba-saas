package fortscale.domain.core;

public class AbstractAnalystAuditableDocument extends AbstractAuditableDocument {
	
	
	private String createdBy;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
