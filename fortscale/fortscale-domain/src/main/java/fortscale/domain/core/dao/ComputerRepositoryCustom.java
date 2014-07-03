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
	List<String> findNameByIsSensitive(Boolean isSensitiveMachine);
	long getNumberOfSensitiveMachines();
	long getNumberOfMachinesOfType(ComputerUsageType type);
	long getNumberOfMachinesOfTypeBeforeTime(ComputerUsageType type,
			DateTime time);
	long getNumberOfSensitiveMachinesBeforeTime(DateTime time);
	long getNumberOfMachines();
	long getNumberOfMachinesBeforeTime(DateTime time);
}
