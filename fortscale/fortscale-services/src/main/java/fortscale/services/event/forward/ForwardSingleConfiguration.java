package fortscale.services.event.forward;

import fortscale.services.dataqueries.querydto.DataQueryDTO;

/**
 * Created by danal on 19/05/2015.
 * Uses to hold the configuration of a single forward process
 */
public class ForwardSingleConfiguration {

	private DataQueryDTO dataQueryDTO;

	private boolean continues;

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
