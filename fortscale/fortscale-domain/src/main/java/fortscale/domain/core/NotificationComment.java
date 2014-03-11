package fortscale.domain.core;

import java.util.Date;
import java.util.UUID;

public class NotificationComment  {

	private String analystId;
	private String analystDisplayName;
	private String message;
	private Date when;
	private Long id;
	private Long basedOn;
	
	public NotificationComment(String analyst, String analystDisplayName, Date when, String message) {
		this(analyst, analystDisplayName, when, message, null);
	}

	public NotificationComment(String analyst, String analystDisplayName, Date when, String message, Long basedOn) {
		this.setanalystId(analyst);
		this.setAnalystDisplayName(analystDisplayName);
		this.setWhen(when);
		this.setMessage(message);
		this.setId(UUID.randomUUID().getLeastSignificantBits());
		this.basedOn = basedOn;
	}
	
	public String getanalystId() {
		return analystId;
	}

	public void setanalystId(String analystId) {
		this.analystId = analystId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(Long basedOn) {
		this.basedOn = basedOn;
	}

	public String getAnalystDisplayName() {
		return analystDisplayName;
	}

	public void setAnalystDisplayName(String analystDisplayName) {
		this.analystDisplayName = analystDisplayName;
	}
	
}
