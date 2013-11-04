package fortscale.domain.analyst;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractAnalystAuditableDocument;






@Document(collection="fortscale_configuration")
public class FortscaleConfiguration extends AbstractAnalystAuditableDocument{
	
	
	private String configId = "score";
	@Field("conf")
	private ScoreConfiguration confObj;
	
	

	public ScoreConfiguration getConfObj() {
		return confObj;
	}

	public void setConfObj(ScoreConfiguration confObj) {
		this.confObj = confObj;
	}

	public String getConfigId() {
		return configId;
	}

//	public void setConfigId(String configId) {
//		this.configId = configId;
//	}
	
}
