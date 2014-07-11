package fortscale.services;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.ComputerUsageType;

public interface ComputerService {

	void updateComputerWithADInfo(AdComputer computer);
	
	ComputerUsageType getComputerUsageType(String hostname);
	
	/**
	 * Gets the cluster group name for the given hostname. The cluster 
	 * group name is a virtual name used to depict all hosts that are 
	 * part of a cluster of hosts and serve a common functionality 
	 * in the system.
	 */
	String getClusterGroupNameForHostname(String hostname);
	
	/**
	 * Run classification on all computers
	 */
	void classifyAllComputers();
}
