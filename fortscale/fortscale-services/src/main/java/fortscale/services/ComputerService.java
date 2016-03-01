package fortscale.services;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.ComputerUsageType;

public interface ComputerService extends CachingService{

	/**
	 * Determines if the computer hostname is taken from active directory
	 * @return true in case it is, false otherwise or in case the hostname is not recognized
	 */
	boolean isHostnameInAD(String hostname);

	/**
	 * Gets the computer object's mongo id
	 * @return id
	 */
	String getComputerId(String hostname);

	void updateComputerWithADInfo(AdComputer computer);
	
	ComputerUsageType getComputerUsageType(String hostname);
	
	String getDomainNameForHostname(String hostname);

	/**
	 * Run classification on all computers
	 */
	void classifyAllComputers();
	
	/**
	 * Ensure we have a computer instance for the given host name. 
	 * This method will create a computer if it doesn't exists
	 */
	void ensureComputerExists(String hostname);
}
