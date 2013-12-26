package fortscale.monitor.mongo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import fortscale.monitor.JobReport;
import fortscale.monitor.JobStep;


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

}
