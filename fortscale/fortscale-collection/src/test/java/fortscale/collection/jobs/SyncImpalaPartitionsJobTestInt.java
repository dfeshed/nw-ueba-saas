package fortscale.collection.jobs;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.collection.NotifyJobFinishListener;
import fortscale.utils.test.category.HadoopTestCategory;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context.xml"})
@Category(HadoopTestCategory.class)
public class SyncImpalaPartitionsJobTestInt {
	
	@Autowired
	private Scheduler jobScheduler;
	
	@Value("${hdfs.user.data.security.events.4769.path}")
	private String hdfsPath;
	
	private String tableName = "wmievents4769";
	
	private String partitionStrategy = "monthly";
	
	private int daysToRetain = 90;

	@Test
	public void runOnEmptyFolders() throws SchedulerException{
		JobKey jobKey = new JobKey("Add_Partitions", "SecurityEvents");
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("hdfsPath", hdfsPath);
		jobDataMap.put("tableName", tableName);
		jobDataMap.put("partitionStrategy", partitionStrategy);
		jobDataMap.put("daysToRetain", daysToRetain);
		
		NotifyJobFinishListener.FinishSignal monitor = NotifyJobFinishListener.waitOnJob(jobScheduler, jobKey);

		// pause all triggers and schedule only the required job
		// we need to start the scheduler before triggering and pausing all jobs as the
		// jobs configuration is loaded only when the scheduler is started
		jobScheduler.start();
		jobScheduler.pauseAll();
		jobScheduler.triggerJob(jobKey,jobDataMap);
		
		
		// wait for job completion
		monitor.doWait();		
	}
}
