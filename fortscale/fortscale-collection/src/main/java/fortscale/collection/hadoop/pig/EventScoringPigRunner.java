package fortscale.collection.hadoop.pig;

import java.util.Date;
import java.util.Properties;

import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;

@Component
public class EventScoringPigRunner {
	
	@Autowired
	private PigRunner pigRunner;
	
	@Value("file:${collection.lib.dir}/beardedpig-1.0-SNAPSHOT.jar")
	private Resource jarFilePath1;
	@Value("file:${collection.lib.dir}/calibro-1.0-SNAPSHOT.jar")
	private Resource jarFilePath2;
	
	protected PartitionStrategy partitionStrategy = new MonthlyPartitionStrategy();
	
	public ExecJob run(Long runtime, Long deltaTime, Resource pigScriptResource, String inputData, String outputDataPrefix) throws Exception{
		Properties scriptParameters = new Properties();
        scriptParameters.put("jarFilePath1", jarFilePath1.getFile().getAbsolutePath());
        scriptParameters.put("jarFilePath2", jarFilePath2.getFile().getAbsolutePath());    
        scriptParameters.put("inputData", getInputDataParameter(inputData, deltaTime));
        scriptParameters.put("outputData", String.format("%sruntime=%s", outputDataPrefix,runtime));
        scriptParameters.put("deltaTime", deltaTime.toString());
        fillWithSpecificScriptParameters(scriptParameters);

        return pigRunner.run(pigScriptResource, scriptParameters);
	}
	
	protected String getInputDataParameter(String inputData, Long runtime) {
		
		long finish = (new Date()).getTime();
		String[] partitions = partitionStrategy.getPartitionsForDateRange(inputData, runtime, finish);
		return Joiner.on(",").skipNulls().join(partitions);		
	}
	
	
	
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){}
}
