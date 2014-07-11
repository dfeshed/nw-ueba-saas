package fortscale.services.computer;

import fortscale.domain.core.Computer;

public interface EndpointDetectionService {
	
	/**
	 * Attempts to classify the given computer as end-point or server.
	 * Classification results will be updated in the computer instance.
	 * @return a value indicating if a change to computer classification was made 
	 */
	boolean classifyComputer(Computer computer);
	
	/**
	 * Classify a new computer using configuration based heuristics only
	 */
	boolean classifyNewComputer(Computer computer);
}
