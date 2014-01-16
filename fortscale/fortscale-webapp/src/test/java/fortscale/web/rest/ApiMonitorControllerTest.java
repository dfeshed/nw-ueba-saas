package fortscale.web.rest;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import fortscale.monitor.JobProgressReporter;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.monitor.domain.JobReport;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.ApiMonitorController.SourceTypeSummary;

public class ApiMonitorControllerTest {
	
	private ApiMonitorController subject;
	private JobProgressReporter monitor;
	
	@Before
	public void setup() {
		subject = new ApiMonitorController();
		monitor = mock(JobProgressReporter.class);
		subject.setJobProgressReporter(monitor); 
	}
	
	@Test
	public void summary_should_return_severity_no_data_when_data_is_reported_with_zero_sum() {
		// arrange
		List<JobReport> reports = new ArrayList<JobReport>();
		JobReport report = new JobReport();
		report.setStart(new Date());
		report.setSourceType("MySource");
		report.setJobName("MyJob");
		report.setFinish(new Date());
		report.getDataReceived().add(new JobDataReceived("MyType", 0, "Events"));
		reports.add(report);
		when(monitor.findJobReportsOlderThan(any(Date.class), anyInt())).thenReturn(reports);
		
		// act
		DataBean<List<SourceTypeSummary>> result = subject.summary(1, 0, 0);
		
		// assert
		assertNotNull(result);
		SourceTypeSummary actual = result.getData().get(0);
		assertTrue(actual.getJobs().get(0).getRunDetails().get(0).getSeverity().equals("NO_DATA"));
	}
	
	@Test
	public void summary_should_return_ok_severity_when_data_reported_in_atleast_one_report() {
		// arrange
		List<JobReport> reports = new ArrayList<JobReport>();
		JobReport report = new JobReport();
		report.setStart(new Date());
		report.setSourceType("MySource");
		report.setJobName("MyJob");
		report.setFinish(new Date());
		report.getDataReceived().add(new JobDataReceived("MyTypeA", 0, "Events"));
		report.getDataReceived().add(new JobDataReceived("MyTypeB", 1, "Events"));
		reports.add(report);
		when(monitor.findJobReportsOlderThan(any(Date.class), anyInt())).thenReturn(reports);
		
		// act
		DataBean<List<SourceTypeSummary>> result = subject.summary(1, 0, 0);
		
		// assert
		assertNotNull(result);
		SourceTypeSummary actual = result.getData().get(0);
		assertTrue(actual.getJobs().get(0).getRunDetails().get(0).getSeverity().equals("OK"));
	}

}
