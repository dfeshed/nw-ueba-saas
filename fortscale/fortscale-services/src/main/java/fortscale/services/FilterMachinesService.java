package fortscale.services;

public interface FilterMachinesService {
	boolean toFilter(String computerName);
	void invalidateKey(String computerName);
}
