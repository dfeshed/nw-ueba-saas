package fortscale.collection.hadoop;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaClient;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HadoopInit implements InitializingBean{
	
	
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
	
	//Group Membership Score table
	@Value("${impala.ldap.group.membership.scores.table.fields}")
	private String impalaGroupMembershipScoringTableFields;
	@Value("${impala.ldap.group.membership.scores.table.delimiter}")
	private String impalaGroupMembershipScoringTableDelimiter;
	@Value("${impala.ldap.group.membership.scores.table.name}")
	private String impalaGroupMembershipScoringTableName;
	@Value("${hdfs.user.processeddata.group.membership.score.path}")
	private String impalaGroupMembershipScoringDirectory;
	@Value("${impala.ldap.group.membership.scores.table.partition.type}")
	private String impalaGroupMembershipScoringTablePartitionType;
		
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
				
		//Group Membership Scoring table
		partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaGroupMembershipScoringTablePartitionType);
		createTable(impalaGroupMembershipScoringTableName, impalaGroupMembershipScoringTableFields, partitionStrategy.getTablePartitionDefinition(), impalaGroupMembershipScoringTableDelimiter, impalaGroupMembershipScoringDirectory);
		
	}
	
	private void createTable(String tableName, String fields, String partition, String delimiter, String location) throws IOException{
		if(!hadoopFs.exists(new Path(location))){
			hadoopFs.mkdirs(new Path(location));
		}
		try{
			impalaClient.createTable(tableName, fields, partition, delimiter, location);
		} catch(Exception e){
			//Nothing to do. just making sure that the table exist.
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {	
		createImpalaTables();
	}
}
