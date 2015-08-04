package fortscale.services.computer.filtering;

public interface FilterMachinesService {
	boolean toFilter(String computerName);
	void invalidateKey(String computerName);
}
