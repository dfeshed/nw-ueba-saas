package fortscale.collection.hadoop;

import fortscale.collection.configuration.CollectionPropertiesResolver;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaClient;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HadoopInit implements InitializingBean{
	
	private static Logger logger = LoggerFactory.getLogger(HadoopInit.class);
	
	@Autowired
	private FileSystem hadoopFs;
	
	@Autowired
	protected ImpalaClient impalaClient;


	@Autowired
	CollectionPropertiesResolver env;
		
	//Users table
	@Value("${impala.user.fields}")
	private String impalaUserFields;
	@Value("${impala.user.table.delimiter}")
	private String impalaUserTableDelimiter;
	@Value("${impala.user.table.name}")
	private String impalaUserTableName;
	@Value("${hdfs.user.data.users.path}")
	private String impalaUsersDirectory;

	public void createImpalaTables() throws IOException{

		PartitionStrategy partitionStrategy;


		//Non Data source Schemas

		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);

		//Data Source Schemas



		//Iterate over the configuration and create the HDFS paths and schemas based on that
		try {
			String[] dataSourcesList = env.getEnvPropertyValue("${fortscale.data.source}").split(",");

			for (int i = 0; i < dataSourcesList.length; i++) {
				String dataSource = dataSourcesList[i];

				//Data schema
				Boolean haveData = env.getBooleanValue(String.format("${impala.%s.have.data}", dataSource));

				if (haveData) {
					String impalaDataTableFields = env.getEnvPropertyValue(String.format("${impala.data.%s.table.fields}", dataSource));
					String impalaDataTableDelimiter = env.getEnvPropertyValue(String.format("${impala.data.%s.table.delimiter}", dataSource));
					String impalaDataTableName = env.getEnvPropertyValue(String.format("${impala.data.%s.table.name}", dataSource));
					String impalaDataDirectory = env.getEnvPropertyValue(String.format("${hdfs.user.data.%s.path}", dataSource));
					String impalaDataTablePartitionType = env.getEnvPropertyValue(String.format("${impala.data.%s.table.partition.type}", dataSource));


					partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaDataTablePartitionType);
					createTable(impalaDataTableName, impalaDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaDataTableDelimiter, impalaDataDirectory);
				}



				//Enriched schema
				Boolean haveEnrich = env.getBooleanValue(String.format("${impala.%s.have.enrich}", dataSource));

				if (haveEnrich) {

					String impalaEnrichedDataTableFields = env.getEnvPropertyValue(String.format("${impala.enricheddata.%s.table.fields}", dataSource));
					String impalaEnrichedDataTableDelimiter = env.getEnvPropertyValue(String.format("${impala.enricheddata.%s.table.delimiter}", dataSource));
					String impalaEnrichedDataTableName = env.getEnvPropertyValue(String.format("${impala.enricheddata.%s.table.name}", dataSource));
					String impalaEnrichedDataDirectory = env.getEnvPropertyValue(String.format("${hdfs.user.enricheddata.%s.path}", dataSource));
					String impalaEnrichedDataTablePartitionType = env.getEnvPropertyValue(String.format("${impala.enricheddata.%s.table.partition.type}", dataSource));

					partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaEnrichedDataTablePartitionType);
					createTable(impalaEnrichedDataTableName, impalaEnrichedDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaEnrichedDataTableDelimiter, impalaEnrichedDataDirectory);
				}


				//Scored schema
				String impalaScoringTableFields = env.getEnvPropertyValue(String.format("${impala.score.%s.table.fields}", dataSource));
				String impalaScoringTableDelimiter = env.getEnvPropertyValue(String.format("${impala.score.%s.table.delimiter}", dataSource));
				String impalaScoringTableName = env.getEnvPropertyValue(String.format("${impala.score.%s.table.name}", dataSource));
				String impalaScoringDirectory = env.getEnvPropertyValue(String.format("${hdfs.user.processeddata.%s.path}", dataSource));
				String impalaScoringTablePartitionType = env.getEnvPropertyValue(String.format("${impala.score.%s.table.partition.type}", dataSource));


				partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaScoringTablePartitionType);
				createTable(impalaScoringTableName, impalaScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaScoringTableDelimiter, impalaScoringDirectory);



				//Top table schema
				Boolean haveTop = env.getBooleanValue(String.format("${impala.%s.have.topScore}", dataSource));

				if(haveTop) {
					String impalaTopScoringTableFields = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.fields}", dataSource));
					String impalaTopScoringTableDelimiter = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.delimiter}", dataSource));
					String impalaTopScoringTableName = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.name}", dataSource));
					String impalaTopScoringDirectory = env.getEnvPropertyValue(String.format("${hdfs.user.processeddata.%s.top.path}", dataSource));
					String impalaTopScoringTablePartitionType = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.partition.type}", dataSource));


					partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopScoringTablePartitionType);
					createTable(impalaTopScoringTableName, impalaTopScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopScoringTableDelimiter, impalaTopScoringDirectory);
				}
			}
		}
		catch (Exception e) {
			logger.error("Mandatory configuration was not exist there for the HadoopInit process was dismissed  , Cause -  {}", e.getMessage());

		}


	}
	
	private void createTable(String tableName, String fields, String partition, String delimiter, String location) throws IOException{
		if(!hadoopFs.exists(new Path(location))){
			hadoopFs.mkdirs(new Path(location));
		}
		try{
			impalaClient.createTable(tableName, fields, partition, delimiter, location, true);
		} catch(Exception e){
			// changed to log warning message instead of swallowing the exception as this might lose the details of real errors that might occur
			// this should be changed so that we won't get exception in case the table exists
			logger.error("error creating table " + tableName, e);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {	
		createImpalaTables();
	}
}
