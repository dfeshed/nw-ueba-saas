package fortscale.collection.hadoop.pig;

import java.util.List;
import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.pig.PigOperations;
import org.springframework.data.hadoop.pig.PigScript;


public abstract class EventScoringPigRunner {
	
	@Autowired
	private PigOperations  pigOperations;
	
	
	public abstract Resource getPigScriptResource();
	public abstract String getInputDataFullPath();
	public abstract String getOutputDataFullPathPrefix();
	
	public ExecJob run(Long runtime, Long deltaTime) throws Exception{
		Properties scriptParameters = new Properties();
        scriptParameters.put("jarFilePath1", "/home/cloudera/fortscale/fs-paprika/event-bulk-scorer/bearded-pig/target/beardedpig-1.0-SNAPSHOT.jar");
        scriptParameters.put("jarFilePath2", "/home/cloudera/fortscale/fs-paprika/calibro/target/calibro-1.0-SNAPSHOT.jar");    
        scriptParameters.put("inputData", getInputDataFullPath());
        scriptParameters.put("outputData", String.format("%sruntime=%s", getOutputDataFullPathPrefix(),runtime));
        scriptParameters.put("deltaTime", deltaTime.toString());
        fillWithSpecificScriptParameters(scriptParameters);
        PigScript pigScript = new PigScript(getPigScriptResource(), scriptParameters);
        List<ExecJob> execJobs = pigOperations.executeScript(pigScript);
        
        if(execJobs.isEmpty()){
        	throw new Exception("execJobs is empty.");
        }
        
        ExecJob execJob = execJobs.get(0);
        while(!execJob.hasCompleted()){
			Thread.sleep(10000);
		}
        
        return execJob;
	}
	
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){}
}
