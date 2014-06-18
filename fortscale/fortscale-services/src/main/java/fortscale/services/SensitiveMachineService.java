package fortscale.services;

public interface SensitiveMachineService {
	
	boolean isMachineSensitive(String machineName);
	void refreshSensitiveMachines();
	void updateSensitiveMachines();
}
