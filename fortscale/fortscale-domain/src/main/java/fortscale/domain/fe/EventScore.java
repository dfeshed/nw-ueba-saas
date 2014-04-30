package fortscale.domain.fe;

import fortscale.domain.events.LogEventsEnum;

public class EventScore {

	private LogEventsEnum eventType;
	private long time;
	private String source;
	private String target;
	private String status;
	
	public EventScore(LogEventsEnum eventType, long time, String source, String target, String status) {
		this.eventType = eventType;
		this.time = time;
		this.source = source;
		this.target = target;
		this.status = status;
	}
	
	public LogEventsEnum getEventType() {
		return eventType;
	}
	public void setEventType(LogEventsEnum eventType) {
		this.eventType = eventType;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
