package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;


public abstract class EventScoringPigRunner {
	
	@Autowired
	private PigRunner pigRunner;
	
	
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

        return pigRunner.run(getPigScriptResource(), scriptParameters);
	}
	
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){}
}
