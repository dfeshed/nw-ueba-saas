package fortscale.services.event.forward;

import fortscale.services.dataqueries.querydto.DataQueryDTO;

/**
 *
 * Uses to hold the configuration of a single forward process
 */
public class ForwardSingleConfiguration {

	//data query to run when using this configuration
	private DataQueryDTO dataQueryDTO;

	//if false run only once
	private boolean continues;

	// number of times this job had run
	private long runNumber;

	public DataQueryDTO getDataQueryDTO() {
		return dataQueryDTO;
	}

	public void setDataQueryDTO(DataQueryDTO dataQueryDTO) {
		this.dataQueryDTO = dataQueryDTO;
	}

	public boolean isContinues() {
		return continues;
	}

	public void setContinues(boolean continues) {
		this.continues = continues;
	}

	public long getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(long runNumber) {
		this.runNumber = runNumber;
	}
}
