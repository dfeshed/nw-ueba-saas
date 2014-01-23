package fortscale.monitor.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobStep {

	private int ordinal;
	private String stepName;
	private Date start;
	private Date finish;
	private List<JobMessage> messages;
	
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
	
	public List<JobMessage> getMessages() {
		if (messages == null) {
			messages = new ArrayList<JobMessage>();
		}
		return messages;
	}
	
	public void setMessages(List<JobMessage> messages) {
		this.messages = messages;
	}
	
	public void addMessages(JobMessage message) {
		List<JobMessage> messages = getMessages();
		messages.add(message);
	}
	
}
