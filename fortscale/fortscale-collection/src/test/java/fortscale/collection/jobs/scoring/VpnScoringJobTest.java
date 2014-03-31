package fortscale.collection.jobs.scoring;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.executionengine.ExecJob;
import org.apache.pig.backend.hadoop.executionengine.physicalLayer.relationalOperators.POStore;
import org.apache.pig.data.Tuple;
import org.apache.pig.tools.pigstats.PigStats;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.core.io.Resource;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.hadoop.pig.EventScoringPigRunner;
import fortscale.monitor.JobProgressReporter;























public class VpnScoringJobTest {
	
	@Mock 
	protected JobProgressReporter monitor;
	
	@Mock
	protected JobDataMapExtension jobDataMapExtension;
	
	@Mock
	private EventScoringPigRunner eventScoringPigRunner;
	
	@InjectMocks
	private VpnScoringJob	vpnScoringJob;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void runtimeNoParamTest() throws Exception{
		DateTime beforeExecute = new DateTime();
		ExecJob execJob = new ExecJobDummy();
		when(jobDataMapExtension.getJobDataMapLongValue(any(JobDataMap.class), any(String.class), eq((Long)null))).thenReturn(null);
		when(eventScoringPigRunner.run((Long)any(), (Long)any(), (Resource)any(), (String)any(), (String)any())).thenReturn(execJob);
		vpnScoringJob.execute(new JobExecutionContextDummy());
		DateTime afterExecute = new DateTime();
		assertTrue(String.format("beforeExecute: %d job runtime: %d", beforeExecute.getMillis(), vpnScoringJob.getRuntime()),
				beforeExecute.getMillis()/1000 <= vpnScoringJob.getRuntime());
		assertTrue(String.format("afterExecute: %d job runtime: %d", afterExecute.getMillis(), vpnScoringJob.getRuntime()),
				afterExecute.getMillis()/1000 >= vpnScoringJob.getRuntime());
	}
	
	@Test
	public void runtimeWithParamSecondTest() throws Exception{
		Long expectedRuntime = System.currentTimeMillis()/1000 - 5;
		Long deltaTimeInSec = 10L;
		when(jobDataMapExtension.getJobDataMapLongValue(any(JobDataMap.class), eq(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER), eq((Long)null))).thenReturn(expectedRuntime);
		Long defaultDeltaValue = new Long(EventScoringJob.EVENTS_DELTA_TIME_IN_SEC_DEFAULT);
		when(jobDataMapExtension.getJobDataMapLongValue(any(JobDataMap.class), eq(EventScoringJob.DELTA_TIME_IN_SEC_JOB_PARAMETER), eq(defaultDeltaValue))).thenReturn(deltaTimeInSec);
		ExecJob execJob = new ExecJobDummy();
		when(eventScoringPigRunner.run((Long)any(), (Long)any(), (Resource)any(), (String)any(), (String)any())).thenReturn(execJob);
		
		
		vpnScoringJob.execute(new JobExecutionContextDummy());
		assertEquals(expectedRuntime, vpnScoringJob.getRuntime());
		assertEquals(expectedRuntime - deltaTimeInSec, vpnScoringJob.getEarliestEventTime().longValue());
	}
	
	@Test
	public void runtimeWithParamMillisTest() throws Exception{
		DateTime dateTime = new DateTime(System.currentTimeMillis() - 5000);
		long expectedRuntimeInMillis = dateTime.getMillis();
		long earliestEventTime = dateTime.minusSeconds(EventScoringJob.EVENTS_DELTA_TIME_IN_SEC_DEFAULT).getMillis();
		when(jobDataMapExtension.getJobDataMapLongValue(any(JobDataMap.class), eq(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER), eq((Long)null))).thenReturn(expectedRuntimeInMillis);
		Long defaultDeltaValue = new Long(EventScoringJob.EVENTS_DELTA_TIME_IN_SEC_DEFAULT);
		when(jobDataMapExtension.getJobDataMapLongValue(any(JobDataMap.class), eq(EventScoringJob.DELTA_TIME_IN_SEC_JOB_PARAMETER), eq(defaultDeltaValue))).thenReturn(defaultDeltaValue);
		ExecJob execJob = new ExecJobDummy();
		when(eventScoringPigRunner.run((Long)any(), (Long)any(), (Resource)any(), (String)any(), (String)any())).thenReturn(execJob);
		vpnScoringJob.execute(new JobExecutionContextDummy());
		assertEquals(expectedRuntimeInMillis/1000, vpnScoringJob.getRuntime().longValue());
		assertEquals(earliestEventTime/1000, vpnScoringJob.getEarliestEventTime().longValue());
	}
	
	
	
	class ExecJobDummy implements ExecJob{
		
		JOB_STATUS jobStatus = JOB_STATUS.COMPLETED;


		public void setJobStatus(JOB_STATUS jobStatus) {
			this.jobStatus = jobStatus;
		}

		@Override
		public JOB_STATUS getStatus() {
			return jobStatus;
		}

		@Override
		public boolean hasCompleted() throws ExecException {
			return false;
		}

		@Override
		public Iterator<Tuple> getResults() throws ExecException {
			return null;
		}

		@Override
		public String getAlias() throws ExecException {
			return null;
		}

		@Override
		public Properties getConfiguration() {
			return null;
		}

		@Override
		public PigStats getStatistics() {
			return null;
		}

		@Override
		public POStore getPOStore() {
			return null;
		}

		@Override
		public void completionNotification(Object cookie) {
			
		}

		@Override
		public void kill() throws ExecException {
			
		}

		@Override
		public void getLogs(OutputStream log) throws ExecException {
			
		}

		@Override
		public void getSTDOut(OutputStream out) throws ExecException {
			
		}

		@Override
		public void getSTDError(OutputStream error) throws ExecException {
			
		}

		@Override
		public Exception getException() {
			return null;
		}
		
	}
	
	@SuppressWarnings("serial")
	class JobDetailDummy implements JobDetail{
		
		private JobKey jobKey = new JobKey("Scoring", "VPN");
		
		@Override
		public Object clone(){
			return null;
		}

		@Override
		public JobKey getKey() {
			return jobKey;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public Class<? extends Job> getJobClass() {
			return null;
		}

		@Override
		public JobDataMap getJobDataMap() {
			return null;
		}

		@Override
		public boolean isDurable() {
			return false;
		}

		@Override
		public boolean isPersistJobDataAfterExecution() {
			return false;
		}

		@Override
		public boolean isConcurrentExectionDisallowed() {
			return false;
		}

		@Override
		public boolean requestsRecovery() {
			return false;
		}

		@Override
		public JobBuilder getJobBuilder() {
			return null;
		}
		
	}

	
	class JobExecutionContextDummy implements JobExecutionContext{
		
		private JobDataMap jobDataMap = new JobDataMap();
		private JobDetail jobDetail = new JobDetailDummy();
		

		@Override
		public Scheduler getScheduler() {
			return null;
		}

		@Override
		public Trigger getTrigger() {
			return null;
		}

		@Override
		public Calendar getCalendar() {
			return null;
		}

		@Override
		public boolean isRecovering() {
			return false;
		}

		@Override
		public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
			return null;
		}

		@Override
		public int getRefireCount() {
			return 0;
		}

		@Override
		public JobDataMap getMergedJobDataMap() {
			return jobDataMap;
		}

		@Override
		public JobDetail getJobDetail() {
			return jobDetail;
		}

		@Override
		public Job getJobInstance() {
			return null;
		}

		@Override
		public Date getFireTime() {
			return null;
		}

		@Override
		public Date getScheduledFireTime() {
			return null;
		}

		@Override
		public Date getPreviousFireTime() {
			return null;
		}

		@Override
		public Date getNextFireTime() {
			return null;
		}

		@Override
		public String getFireInstanceId() {
			return null;
		}

		@Override
		public Object getResult() {
			return null;
		}

		@Override
		public void setResult(Object result) {
			
		}

		@Override
		public long getJobRunTime() {
			return 0;
		}

		@Override
		public void put(Object key, Object value) {
			
		}

		@Override
		public Object get(Object key) {
			return null;
		}
		
	}
}
