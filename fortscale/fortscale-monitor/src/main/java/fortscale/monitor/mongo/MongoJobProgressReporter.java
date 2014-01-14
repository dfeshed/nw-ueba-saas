package fortscale.monitor.mongo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.monitor.domain.JobMessage;
import fortscale.monitor.domain.JobReport;
import fortscale.monitor.domain.JobStep;

@Component
public class MongoJobProgressReporter implements JobProgressReporter {

	private static Logger logger = LoggerFactory.getLogger(MongoJobProgressReporter.class);
	
	private enum Severity { ERROR, WARN };
	
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
	public String startJob(String sourceType, String jobName, int numSteps) {
		
		if ((sourceType ==null) || (jobName == null)) {
			logger.warn(String.format("startJob called with sourceType=%s, jobName=%s", sourceType, jobName));
			return null;
		}
		
		JobReport report = new JobReport();
		report.setJobName(jobName);
		report.setSourceType(sourceType);
		report.setStart(new Date());
		report.setTotalExceptedSteps(numSteps);
		
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

	
	/**
	 * Gets the list of job reports older than the time given (excluded), restrict the list 
	 * of items count to the amount given.
	 * @param when the starting time to look for reports
	 * @param count the maximum number of job report to return
	 * @return the list of job reports found
	 */
	public List<JobReport> findJobReportsOlderThan(Date when, int count) {
		Assert.isTrue(count > 0);
		
		PageRequest page = new PageRequest(0, count, new Sort(Sort.Direction.DESC, "sourceType", "jobName", "start"));
		Page<JobReport> resultPage = repository.findByStartLessThan(when, page);
		
		List<JobReport> reports = new LinkedList<JobReport>();
		for (JobReport report : resultPage) {
			reports.add(report);
		}
		
		return reports;
	}
	
	/**
	 * Get the list of job reports newer than the time given (excluded), restrict the list
	 * of items count to the amount given.
	 * @param when the starting time to look for reports
	 * @param count the maximum number of job reports to return
	 * @return the list of job reports found
	 */
	public List<JobReport> findJobReportsNewerThan(Date when, int count) {
		Assert.isTrue(count > 0);
		
		PageRequest page = new PageRequest(0, count, new Sort(Sort.Direction.ASC, "sourceType", "jobName", "start"));
		Page<JobReport> resultPage = repository.findByStartGreaterThan(when, page);
		
		List<JobReport> reports = new LinkedList<JobReport>();
		for (JobReport report : resultPage) {
			reports.add(report);
		}
		
		return reports;
	}
	
	/**
	 * Adds a data received metric to the job report
	 * @param id the job instance id
	 * @param data the data received details
	 */
	public void addDataReceived(String id, JobDataReceived data) {
		if (id==null || data==null) {
			logger.warn("addDataReceived was called with id={}, data={}", id, data);
			return;
		}
			
		JobReport report = repository.findOne(id);
		if (report==null) {
			logger.warn("report not found with id={}", id);
		} else {
			report.getDataReceived().add(data);
			repository.save(report);
		}
	}
	
	
}
