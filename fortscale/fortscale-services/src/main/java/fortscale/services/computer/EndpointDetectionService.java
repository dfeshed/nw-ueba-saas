package fortscale.services.computer;

import fortscale.domain.core.Computer;

public interface EndpointDetectionService {

	/**
	 * Get the machine info structure according to hostname
	 */
	MachineInfo getMachineInfo(String hostname);
	
	/**
	 * Attempts to classify the given computer as end-point or server.
	 * Classification results will be updated in the computer instance 
	 */
	void classifyComputer(Computer computer);
}
