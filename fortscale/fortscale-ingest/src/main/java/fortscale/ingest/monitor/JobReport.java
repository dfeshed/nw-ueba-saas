package fortscale.ingest.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;


public class JobReport {

	@Id
	private String id;
	private String jobName;
	private String sourceType;
	private Date start;
	private Date finish;
	private List<JobStep> steps;

	public JobReport() {}
	
	public JobReport(String sourceType, String jobName) {
		this.setSourceType(sourceType);
		this.setJobName(jobName);
	}
	
	
	@Override
	public String toString() {
		return String.format(
				"JobReport[id=%s. jobName=%s, sourceType=%s, start=%tc, finish=%tc",
				getId(), getJobName(), getSourceType(), getStart(), getFinish());
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
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


	public List<JobStep> getSteps() {
		if (steps == null) {
			steps = new ArrayList<JobStep>();
		}
		return steps;
	}
	
	public JobStep findStep(String stepName) {
		List<JobStep> steps = getSteps();
		for (JobStep step : steps) {
			if (step.getStepName().equals(stepName))
				return step;
		}
		return null;
	}

	public void setSteps(List<JobStep> steps) {
		this.steps = steps;
	}


	
}
