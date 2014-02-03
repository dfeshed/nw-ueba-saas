package fortscale.collection.hadoop.pig;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.pig.backend.executionengine.ExecJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.logging.Logger;

@Component
public class EventScoringPigRunner {
	private static Logger logger = Logger.getLogger(EventScoringPigRunner.class);
	
	@Autowired
	private PigRunner pigRunner;
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Value("file:${collection.lib.dir}/beardedpig-1.0-SNAPSHOT.jar")
	private Resource jarFilePath1;
	@Value("file:${collection.lib.dir}/calibro-1.0-SNAPSHOT.jar")
	private Resource jarFilePath2;
	
	protected PartitionStrategy partitionStrategy = new MonthlyPartitionStrategy();
	
	public ExecJob run(Long runtime, Long earliestEventTime, Resource pigScriptResource, String inputData, String outputDataPrefix) throws IOException, NoPartitionExistException, NoPigJobExecutedException, InterruptedException{
		Properties scriptParameters = new Properties();
        scriptParameters.put("jarFilePath1", jarFilePath1.getFile().getAbsolutePath());
        scriptParameters.put("jarFilePath2", jarFilePath2.getFile().getAbsolutePath());    
        scriptParameters.put("inputData", getInputDataParameter(inputData, earliestEventTime, runtime));
        scriptParameters.put("outputData", String.format("%sruntime=%s", outputDataPrefix,runtime));
        scriptParameters.put("deltaTime", earliestEventTime.toString());
        fillWithSpecificScriptParameters(scriptParameters);

        return pigRunner.run(pigScriptResource, scriptParameters);
	}
	
	protected String getInputDataParameter(String inputData, long earliestEventTime, long latestEventTime) throws IOException, NoPartitionExistException {
		String[] partitions = partitionStrategy.getPartitionsForDateRange(inputData, earliestEventTime, latestEventTime);
		StringBuilder builder = new StringBuilder();
		for(String partitionPath: partitions){
			if(hadoopFs.exists(new Path(partitionPath))) {
				builder.append(partitionPath).append(",");
			} else{
				logger.info("the partition {} does not exist.", partitionPath);
			}
		}
		if(builder.length() == 0){
			logger.info("all the partitions {} do not exist.", StringUtils.join(partitions, ","));
			throw new NoPartitionExistException(String.format("the partitions %s do not exist.", StringUtils.join(partitions, ",")));
		}
		return builder.substring(0, builder.length()-1);		
	}
	
	
	
	protected void fillWithSpecificScriptParameters(Properties scriptParameters){}
}
