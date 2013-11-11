package fortscale.domain.analyst;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractAuditableDocument;






@Document(collection="fortscale_configuration")
public class FortscaleConfiguration extends AbstractAuditableDocument{
	
	@Indexed
	private String configId = "score";
	@Field("conf")
	private Object confObj;
	
	
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

	public Object getConfObj() {
		return confObj;
	}

	public void setConfObj(Object confObj) {
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
