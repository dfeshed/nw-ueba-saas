package fortscale.domain.analyst;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractAuditableDocument;






@Document(collection="fortscale_configuration")
public class FortscaleConfiguration extends AbstractAuditableDocument{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3731751327585262212L;
	@Indexed
	private String configId = "score";
	@Field("conf")
	private ScoreConfiguration confObj;
	
	
	@Indexed
	private String createdById;
	@Indexed
	private String createdByUsername;
	
	
	public FortscaleConfiguration(String configId){
		this.configId = configId;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(String createdBy) {
		this.createdById = createdBy;
	}

	public ScoreConfiguration getConfObj() {
		return confObj;
	}

	public void setConfObj(ScoreConfiguration confObj) {
		this.confObj = confObj;
	}

	public String getConfigId() {
		return configId;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(String createdByUsername) {
		this.createdByUsername = createdByUsername;
	}

	
	
}
