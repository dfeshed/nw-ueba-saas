package fortscale.collection.hadoop.pig;

import java.util.List;
import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.pig.PigOperations;
import org.springframework.data.hadoop.pig.PigScript;
import org.springframework.stereotype.Component;

@Component
public class PigRunner {
	
	@Autowired
	private PigOperations  pigOperations;
		
	public ExecJob run(Resource pigScriptResource, Properties scriptParameters) throws Exception{
        PigScript pigScript = new PigScript(pigScriptResource, scriptParameters);
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
	
}
