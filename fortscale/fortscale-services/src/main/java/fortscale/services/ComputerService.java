package fortscale.services;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.ComputerUsageType;

public interface ComputerService {

	void updateComputerWithADInfo(AdComputer computer);
	
	ComputerUsageType getComputerUsageType(String hostname);
}
