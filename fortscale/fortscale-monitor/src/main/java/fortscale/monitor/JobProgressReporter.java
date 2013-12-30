package fortscale.monitor;

import java.util.List;

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
	 * @return the job instance id
	 */
	public String startJob(String sourceType, String jobName);
	
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
	 * Gets the list of job reports in the last given days
	 * @param days the number of days to retrieve
	 * @return the list of job reports found
	 */
	public List<JobReport> findJobReportsForLastDays(int days);
}
