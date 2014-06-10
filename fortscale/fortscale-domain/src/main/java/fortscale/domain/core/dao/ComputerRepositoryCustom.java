package fortscale.domain.core.dao;

import java.util.Date;

import fortscale.domain.core.Computer;

public interface ComputerRepositoryCustom {

	Date getLatestWhenChanged();
	void updateSensitiveMachine(Computer computer, boolean isSensitiveMachine);
}
