package fortscale.domain.system;

import fortscale.domain.core.AbstractAuditableDocument;

public class DcSystemConfiguraion extends AbstractAuditableDocument{
	private static final long serialVersionUID = -4566423835774705851L;

	
	private String type;
	
	private DcConfiguration conf;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DcConfiguration getConf() {
		return conf;
	}

	public void setConf(DcConfiguration conf) {
		this.conf = conf;
	}
}
