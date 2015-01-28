package fortscale.services.computer;

import fortscale.services.CachingService;

import java.io.IOException;

public interface SensitiveMachineService extends CachingService {
	
	boolean isMachineSensitive(String machineName);
	void refreshSensitiveMachines();
	void updateSensitiveMachines() throws IOException;
}
