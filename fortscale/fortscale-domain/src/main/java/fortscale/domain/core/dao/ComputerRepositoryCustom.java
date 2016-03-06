package fortscale.domain.core.dao;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageType;

public interface ComputerRepositoryCustom {

	Date getLatestWhenChanged();
	void updateSensitiveMachine(Computer computer, boolean isSensitiveMachine);
	List<Computer> getComputersFromNames(List<String> machinesNames);
	List<Computer> getComputersOfType(ComputerUsageType type, int limit);
	List<String> findNameByIsSensitive(Boolean isSensitiveMachine);
	long getNumberOfSensitiveMachines();
	long getNumberOfMachinesOfType(ComputerUsageType type);
	long getNumberOfMachinesOfTypeBeforeTime(ComputerUsageType type,
			DateTime time);
	long getNumberOfSensitiveMachinesBeforeTime(DateTime time);
	long getNumberOfMachinesBeforeTime(DateTime time);
	
	List<Computer> getComputersWithPartialFields(List<String> machineNames, String... includeFields);
	Computer getComputerWithPartialFields(String machineName, String... includeFields);
	public void updateSensitiveMachineByName(String machineName, boolean isSensitive);
	public boolean findIfComputerExists(String computerName);

	public List<Computer> findByFilters(String nameContains, String distinguishedNameContains, String fields, String usageTypes, String usageTypesAnd, Integer limit);
}
