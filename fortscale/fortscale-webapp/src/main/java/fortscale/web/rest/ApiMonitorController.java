package fortscale.web.rest;

import java.util.Date;
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
import fortscale.monitor.domain.JobDataReceived;
import fortscale.monitor.domain.JobReport;
import fortscale.monitor.domain.JobStep;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/monitor")
public class ApiMonitorController {

	@Autowired
	private JobProgressReporter monitor;
	
	public void setJobProgressReporter(JobProgressReporter monitor) {
		this.monitor = monitor;
	}
	

	/**
	 * returns job details of the 24 hours BEFORE the specified latest timestamp.
	 */
	@RequestMapping(value = "/summary/before/{ts}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SourceTypeSummary>> summaryBefore(@PathVariable("ts") long ts) {
		
		List<JobReport> reports = monitor.findJobReportsOlderThan(new Date(ts));
		return convertToSummaryDataBean(reports);
	}
	
	/**
	 * returns all the job details AFTER the specified earliest timestamp.
	 */
	@RequestMapping(value = "/summary/after/{ts}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SourceTypeSummary>> summaryAfter(@PathVariable("ts") long ts) {
		
		List<JobReport> reports = monitor.findJobReportsNewerThan(new Date(ts));
		return convertToSummaryDataBean(reports);
	}
	
	
	

	/***
	 * returns all the job details in the last 24 hours
	 */
	@RequestMapping(value = "/summary", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SourceTypeSummary>> summary() {
			
		//summary (not parameters): returns all the job details in the last 24 hours
		//summary + latest (timestamp): returns job details of the 24 hours BEFORE the specified latest timestamp.
		//summary + earliest (timestamp): returns all the job details AFTER the specified earliest timestamp.
		
		List<JobReport> reports = monitor.findLatestJobReports();
		return convertToSummaryDataBean(reports);
	}
		
	
	private DataBean<List<SourceTypeSummary>> convertToSummaryDataBean(List<JobReport> reports) {
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
			runDetail.setStart(report.getStart());
			runDetail.setFinish(report.getFinish());
			runDetail.setId(report.getId());
			
			if (report.isHasErrors()) {
				runDetail.setSeverity("ERROR");
			} else if (report.isHasWarnings()) {
				runDetail.setSeverity("WARN");
			} else if (report.getFinish()==null) {
				runDetail.setSeverity("NOT_FINISHED");
			} else if (report.isShouldReportDataReceived() && !hasData(report)) {
				runDetail.setSeverity("NO_DATA");
			} else {
				runDetail.setSeverity("OK");
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
	
	private boolean hasData(JobReport report) {
		for (JobDataReceived data : report.getDataReceived()) {
			if (data.getValue()>0)
				return true;
		}
		return false;
	}

	/**
	 * Get the job report for the given id
	 * @param id the job report identifier
	 * @return the job report details
	 */
	@RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<JobReport>> findReport(@PathVariable("id") String id) {
		Assert.hasText(id);
		
		JobReport report = monitor.getByID(id);
		
		DataBean<List<JobReport>> response = new DataBean<List<JobReport>>();
		List<JobReport> list = new LinkedList<JobReport>();
		list.add(report);
		response.setData(list);
		response.setTotal(1);
		return response;
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
		private Date start;
		private Date finish;
		private String severity = "NO_DATA";
		private boolean runAllSteps;
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
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
