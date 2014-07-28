package fortscale.collection.tagging.service;

import java.io.IOException;

public interface SensitiveMachineService {
	
	boolean isMachineSensitive(String machineName);
	void refreshSensitiveMachines();
	void updateSensitiveMachines() throws IOException;
}
