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
import fortscale.collection.hadoop.ImpalaClient;
import fortscale.collection.hadoop.pig.EventScoringPigRunner;
import fortscale.monitor.JobProgressReporter;
import fortscale.services.UserServiceFacade;























public class VpnScoringJobTest {
	
	@Mock 
	protected JobProgressReporter monitor;
	
	@Mock
	protected JobDataMapExtension jobDataMapExtension;
	
	@Mock
	private ImpalaClient impalaClient;
	
	@Mock
	private EventScoringPigRunner eventScoringPigRunner;
	
	@Mock
	private UserServiceFacade userServiceFacade;
		
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
		JobExecutionContextDummy jobExecutionContextDummy = new JobExecutionContextDummy();
		Long expectedRuntime = System.currentTimeMillis()/1000 - 5;
		Long deltaTimeInSec = 10L;
		when(jobDataMapExtension.getJobDataMapLongValue((JobDataMap)any(), eq(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER))).thenReturn(expectedRuntime);
		jobExecutionContextDummy.getMergedJobDataMap().put(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER, expectedRuntime);
		when(jobDataMapExtension.getJobDataMapLongValue((JobDataMap)any(), eq(EventScoringJob.DELTA_TIME_IN_SEC_JOB_PARAMETER))).thenReturn(deltaTimeInSec);
		jobExecutionContextDummy.getMergedJobDataMap().put(EventScoringJob.DELTA_TIME_IN_SEC_JOB_PARAMETER, deltaTimeInSec);
		ExecJob execJob = new ExecJobDummy();
		when(eventScoringPigRunner.run((Long)any(), (Long)any(), (Resource)any(), (String)any(), (String)any())).thenReturn(execJob);
		
		
		vpnScoringJob.execute(jobExecutionContextDummy);
		assertEquals(expectedRuntime, vpnScoringJob.getRuntime());
		assertEquals(expectedRuntime - deltaTimeInSec, vpnScoringJob.getEarliestEventTime().longValue());
	}
	
	@Test
	public void runtimeWithParamMillisTest() throws Exception{
		JobExecutionContextDummy jobExecutionContextDummy = new JobExecutionContextDummy();
		DateTime dateTime = new DateTime(System.currentTimeMillis() - 5000);
		long expectedRuntimeInMillis = dateTime.getMillis();
		long earliestEventTime = dateTime.minusSeconds(EventScoringJob.EVENTS_DELTA_TIME_IN_SEC_DEFAULT).getMillis();
		when(jobDataMapExtension.getJobDataMapLongValue((JobDataMap)any(), eq(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER))).thenReturn(expectedRuntimeInMillis);
		jobExecutionContextDummy.getMergedJobDataMap().put(EventScoringJob.LATEST_EVENT_TIME_JOB_PARAMETER, expectedRuntimeInMillis);
		ExecJob execJob = new ExecJobDummy();
		when(eventScoringPigRunner.run((Long)any(), (Long)any(), (Resource)any(), (String)any(), (String)any())).thenReturn(execJob);
		vpnScoringJob.execute(jobExecutionContextDummy);
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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<Tuple> getResults() throws ExecException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAlias() throws ExecException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Properties getConfiguration() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PigStats getStatistics() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public POStore getPOStore() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void completionNotification(Object cookie) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void kill() throws ExecException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getLogs(OutputStream log) throws ExecException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getSTDOut(OutputStream out) throws ExecException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getSTDError(OutputStream error) throws ExecException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Exception getException() {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Job> getJobClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public JobDataMap getJobDataMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isDurable() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isPersistJobDataAfterExecution() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isConcurrentExectionDisallowed() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean requestsRecovery() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public JobBuilder getJobBuilder() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	
	class JobExecutionContextDummy implements JobExecutionContext{
		
		private JobDataMap jobDataMap = new JobDataMap();
		private JobDetail jobDetail = new JobDetailDummy();
		

		@Override
		public Scheduler getScheduler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Trigger getTrigger() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Calendar getCalendar() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isRecovering() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRefireCount() {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getScheduledFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getPreviousFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getNextFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getFireInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getResult() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setResult(Object result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public long getJobRunTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void put(Object key, Object value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object get(Object key) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
