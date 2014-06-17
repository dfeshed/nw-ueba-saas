package fortscale.domain.core;

public class AbstractAnalystAuditableDocument extends AbstractAuditableDocument {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6629391658169457969L;
	private String createdBy;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
