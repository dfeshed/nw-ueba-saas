package fortscale.services.event.forward;

import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="forward_configuration")
public class ForwardConfiguration extends AbstractAuditableDocument{

	private static final long serialVersionUID = -3731751327585262212L;

	@Indexed
	private String type;

	private List<ForwardSingleConfiguration> confList;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ForwardSingleConfiguration> getConfList() {
		return confList;
	}

	public void setConfList(List<ForwardSingleConfiguration> confList) {
		this.confList = confList;
	}
}
