package fortscale.services.event.forward;

import fortscale.common.dataqueries.querydto.DataQueryDTOBase;

/**
 *
 * Uses to hold the configuration of a single forward process
 */
public class ForwardSingleConfiguration {

	//data query to run when using this configuration
	private DataQueryDTOBase dataQueryDTOBase;

	//if false run only once
	private boolean continues;

	// number of times this job had run
	private long runNumber;

	public DataQueryDTOBase getDataQueryDTO() {
		return dataQueryDTOBase;
	}

	public void setDataQueryDTO(DataQueryDTOBase dataQueryDTOBase) {
		this.dataQueryDTOBase = dataQueryDTOBase;
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
