package fortscale.collection.tagging.service;

public interface SensitiveMachineService {
	
	boolean isMachineSensitive(String machineName);
	void refreshSensitiveMachines();
	void updateSensitiveMachines();
}
