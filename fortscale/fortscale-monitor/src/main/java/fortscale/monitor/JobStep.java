package fortscale.monitor;

import java.util.Date;

public class JobStep {

	private int ordinal;
	private String stepName;
	private Date start;
	private Date finish;
	
	public JobStep() {}
	
	public JobStep(String stepName) {
		this.setStepName(stepName);
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getFinish() {
		return finish;
	}

	public void setFinish(Date finish) {
		this.finish = finish;
	}
	
}
