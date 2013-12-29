package fortscale.monitor.mongo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.monitor.JobMessage;
import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.JobReport;
import fortscale.monitor.JobStep;

@Component
public class MongoJobProgressReporter implements JobProgressReporter {

	private static Logger logger = LoggerFactory.getLogger(MongoJobProgressReporter.class);
	
	@Autowired
	private JobReportRepository repository;
		
	public MongoJobProgressReporter() {}
	
	public MongoJobProgressReporter(JobReportRepository repository) {
		this.repository = repository;
	}
	
	
	@Override
	public JobReport getByID(String id) {
		if (id == null) {
			logger.warn("getByID called with null id");
			return null;
		}
		
		return repository.findOne(id);
	}

	@Override
	public String startJob(String sourceType, String jobName) {
		
		if ((sourceType ==null) || (jobName == null)) {
			logger.warn(String.format("startJob called with sourceType=%s, jobName=%s", sourceType, jobName));
			return null;
		}
		
		JobReport report = new JobReport();
		report.setJobName(jobName);
		report.setSourceType(sourceType);
		report.setStart(new Date());
		
		JobReport saved = repository.save(report);
		return saved.getId();
	}

	@Override
	public void finishJob(String id) {
		
		if (id==null) {
			logger.warn("finishJob called with id null");
			return;
		}

		JobReport report = repository.findOne(id);
		if (report!=null) {
			report.setFinish(new Date());
			
			repository.save(report);
		} else {
			logger.warn("job with id=%s not found", id);
		}
		
	}

	@Override
	public void startStep(String id, String stepName, int ordinal) {
		if ((id==null) || (stepName==null)) {
			logger.warn(String.format("startStep called with id=%s, stepName=%s, ordinal=%d", id, stepName, ordinal));
			return;
		}
			
		
		JobReport report = repository.findOne(id);
		if (report!=null) {
			JobStep step = new JobStep(stepName);
			step.setOrdinal(ordinal);
			step.setStart(new Date());
			
			report.getSteps().add(step);
			repository.save(report);
		} else {
			logger.warn(String.format("cannot find job with id=%s", id));
		}
	}

	@Override
	public void finishStep(String id, String stepName) {
		if ((id==null) || (stepName==null)) {
			logger.warn(String.format("finishStep called with id=%s, stepName=%s", id, stepName));
			return;
		}
		
		JobReport report = repository.findOne(id);
		if (report!=null) {
			JobStep step = report.findStep(stepName);
			if (step!=null) {
				step.setFinish(new Date());
				
				repository.save(report);
			} else {
				logger.warn(String.format("step %s not found in job %s", stepName, id));
			}
			
		} else {
			logger.warn(String.format("job not found with id=%s", id));
		}

	}
	
	/***
	 * Reports error during processing of job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 * @param message the error message
	 */
	@Override
	public void error(String id, String stepName, String message) {
		if ((id==null) || (stepName==null) || (message==null)) {
			logger.warn(String.format("error called with id=%s, stepName=%s, message=%s", id, stepName, message));
			return;
		}
		
		logStepMessage(id, stepName, Severity.ERROR, message);
	}
	
	/***
	 * Reports warning during processing of job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 * @param message the error message
	 */
	@Override
	public void warn(String id, String stepName, String message) {
		if ((id==null) || (stepName==null) || (message==null)) {
			logger.warn(String.format("warn called with id=%s, stepName=%s, message=%s", id, stepName, message));
			return;
		}
		
		logStepMessage(id, stepName, Severity.WARN, message);
	}
	
	private void logStepMessage(String id, String stepName, Severity severity, String message) {	
		// get the job report
		JobReport report = repository.findOne(id);
		if (report!=null) {
			JobStep step = report.findStep(stepName);
			if (step!=null) {
				JobMessage jobMessage = new JobMessage();
				jobMessage.setWhen(new Date());
				jobMessage.setSeverity(severity.toString());
				jobMessage.setMessage(message);
				
				step.addMessages(jobMessage);
				if (severity == Severity.ERROR)
					report.setHasErrors(true);
				if (severity == Severity.WARN)
					report.setHasWarnings(true);
				
				repository.save(report);
			} else {
				logger.warn(String.format("step %s not found in job %s", stepName, id));
			}
			
		} else {
			logger.warn(String.format("job not found with id=%s", id));
		}
	}

	private enum Severity { ERROR, WARN };
	
}
