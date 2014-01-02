package fortscale.monitor.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="job_report")
@TypeAlias(value="JobReport")
public class JobReport {

	@Id
	private String id;
	private String jobName;
	private String sourceType;
	// expire report after 20 days
	@Indexed(unique=false, expireAfterSeconds=60*60*24*20, background=true)
	private Date start;
	private Date finish;
	private List<JobStep> steps;
	private boolean hasErrors;
	private boolean hasWarnings;
	private List<JobDataReceived> dataReceived;
	private int totalExceptedSteps;
	

	public JobReport() {}
	
	public JobReport(String sourceType, String jobName) {
		this.setSourceType(sourceType);
		this.setJobName(jobName);
		this.hasErrors = false;
		this.hasWarnings = false;
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
			steps = new LinkedList<JobStep>();
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

	public boolean isHasErrors() {
		return hasErrors;
	}

	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public boolean isHasWarnings() {
		return hasWarnings;
	}

	public void setHasWarnings(boolean hasWarnings) {
		this.hasWarnings = hasWarnings;
	}

	public List<JobDataReceived> getDataReceived() {
		if (dataReceived==null) {
			dataReceived = new LinkedList<JobDataReceived>();
		}
		return dataReceived;
	}

	public void setDataReceived(List<JobDataReceived> dataReceived) {
		this.dataReceived = dataReceived;
	}

	public int getTotalExceptedSteps() {
		return totalExceptedSteps;
	}

	public void setTotalExceptedSteps(int totalExceptedSteps) {
		this.totalExceptedSteps = totalExceptedSteps;
	}


	
}
