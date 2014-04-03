package fortscale.services.machine;

public interface EndpointDetectionService {

	/**
	 * Get the machine info structure according to hostname
	 */
	MachineInfo getMachineInfo(String hostname);
}
