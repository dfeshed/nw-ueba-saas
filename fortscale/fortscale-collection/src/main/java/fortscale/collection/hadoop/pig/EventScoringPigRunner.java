package fortscale.collection.hadoop.pig;

import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class EventScoringPigRunner {
	
	@Autowired
	private PigRunner pigRunner;
	
	@Value("${collection.lib.dir}/beardedpig-1.0-SNAPSHOT.jar")
	private Resource jarFilePath1;
	@Value("${collection.lib.dir}/calibro-1.0-SNAPSHOT.jar")
	private Resource jarFilePath2;
	
	public ExecJob run(Long runtime, Long deltaTime, Resource pigScriptResource, String inputData, String outputDataPrefix) throws Exception{
		Properties scriptParameters = new Properties();
        scriptParameters.put("jarFilePath1", jarFilePath1);
        scriptParameters.put("jarFilePath2", jarFilePath2);    
        scriptParameters.put("inputData", inputData);
        scriptParameters.put("outputData", String.format("%sruntime=%s", outputDataPrefix,runtime));
        scriptParameters.put("deltaTime", deltaTime.toString());
        fillWithSpecificScriptParameters(scriptParameters);

        return pigRunner.run(pigScriptResource, scriptParameters);
	}
	
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){}
}
