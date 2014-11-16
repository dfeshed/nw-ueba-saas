package fortscale.domain.events.dao;

import fortscale.domain.events.DhcpEvent;

public interface DhcpEventRepositoryCustom {

	/**
	 * Gets the latest ip assignment event for a computer that occurred before the given timestamp
	 */
	DhcpEvent findLatestEventForComputerBeforeTimestamp(String ipAddress, String hostname, long timestamp);
}
