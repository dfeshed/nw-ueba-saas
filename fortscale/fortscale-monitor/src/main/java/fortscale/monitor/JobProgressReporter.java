package fortscale.monitor;

import java.util.Date;
import java.util.List;

import fortscale.monitor.domain.JobDataReceived;
import fortscale.monitor.domain.JobReport;

/**
 * Defines operations to report on jobs progress and steps progress
 */
public interface JobProgressReporter {

	
	/***
	 * Get job report details by id
	 * @param id
	 * @return
	 */
	public JobReport getByID(String id);
	
	/***
	 * Reports start time of the job for the source type 
	 * @param sourceType the type of data the job handles, i.e. VPN, SSH
	 * @param jobName the name of the job
	 * @param numSteps the total number steps expected in the job
	 * @return the job instance id
	 */
	public String startJob(String sourceType, String jobName, int numSteps);
	
	/**
	 * Reports the time the job has finished executing
	 * @param id the job instance id
	 */
	public void finishJob(String id);
	
	/**
	 * Reports the start time of the job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 * @param ordinal the ordinal position of the step 
	 */
	public void startStep(String id, String stepName, int ordinal);
	
	
	/**
	 * Reports the finish time of the job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 */	
	public void finishStep(String id, String stepName);
	
	
	/***
	 * Reports error during processing of job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 * @param message the error message
	 */
	public void error(String id, String stepName, String message);
	
	/***
	 * Reports warning during processing of job step
	 * @param id the job instance id
	 * @param stepName the name of the step inside the job
	 * @param message the error message
	 */
	public void warn(String id, String stepName, String message);
	
	/**
	 * Gets the list of job reports older than the time given (excluded), restrict the list 
	 * of items count to the amount given.
	 * @param when the starting time to look for reports
	 * @param count the maximum number of job report to return
	 * @return the list of job reports found
	 */
	public List<JobReport> findJobReportsOlderThan(Date when, int count);
	
	/**
	 * Get the list of job reports newer than the time given (excluded), restrict the list
	 * of items count to the amount given.
	 * @param when the starting time to look for reports
	 * @param count the maximum number of job reports to return
	 * @return the list of job reports found
	 */
	public List<JobReport> findJobReportsNewerThan(Date when, int count);
	
	/**
	 * Adds a data received metric to the job report
	 * @param id the job instance id
	 * @param data the data received details
	 */
	public void addDataReceived(String id, JobDataReceived data);
}
