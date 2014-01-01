package fortscale.monitor.mongo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.VerificationModeFactory;

import fortscale.monitor.domain.JobDataReceived;
import fortscale.monitor.domain.JobReport;
import fortscale.monitor.domain.JobStep;

@RunWith(JUnitParamsRunner.class)
public class MongoJobProgressReporterTest {

	private MongoJobProgressReporter subject;
	private JobReportRepository repository; 
	
	@Before
	public void initialize() {
		
		repository = mock(JobReportRepository.class);
		subject = new MongoJobProgressReporter(repository);
	}
	
	
	@Test
	public void getByID_shouldReturnNullWhenIDisNull() {
		JobReport report = subject.getByID(null);
		assertNull(report);
	}
	
	@Test
	public void getByID_shouldReturnNullWhenIDdoesNotExists() {
		JobReport report = subject.getByID("IgnoreMe");
		assertNull(report);
	}

	@Test
	public void reportJobStart_shouldCreateANewID() {
		
		// setup mock
		JobReport report = new JobReport();
		report.setId("aaaa");
		when(repository.save((JobReport)anyObject())).thenReturn(report);
		
		String id = subject.startJob("sourceA",  "jobA");
		assertNotNull(id);
		assertTrue(id.length()>0);
	}
	
	@Test
	public void reportJobStart_shouldCreateANewSavedJobReport() {
		
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setStart(new Date());
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		String id = subject.startJob("sourceA", "jobA");
		JobReport report = subject.getByID(id);
		
		assertNotNull(report);
		assertNotNull(report.getStart());
	}
	
	@Test
	public void reportJobStart_shouldPutCurrentTimeInTheSavedJobReport() {
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setStart(new Date());
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		
		String id = subject.startJob("sourceA", "jobA");
		JobReport report = subject.getByID(id);
		
		Date start = report.getStart();
		assertNotNull(start);
		
		Date now = new Date();
		long diff = now.getTime() -  start.getTime();
		
		assertTrue((diff / 1000L) < 3L); // diff should be up to 3 seconds from now 
	}

	
	@Test
	public void reportJobFinish_shouldIngoreNullId() {
		subject.finishJob(null);
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportJobFinish_shouldIngoreNonExistantId() {
		subject.finishJob("IgnoreMe");
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportJobFinish_shouldPutCurrentTimeInTheSavedJobReport() {
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setStart(new Date());
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		
		String id = subject.startJob("SourceA", "JobA");
		subject.finishJob(id);
		
		JobReport report = subject.getByID(id);
		
		Date finish = report.getFinish();
		assertNotNull(finish);
		
		Date now = new Date();
		long diff = now.getTime() -  finish.getTime();
		
		assertTrue((diff / 1000L) < 3L); // diff should be up to 3 seconds from now 
	}

	@Test
	public void reportStepStart_shouldIngoreNullJobId() {
		subject.startStep(null, "a", 0);
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportStepStart_shouldIngoreNonExistingJobId() {
		subject.startStep("IgnoreMe", "a", 0);
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportStepStart_shouldIngoreNullStepId() {
		
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		String id = subject.startJob("SourceA",  "JobA");
		subject.startStep(id, null, 0);
		
		// do nothing, ensure no exception is thrown
	}
	
	
	@Test
	public void reportStepStart_shouldPutStartTimeInStep() {
		
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setStart(new Date());
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
				
		String id = subject.startJob("SourceA",  "JobA");
		subject.startStep(id, "Step1", 0);
		
		JobReport report = subject.getByID(id);
		assertNotNull(report.getSteps());
		assertTrue(report.getSteps().size()==1);
		assertNotNull(report.getSteps().get(0).getStart());
	}

	@Test
	public void reportStepFinish_shouldIgnoreStepsThatDoNotExists() {
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setStart(new Date());
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		
		String id = subject.startJob("SourceA",  "JobA");
		subject.finishStep(id, "IgnoreMe");
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportStepFinish_shouldIgnoreNullJobId() {
		subject.finishStep(null, "A");
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportStepFinish_shouldIgnoreNullStepsName() {
		
		// setup mock
		JobReport report = new JobReport();
		report.setId("aaaa");
		when(repository.save((JobReport)anyObject())).thenReturn(report);
		
		String id = subject.startJob("SourceA",  "JobA");
		subject.finishStep(id, null);
		
		// do nothing, ensure no exception is thrown
	}
	
	@Test
	public void reportStepFinish_shouldIgnoreJobsThatDoNotExists() {
		subject.finishStep("IgnoreMe", "MeToo");
	}
	
	@Test
	public void reportStepFinish_shouldPutFinishTimeForStep() {
		// setup mock
		JobReport value = new JobReport();
		value.setId("aaaa");
		value.setFinish(new Date());
		value.getSteps().add(new JobStep("StepA"));
		when(repository.save((JobReport)anyObject())).thenReturn(value);
		when(repository.findOne("aaaa")).thenReturn(value);
		
		String id = subject.startJob("SourceA",  "JobA");
		subject.startStep(id, "StepA",  0);
		subject.finishStep(id, "StepA");
		
		JobReport report = subject.getByID(id);
		
		Date finish = report.getSteps().get(0).getFinish();
		Date now = new Date();
		long diff = now.getTime() - finish.getTime();
		
		assertTrue((diff/1000L) < 3L); // diff should be up to 3 seconds from now 
	}
	
	@Test
	public void error_should_put_new_message_for_step() {
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
			
		// act
		subject.error("aaaa", "StepA", "my error message");
		
		// assert
		ArgumentCaptor<JobReport> argument = ArgumentCaptor.forClass(JobReport.class);
		verify(repository).save(argument.capture());
		assertTrue(argument.getValue().findStep("StepA").getMessages().size() == 1);
		assertTrue(argument.getValue().findStep("StepA").getMessages().get(0).getMessage().equals("my error message"));
		assertTrue(argument.getValue().findStep("StepA").getMessages().get(0).getSeverity().equals("ERROR"));
	}

	@Test
	public void warn_should_put_new_message_for_step() {
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
			
		// act
		subject.warn("aaaa", "StepA", "my error message");
		
		// assert
		ArgumentCaptor<JobReport> argument = ArgumentCaptor.forClass(JobReport.class);
		verify(repository).save(argument.capture());
		assertTrue(argument.getValue().findStep("StepA").getMessages().size() == 1);
		assertTrue(argument.getValue().findStep("StepA").getMessages().get(0).getMessage().equals("my error message"));
		assertTrue(argument.getValue().findStep("StepA").getMessages().get(0).getSeverity().equals("WARN"));
	}
	
	@Test
	@Parameters({ "bbb, StepA",
				  "aaaa, StepB"
				})
	public void error_with_invalid_parameters_should_do_nothing(String id, String stepName) {
		
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.setId("aaaa");
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
		when(repository.findOne(anyString())).thenReturn(null);
		
		// act
		subject.error(id, stepName, "my error message");
		
		// assert
		verify(repository, times(0)).save(any(JobReport.class));
	}
	
	@Test
	@Parameters({ "bbb, StepA",
				  "aaaa, StepB"
				})
	public void warn_with_invalid_parameters_should_do_nothing(String id, String stepName) {
		
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.setId("aaaa");
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
		when(repository.findOne(anyString())).thenReturn(null);
		
		// act
		subject.warn(id, stepName, "my error message");
		
		// assert
		verify(repository, times(0)).save(any(JobReport.class));		
	}
	
	@Test
	public void error_should_mark_the_job_report_as_haserrors() {
		
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
			
		// act
		subject.error("aaaa", "StepA", "my error message");
		
		// assert
		ArgumentCaptor<JobReport> argument = ArgumentCaptor.forClass(JobReport.class);
		verify(repository).save(argument.capture());
		assertTrue(argument.getValue().isHasErrors());
		
	}
	
	@Test
	public void warn_should_mark_the_job_report_as_has_warnings() {
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.getSteps().add(new JobStep("StepA"));
		
		when(repository.findOne("aaaa")).thenReturn(previousReport);
			
		// act
		subject.warn("aaaa", "StepA", "my error message");
		
		// assert
		ArgumentCaptor<JobReport> argument = ArgumentCaptor.forClass(JobReport.class);
		verify(repository).save(argument.capture());
		assertTrue(argument.getValue().isHasWarnings());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void findJobReportsForLastDays_should_throw_exception_if_days_is_zero() {
		subject.findJobReportsForLastDays(0);
	}
	
	@Test
	public void addDataReceived_should_append_the_data_to_existing_list() {
		// arrange
		JobReport previousReport = new JobReport();
		previousReport.setStart(new Date());
		previousReport.getDataReceived().add(new JobDataReceived("Users", 323, "KB"));
		when(repository.findOne("sss")).thenReturn(previousReport);
		
		// act
		subject.addDataReceived("sss", new JobDataReceived("Groups", 23, "KB"));
		
		// assert
		ArgumentCaptor<JobReport> argument = ArgumentCaptor.forClass(JobReport.class);
		verify(repository).save(argument.capture());
		assertTrue(argument.getValue().getDataReceived().toArray().length == 2);
	}
	
	@Test
	public void addDataReceived_with_invalid_id_should_do_nothing() {
		// arrange
		when(repository.findOne("sss")).thenReturn(null);
		
		// act
		subject.addDataReceived("sss", new JobDataReceived("Groups", 23, "KB"));
		
		// assert
		verify(repository, VerificationModeFactory.times(0)).save(any(JobReport.class));
	}
}
