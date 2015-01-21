package fortscale.services;

import java.io.IOException;

public interface SensitiveMachineService extends CachingService {
	
	boolean isMachineSensitive(String machineName);
	void refreshSensitiveMachines();
	void updateSensitiveMachines() throws IOException;
}
