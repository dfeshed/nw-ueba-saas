package fortscale.domain.system;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import fortscale.domain.core.AbstractAuditableDocument;


@Document
public class SystemConfiguration extends AbstractAuditableDocument{
	private static final long serialVersionUID = -4311416194060184710L;
	
	public static final String typeFieldName = "type";
	public static final String confFieldName = "conf";
	
	@Indexed
	private String type;
	
	@Field(confFieldName)
	private Object conf;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getConf() {
		return conf;
	}

	public void setConf(Object conf) {
		this.conf = conf;
	}
	
	
}
