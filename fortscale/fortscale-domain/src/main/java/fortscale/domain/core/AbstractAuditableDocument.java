package fortscale.domain.core;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;

import org.springframework.data.mongodb.core.mapping.Field;






public class AbstractAuditableDocument extends AbstractDocument{

	@Indexed
	@Field("created")
	private Date created;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(", ");
		sb.append("Created: ").append(getCreated());
		return sb.toString();
	}
     
}
