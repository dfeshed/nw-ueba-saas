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

	//@Autowired
	//Environment env;

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

	//Total Score table
	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
	@Value("${impala.total.scores.table.name}")
	private String impalaTotalScoringTableName;
	@Value("${hdfs.user.processeddata.totalscore.path}")
	private String impalaTotalScoringDirectory;
	@Value("${impala.total.scores.table.partition.type}")
	private String impalaTotalScoringTablePartitionType;







	
	//Security Events Data table
	@Value("${impala.data.security.events.4769.table.fields}")
	private String impalaSecDataTableFields;
	@Value("${impala.data.security.events.4769.table.delimiter}")
	private String impalaSecDataTableDelimiter;
	@Value("${impala.data.security.events.4769.table.name}")
	private String impalaSecDataTableName;
	@Value("${hdfs.user.data.security.events.4769.path}")
	private String impalaSecDataDirectory;
    @Value("${impala.data.security.events.4769.table.partition.type}")
    private String  impalaSecDataTablePartitionType;
	
	//Security Events Scoring table
	@Value("${impala.score.ldapauth.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.score.ldapauth.table.delimiter}")
	private String impalaSecScoringTableDelimiter;
	@Value("${impala.score.ldapauth.table.name}")
	private String impalaSecScoringTableName;
	@Value("${hdfs.user.processeddata.security.events.4769.path}")
	private String impalaSecScoringDirectory;
	@Value("${impala.score.ldapauth.table.partition.type}")
	private String impalaSecScoringTablePartitionType;
	
	// Top Security Events Scoring table
	@Value("${impala.score.ldapauth_top.table.fields}")
	private String impalaSecTopScoringTableFields;
	@Value("${impala.score.ldapauth_top.table.delimiter}")
	private String impalaSecTopScoringTableDelimiter;
	@Value("${impala.score.ldapauth_top.table.name}")
	private String impalaSecTopScoringTableName;
	@Value("${hdfs.user.processeddata.security.top.events.4769.path}")
	private String impalaSecTopScoringDirectory;
	@Value("${impala.score.ldapauth_top.table.partition.type}")
	private String impalaSecTopScoringTablePartitionType;
	
	// Security Events Login Scoring table
	@Value("${impala.score.login.table.name}")
	private String impalaLoginScoringTableName;
	@Value("${impala.score.login.table.delimiter}")
	private String impalaLoginScoringTableDelimiter;
	@Value("${impala.score.login.table.fields}")
	private String impalaLoginScoringTableFields;
	@Value("${hdfs.user.processeddata.security.login.path}")
	private String impalaLoginScoringDirectory;
    @Value("${impala.score.login.table.partition.type}")
    private String impalaSecLoginScoringTablePartitionType;

	//VPN Session Scoring table
	@Value("${impala.score.vpn.session.table.fields}")
	private String impalaVpnSessionScoringTableFields;
	@Value("${impala.score.vpn.session.table.delimiter}")
	private String impalaVpnSessionScoringTableDelimiter;
	@Value("${impala.score.vpn.session.table.name}")
	private String impalaVpnSessionScoringTableName;
	@Value("${hdfs.user.processeddata.vpnscores.session.path}")
	private String impalaVpnSessionScoringDirectory;
    @Value("${impala.score.vpn.session.table.partition.type}")
    private String impalaVpnSessionScoringTablePartitionType;
	
	//Top VPN Session Scoring table
	@Value("${impala.score.vpn.session.top.table.fields}")
	private String impalaTopVpnSessionScoringTableFields;
	@Value("${impala.score.vpn.session.top.table.delimiter}")
	private String impalaTopVpnSessionScoringTableDelimiter;
	@Value("${impala.score.vpn.session.top.table.name}")
	private String impalaTopVpnSessionScoringTableName;
	@Value("${hdfs.user.processeddata.vpnscores.session.top.path}")
	private String impalaTopVpnSessionScoringDirectory;
    @Value("${impala.score.vpn.session.top.table.partition.type}")
    private String impalaTopVpnSessionScoringTablePartitionType;
	


	
	
	//AD Computers table
	@Value("${impala.ldapcomputers.table.fields}")
	private String impalaAdComputerTableFields;
	@Value("${impala.ldapcomputers.table.delimiter}")
	private String impalaAdComputerTableDelimiter;
	@Value("${impala.ldapcomputers.table.name}")
	private String impalaAdComputerTableName;
	@Value("${hdfs.user.data.ldap.computers.path}")
	private String impalaAdComputerDirectory;
    @Value("${impala.ldapcomputers.table.partition.type}")
    private String impalaADComputerDataTablePartitionType;
	
	//AD OUs table
	@Value("${impala.ldapous.table.fields}")
	private String impalaAdOUTableFields;
	@Value("${impala.ldapous.table.delimiter}")
	private String impalaAdOUTableDelimiter;
	@Value("${impala.ldapous.table.name}")
	private String impalaAdOUTableName;
	@Value("${hdfs.user.data.ldap.ous.path}")
	private String impalaAdOUDirectory;
    @Value("${impala.ldapous.table.partition.type}")
    private String impalaADOUsDataTablePartitionType;
	
	//AD Group table
	@Value("${impala.ldapgroups.table.fields}")
	private String impalaAdGroupTableFields;
	@Value("${impala.ldapgroups.table.delimiter}")
	private String impalaAdGroupTableDelimiter;
	@Value("${impala.ldapgroups.table.name}")
	private String impalaAdGroupTableName;
	@Value("${hdfs.user.data.ldap.groups.path}")
	private String impalaAdGroupDirectory;
    @Value("${impala.ldapgroups.table.partition.type}")
    private String impalaADGroupsDataTablePartitionType;
	
	//AD User table
	@Value("${impala.ldapusers.table.fields}")
	private String impalaAdUserTableFields;
	@Value("${impala.ldapusers.table.delimiter}")
	private String impalaAdUserTableDelimiter;
	@Value("${impala.ldapusers.table.name}")
	private String impalaAdUserTableName;
	@Value("${hdfs.user.data.ldap.users.path}")
	private String impalaAdUserDirectory;
    @Value("${impala.ldapusers.table.partition.type}")
    private String impalaADUsersDataTablePartitionType;
	
	// Security Events (4769) Enriched Data table
	@Value("${impala.enricheddata.security.events.table.fields}")
	private String impalaSecEnrichedDataTableFields;
	@Value("${impala.enricheddata.security.events.table.delimiter}")
	private String impalaSecEnrichedDataTableDelimiter;
	@Value("${impala.enricheddata.security.events.table.name}")
	private String impalaSecEnrichedDataTableName;
	@Value("${hdfs.user.enricheddata.security.events.path}")
	private String impalaSecEnrichedDataDirectory;
	@Value("${impala.enricheddata.security.events.table.partition.type}")
	private String impalaSecEnrichedDataTablePartitionType;





	// AMT Enriched Data table
	@Value("${impala.enricheddata.amt.table.fields}")
	private String impalaAmtEnrichedDataTableFields;
	@Value("${impala.enricheddata.amt.table.delimiter}")
	private String impalaAmtEnrichedDataTableDelimiter;
	@Value("${impala.enricheddata.amt.table.name}")
	private String impalaAmtEnrichedDataTableName;
	@Value("${hdfs.user.enricheddata.amt.path}")
	private String impalaAmtEnrichedDataDirectory;
	@Value("${impala.enricheddata.amt.table.partition.type}")
	private String impalaAmtEnrichedDataTablePartitionType;

	//AMT Data table
	@Value("${impala.data.amt.table.fields}")
	private String impalaAmtDataTableFields;
	@Value("${impala.data.amt.table.delimiter}")
	private String impalaAmtDataTableDelimiter;
	@Value("${impala.data.amt.table.name}")
	private String impalaAmtDataTableName;
	@Value("${hdfs.user.data.amt.path}")
	private String impalaAmtDataDirectory;
	@Value("${impala.data.amt.table.partition.type}")
	private String impalaAmtDataTablePartitionType;

	// AMT Scoring table
	@Value("${impala.score.amt.table.fields}")
	private String impalaAmtScoringTableFields;
	@Value("${impala.score.amt.table.delimiter}")
	private String impalaAmtScoringTableDelimiter;
	@Value("${impala.score.amt.table.name}")
	private String impalaAmtScoringTableName;
	@Value("${hdfs.user.processeddata.amtscores.path}")
	private String impalaAmtScoringDirectory;
	@Value("${impala.score.amt.table.partition.type}")
	private String impalaAmtScoringTablePartitionType;

	// Top AMT Scoring table
	@Value("${impala.score.amt_top.table.fields}")
	private String impalaTopAmtScoringTableFields;
	@Value("${impala.score.amt_top.table.delimiter}")
	private String impalaTopAmtScoringTableDelimiter;
	@Value("${impala.score.amt_top.table.name}")
	private String impalaTopAmtScoringTableName;
	@Value("${hdfs.user.processeddata.amtscores.top.path}")
	private String impalaTopAmtScoringDirectory;
	@Value("${impala.score.amt_top.table.partition.type}")
	private String impalaTopAmtScoringTablePartitionType;

	// AMT Session Data table
	@Value("${impala.sessiondata.amt.table.fields}")
	private String impalaAmtSessionDataTableFields;
	@Value("${impala.sessiondata.amt.table.delimiter}")
	private String impalaAmtSessionDataTableDelimiter;
	@Value("${impala.sessiondata.amt.table.name}")
	private String impalaAmtSessionDataTableName;
	@Value("${hdfs.user.processeddata.amtsession.path}")
	private String impalaAmtSessionDataDirectory;
	@Value("${impala.sessiondata.amt.table.partition.type}")
	private String impalaAmtSessionDataTablePartitionType;

	public void createImpalaTables() throws IOException{

		PartitionStrategy partitionStrategy;


		//Non Data source Schemas

		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		

		//Total Scoring table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTotalScoringTablePartitionType);
		createTable(impalaTotalScoringTableName, impalaTotalScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTotalScoringTableDelimiter, impalaTotalScoringDirectory);
		
		//AD Computer table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaADComputerDataTablePartitionType);
        createTable(impalaAdComputerTableName, impalaAdComputerTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAdComputerTableDelimiter, impalaAdComputerDirectory);

		//AD OU table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaADOUsDataTablePartitionType);
		createTable(impalaAdOUTableName, impalaAdOUTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAdOUTableDelimiter, impalaAdOUDirectory);
				
		//AD Group table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaADGroupsDataTablePartitionType);
		createTable(impalaAdGroupTableName, impalaAdGroupTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAdGroupTableDelimiter, impalaAdGroupDirectory);
		
		//AD User table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaADUsersDataTablePartitionType);
		createTable(impalaAdUserTableName, impalaAdUserTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAdUserTableDelimiter, impalaAdUserDirectory);



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
				String impalaTopScoringTableFields = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.fields}", dataSource));
				String impalaTopScoringTableDelimiter = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.delimiter}", dataSource));
				String impalaTopScoringTableName = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.name}", dataSource));
				String impalaTopScoringDirectory = env.getEnvPropertyValue(String.format("${hdfs.user.processeddata.%s.top.path}", dataSource));
				String impalaTopScoringTablePartitionType = env.getEnvPropertyValue(String.format("${impala.score.%s.top.table.partition.type}", dataSource));


				partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopScoringTablePartitionType);
				createTable(impalaTopScoringTableName, impalaTopScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopScoringTableDelimiter, impalaTopScoringDirectory);
			}
		}
		catch (Exception e)
		{
			logger.error("Mandatory configuration was not exist there for the HadoopInit process was dismissed  , Cause -  {}",e.getMessage());

		}










		//Security Events Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecDataTablePartitionType);
		createTable(impalaSecDataTableName, impalaSecDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecDataTableDelimiter, impalaSecDataDirectory);

	


		//Security Events Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecScoringTablePartitionType);
		createTable(impalaSecScoringTableName, impalaSecScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecScoringTableDelimiter, impalaSecScoringDirectory);

		//Top Security Events Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecTopScoringTablePartitionType);
		createTable(impalaSecTopScoringTableName, impalaSecTopScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecTopScoringTableDelimiter, impalaSecTopScoringDirectory);

		// Security Events Login Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecLoginScoringTablePartitionType);
		createTable(impalaLoginScoringTableName, impalaLoginScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaLoginScoringTableDelimiter, impalaLoginScoringDirectory);



		//VPN Session Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaVpnSessionScoringTablePartitionType);
		createTable(impalaVpnSessionScoringTableName, impalaVpnSessionScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaVpnSessionScoringTableDelimiter, impalaVpnSessionScoringDirectory);

		//Top VPN Session Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopVpnSessionScoringTablePartitionType);
		createTable(impalaTopVpnSessionScoringTableName, impalaTopVpnSessionScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopVpnSessionScoringTableDelimiter, impalaTopVpnSessionScoringDirectory);



		//AMT Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaAmtScoringTablePartitionType);
		createTable(impalaAmtScoringTableName, impalaAmtScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAmtScoringTableDelimiter, impalaAmtScoringDirectory);

		//AMT Top Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopAmtScoringTablePartitionType);
		createTable(impalaTopAmtScoringTableName, impalaTopAmtScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopAmtScoringTableDelimiter, impalaTopAmtScoringDirectory);





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
