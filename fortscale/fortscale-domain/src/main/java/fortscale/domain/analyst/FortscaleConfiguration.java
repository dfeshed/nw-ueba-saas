package fortscale.domain.analyst;

import org.springframework.data.mongodb.core.mapping.Document;

import fortscale.domain.core.AbstractAnalystAuditableDocument;






//@Document(collection="fortscale_configuration")
public class FortscaleConfiguration extends AbstractAnalystAuditableDocument{
	
	
	private String configId;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}
	
}
