package fortscale.services.fe;

import java.util.Date;

public class FeAttribute {
	private String name;
	private String value;
	private Long revision;
	private Date timestamp;
	
	
	public FeAttribute(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Long getRevision() {
		return revision;
	}
	public void setRevision(Long revision) {
		this.revision = revision;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
