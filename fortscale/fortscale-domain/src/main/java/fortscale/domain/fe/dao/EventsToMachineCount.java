package fortscale.domain.fe.dao;

/**
 * Counts the number of events for a specific hostname. 
 * Usage of this DTO class can specify if the machine is source or target
 */
public class EventsToMachineCount {

	private String hostname;
	private int eventsCount;
	
	public EventsToMachineCount(String hostname, int eventsCount) {
		this.setHostname(hostname);
		this.setEventsCount(eventsCount);
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getEventsCount() {
		return eventsCount;
	}

	public void setEventsCount(int eventsCount) {
		this.eventsCount = eventsCount;
	}
	
	public void incEventsCount(int count) {
		this.eventsCount += count;
	}
	
}
