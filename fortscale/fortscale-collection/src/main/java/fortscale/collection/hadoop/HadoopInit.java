package fortscale.collection.hadoop;

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
		
	//Users table
	@Value("${impala.user.fields}")
	private String impalaUserFields;
	@Value("${impala.user.table.delimiter}")
	private String impalaUserTableDelimiter;
	@Value("${impala.user.table.name}")
	private String impalaUserTableName;
	@Value("${hdfs.user.data.users.path}")
	private String impalaUsersDirectory;

	// Security Events Login Raw Data table
	@Value("${impala.rawdata.security.events.login.table.fields}")
	private String impalaSecLoginRawDataTableFields;
	@Value("${impala.rawdata.security.events.login.table.delimiter}")
	private String impalaSecLoginRawDataTableDelimiter;
	@Value("${impala.rawdata.security.events.login.table.name}")
	private String impalaSecLoginRawDataTableName;
	@Value("${hdfs.user.rawdata.security.events.login.path}")
	private String impalaSecLoginRawDataDirectory;
	@Value("${impala.rawdata.security.events.login.table.partition.type}")
	private String  impalaSecLoginRawDataTablePartitionType;

	// Security Events Login Data table
	@Value("${impala.data.security.events.login.table.fields}")
	private String impalaSecLoginTableFields;
	@Value("${impala.data.security.events.login.table.delimiter}")
	private String impalaSecLoginTableDelimiter;
	@Value("${impala.data.security.events.login.table.name}")
	private String impalaSecLoginTableName;
	@Value("${hdfs.user.data.security.events.login.path}")
	private String impalaSecLoginDirectory;
    @Value("${impala.data.security.events.login.table.partition.type}")
    private String  impalaSecLoginTablePartitionType;
	
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
	
	//VPN Data table
	@Value("${impala.data.vpn.table.fields}")
	private String impalaVpnDataTableFields;
	@Value("${impala.data.vpn.table.delimiter}")
	private String impalaVpnDataTableDelimiter;
	@Value("${impala.data.vpn.table.name}")
	private String impalaVpnDataTableName;
	@Value("${hdfs.user.data.vpn.path}")
	private String impalaVpnDataDirectory;
	@Value("${impala.data.vpn.table.partition.type}")
	private String impalaVpnDataTablePartitionType;
		
	//VPN Scoring table
	@Value("${impala.score.vpn.table.fields}")
	private String impalaVpnScoringTableFields;
	@Value("${impala.score.vpn.table.delimiter}")
	private String impalaVpnScoringTableDelimiter;
	@Value("${impala.score.vpn.table.name}")
	private String impalaVpnScoringTableName;
	@Value("${hdfs.user.processeddata.vpnscores.path}")
	private String impalaVpnScoringDirectory;
	@Value("${impala.score.vpn.table.partition.type}")
	private String impalaVpnScoringTablePartitionType;
	
	//Top VPN Scoring table
	@Value("${impala.score.vpn_top.table.fields}")
	private String impalaTopVpnScoringTableFields;
	@Value("${impala.score.vpn_top.table.delimiter}")
	private String impalaTopVpnScoringTableDelimiter;
	@Value("${impala.score.vpn_top.table.name}")
	private String impalaTopVpnScoringTableName;
	@Value("${hdfs.user.processeddata.vpnscores_top.path}")
	private String impalaTopVpnScoringDirectory;
	@Value("${impala.score.vpn_top.table.partition.type}")
	private String impalaTopVpnScoringTablePartitionType;
	
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
	
	//SSH Data table
	@Value("${impala.data.ssh.table.fields}")
	private String impalaSshDataTableFields;
	@Value("${impala.data.ssh.table.delimiter}")
	private String impalaSshDataTableDelimiter;
	@Value("${impala.data.ssh.table.name}")
	private String impalaSshDataTableName;
	@Value("${hdfs.user.data.ssh.path}")
	private String impalaSshDataDirectory;
    @Value("${impala.data.ssh.table.partition.type}")
    private String impalaSshDataTablePartitionType;
	
	//SSH Scoring table
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	@Value("${impala.score.ssh.table.delimiter}")
	private String impalaSshScoringTableDelimiter;
	@Value("${impala.score.ssh.table.name}")
	private String impalaSshScoringTableName;
	@Value("${hdfs.user.processeddata.sshscores.path}")
	private String impalaSshScoringDirectory;
	@Value("${impala.score.ssh.table.partition.type}")
	private String impalaSshScoringTablePartitionType;
	
	//Top SSH Scoring table
	@Value("${impala.score.ssh_top.table.fields}")
	private String impalaTopSshScoringTableFields;
	@Value("${impala.score.ssh_top.table.delimiter}")
	private String impalaTopSshScoringTableDelimiter;
	@Value("${impala.score.ssh_top.table.name}")
	private String impalaTopSshScoringTableName;
	@Value("${hdfs.user.processeddata.sshscores.top.path}")
	private String impalaTopSshScoringDirectory;
	@Value("${impala.score.ssh_top.table.partition.type}")
	private String impalaTopSshScoringTablePartitionType;
	
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

	// SSH Enriched Data table
	@Value("${impala.enricheddata.ssh.table.fields}")
	private String impalaSshEnrichedDataTableFields;
	@Value("${impala.enricheddata.ssh.table.delimiter}")
	private String impalaSshEnrichedDataTableDelimiter;
	@Value("${impala.enricheddata.ssh.table.name}")
	private String impalaSshEnrichedDataTableName;
	@Value("${hdfs.user.enricheddata.ssh.path}")
	private String impalaSshEnrichedDataDirectory;
	@Value("${impala.enricheddata.ssh.table.partition.type}")
	private String impalaSshEnrichedDataTablePartitionType;

	// VPN Enriched Data table
	@Value("${impala.enricheddata.vpn.table.fields}")
	private String impalaVpnEnrichedDataTableFields;
	@Value("${impala.enricheddata.vpn.table.delimiter}")
	private String impalaVpnEnrichedDataTableDelimiter;
	@Value("${impala.enricheddata.vpn.table.name}")
	private String impalaVpnEnrichedDataTableName;
	@Value("${hdfs.user.enricheddata.vpn.path}")
	private String impalaVpnEnrichedDataDirectory;
	@Value("${impala.enricheddata.vpn.table.partition.type}")
	private String impalaVpnEnrichedDataTablePartitionType;

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
		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		
		//Security Events Data table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecDataTablePartitionType);
		createTable(impalaSecDataTableName, impalaSecDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecDataTableDelimiter, impalaSecDataDirectory);
		
		//Security Events Login table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecLoginTablePartitionType);
		createTable(impalaSecLoginTableName, impalaSecLoginTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecLoginTableDelimiter, impalaSecLoginDirectory);

		//Security Events Login raw data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecLoginRawDataTablePartitionType);
		createTable(impalaSecLoginRawDataTableName, impalaSecLoginRawDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecLoginRawDataTableDelimiter, impalaSecLoginRawDataDirectory);


		//Security Events Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecScoringTablePartitionType);
		createTable(impalaSecScoringTableName, impalaSecScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecScoringTableDelimiter, impalaSecScoringDirectory);

		//Top Security Events Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecTopScoringTablePartitionType);
		createTable(impalaSecTopScoringTableName, impalaSecTopScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecTopScoringTableDelimiter, impalaSecTopScoringDirectory);
		
		// Security Events Login Scoring table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecLoginScoringTablePartitionType);
		createTable(impalaLoginScoringTableName, impalaLoginScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaLoginScoringTableDelimiter, impalaLoginScoringDirectory);
				
		//VPN Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaVpnDataTablePartitionType);
		createTable(impalaVpnDataTableName, impalaVpnDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaVpnDataTableDelimiter, impalaVpnDataDirectory);
		
		//VPN Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaVpnScoringTablePartitionType);
		createTable(impalaVpnScoringTableName, impalaVpnScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaVpnScoringTableDelimiter, impalaVpnScoringDirectory);

		//Top VPN Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopVpnScoringTablePartitionType);
		createTable(impalaTopVpnScoringTableName, impalaTopVpnScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopVpnScoringTableDelimiter, impalaTopVpnScoringDirectory);

		//VPN Session Scoring table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaVpnSessionScoringTablePartitionType);
		createTable(impalaVpnSessionScoringTableName, impalaVpnSessionScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaVpnSessionScoringTableDelimiter, impalaVpnSessionScoringDirectory);

		//Top VPN Session Scoring table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopVpnSessionScoringTablePartitionType);
		createTable(impalaTopVpnSessionScoringTableName, impalaTopVpnSessionScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopVpnSessionScoringTableDelimiter, impalaTopVpnSessionScoringDirectory);
		
		//SSH Data table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSshDataTablePartitionType);
		createTable(impalaSshDataTableName, impalaSshDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSshDataTableDelimiter, impalaSshDataDirectory);
		
		//SSH Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSshScoringTablePartitionType);
		createTable(impalaSshScoringTableName, impalaSshScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSshScoringTableDelimiter, impalaSshScoringDirectory);

		//SSH Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopSshScoringTablePartitionType);
		createTable(impalaTopSshScoringTableName, impalaTopSshScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopSshScoringTableDelimiter, impalaTopSshScoringDirectory);

		//AMT Data table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaAmtDataTablePartitionType);
		createTable(impalaAmtDataTableName, impalaAmtDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAmtDataTableDelimiter, impalaAmtDataDirectory);

        //AMT SessionData table
        partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaAmtSessionDataTablePartitionType);
        createTable(impalaAmtSessionDataTableName, impalaAmtSessionDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAmtSessionDataTableDelimiter, impalaAmtSessionDataDirectory);

		//AMT Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaAmtScoringTablePartitionType);
		createTable(impalaAmtScoringTableName, impalaAmtScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAmtScoringTableDelimiter, impalaAmtScoringDirectory);

		//AMT Top Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTopAmtScoringTablePartitionType);
		createTable(impalaTopAmtScoringTableName, impalaTopAmtScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaTopAmtScoringTableDelimiter, impalaTopAmtScoringDirectory);

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
				
		// Security Events (4769) Enriched Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSecEnrichedDataTablePartitionType);
		createTable(impalaSecEnrichedDataTableName, impalaSecEnrichedDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSecEnrichedDataTableDelimiter, impalaSecEnrichedDataDirectory);

		// SSH Enriched Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaSshEnrichedDataTablePartitionType);
		createTable(impalaSshEnrichedDataTableName, impalaSshEnrichedDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaSshEnrichedDataTableDelimiter, impalaSshEnrichedDataDirectory);

		// VPN Enriched Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaVpnEnrichedDataTablePartitionType);
		createTable(impalaVpnEnrichedDataTableName, impalaVpnEnrichedDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaVpnEnrichedDataTableDelimiter, impalaVpnEnrichedDataDirectory);


		// AMT Enriched Data table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaAmtEnrichedDataTablePartitionType);
		createTable(impalaAmtEnrichedDataTableName, impalaAmtEnrichedDataTableFields, partitionStrategy.getTablePartitionDefinition(), impalaAmtEnrichedDataTableDelimiter, impalaAmtEnrichedDataDirectory);


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
