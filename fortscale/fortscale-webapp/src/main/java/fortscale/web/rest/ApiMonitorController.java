package fortscale.web.rest;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobReport;
import fortscale.monitor.domain.JobStep;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/monitor")
public class ApiMonitorController {

	@Autowired
	private JobProgressReporter monitor;
	
	/***
	 * Get an aggregated list of jobs monitor status according to source type and job name 
	 * @return
	 */
	@RequestMapping(value = "/summary/{days}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SourceTypeSummary>> summary(@PathVariable("days") int days) {
		
		// get the list of job reports for the last days,
		// assume it is sorted by source type, job name and start date
		List<JobReport> reports = monitor.findJobReportsForLastDays(days);
		
		// convert job report results to service response format
		List<SourceTypeSummary> summary = new LinkedList<SourceTypeSummary>();
		int total = 0;
		for (JobReport report : reports) {
			
			// find the source type summary for the report
			SourceTypeSummary sourceSummary = null;
			for (SourceTypeSummary listSummary : summary) {
				if (listSummary.getSourceType().equals(report.getSourceType())) {
					sourceSummary = listSummary;
					break;
				}
			}
			if (sourceSummary==null) {
				sourceSummary = new SourceTypeSummary();
				sourceSummary.setSourceType(report.getSourceType());
				summary.add(sourceSummary);
				total++;
			}
			
			// find the job name summary for the report
			JobSummary jobSummary = null;
			for (JobSummary item : sourceSummary.getJobs()) {
				if (item.getJobName().equals(report.getJobName())) {
					jobSummary = item;
					break;
				}
			}
			if (jobSummary==null) {
				jobSummary = new JobSummary();
				jobSummary.setJobName(report.getJobName());
				sourceSummary.getJobs().add(jobSummary);
			}
			
			// add the run detail for the job summary
			RunDetail runDetail = new RunDetail();
			runDetail.setStart(report.getStart().getTime());
			if (report.getFinish()!=null)
				runDetail.setFinish(report.getFinish().getTime());
			runDetail.setId(report.getId());
			
			if (report.isHasErrors()) {
				runDetail.setSeverity("ERROR");
			} else if (report.isHasWarnings()) {
				runDetail.setSeverity("WARN");
			} else if (report.getFinish()!=null) {
				runDetail.setSeverity("OK");
			} else {
				runDetail.setSeverity("NO_DATA");
			}
			
			// set a flag indicating if all steps were executed
			runDetail.setRunAllSteps(false);
			for (JobStep step : report.getSteps()) {
				if (step.getOrdinal() == report.getTotalExceptedSteps() && step.getFinish()!=null)
					runDetail.setRunAllSteps(true);
			}			
			
			jobSummary.getRunDetails().add(runDetail);
		}
		
		DataBean<List<SourceTypeSummary>> ret = new DataBean<List<SourceTypeSummary>>();
		ret.setData(summary);
		ret.setTotal(total);
		ret.setOffset(0);
		
		return ret;
	}
	
	/***
	 * Get an aggregated list of jobs monitor status according to source type and job name 
	 * @return
	 */
	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SourceTypeSummary>> summary() {
		return summary(7);
	}
	
	/**
	 * Get the job report for the given id
	 * @param id the job report identifier
	 * @return the job report details
	 */
	@RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public JobReport findReport(@PathVariable("id") String id) {
		Assert.hasText(id);
		
		JobReport report = monitor.getByID(id);
		return report;		
	}
	
	public class SourceTypeSummary {
		
		private String sourceType;
		private List<JobSummary> jobs;
		
		public String getSourceType() {
			return sourceType;
		}

		public void setSourceType(String sourceType) {
			this.sourceType = sourceType;
		}
		
		public List<JobSummary> getJobs() {
			if (jobs==null) {
				jobs = new LinkedList<JobSummary>();
			}
			return jobs;
		}
		
		public void setJobs(List<JobSummary> jobs) {
			this.jobs = jobs;
		}
	}

	public class JobSummary {
		private String jobName;
		private List<RunDetail> runDetails;
		
		public String getJobName() {
			return jobName;
		}
		
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		
		public List<RunDetail> getRunDetails() {
			if (runDetails==null) {
				runDetails = new LinkedList<RunDetail>();
			}
			return runDetails;
		}
		
		public void setRunDetails(List<RunDetail> runDetails) {
			this.runDetails = runDetails;
		}
	}
	
	public class RunDetail {
		
		private String id;
		private long start;
		private long finish;
		private String severity = "NO_DATA";
		private boolean runAllSteps;
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;
		}

		public long getFinish() {
			return finish;
		}

		public void setFinish(long finish) {
			this.finish = finish;
		}

		public String getSeverity() {
			return severity;
		}

		public void setSeverity(String severity) {
			this.severity = severity;
		}
		
		public boolean isRunAllSteps() {
			return runAllSteps;
		}

		public void setRunAllSteps(boolean runAllSteps) {
			this.runAllSteps = runAllSteps;
		}		
	}
}
