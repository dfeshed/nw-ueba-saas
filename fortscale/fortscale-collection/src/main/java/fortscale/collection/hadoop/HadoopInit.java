package fortscale.collection.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.RuntimePartitionStrategy;

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
	
	//Security Events Data table
	@Value("${impala.data.security.events.4769.table.fields}")
	private String impalaSecDataTableFields;
	@Value("${impala.data.security.events.4769.table.delimiter}")
	private String impalaSecDataTableDelimiter;
	@Value("${impala.data.security.events.4769.table.name}")
	private String impalaSecDataTableName;
	@Value("${hdfs.user.data.security.events.4769.path}")
	private String impalaSecDataDirectory;
	
	//Security Events Scoring table
	@Value("${impala.score.login.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.score.login.table.delimiter}")
	private String impalaSecScoringTableDelimiter;
	@Value("${impala.score.login.table.name}")
	private String impalaSecScoringTableName;
	@Value("${hdfs.user.processeddata.security.events.4769.path}")
	private String impalaSecScoringDirectory;
	
	//VPN Data table
	@Value("${impala.data.vpn.table.fields}")
	private String impalaVpnDataTableFields;
	@Value("${impala.data.vpn.table.delimiter}")
	private String impalaVpnDataTableDelimiter;
	@Value("${impala.data.vpn.table.name}")
	private String impalaVpnDataTableName;
	@Value("${hdfs.user.data.vpn.path}")
	private String impalaVpnDataDirectory;
	
	//VPN Scoring table
	@Value("${impala.score.vpn.table.fields}")
	private String impalaVpnScoringTableFields;
	@Value("${impala.score.vpn.table.delimiter}")
	private String impalaVpnScoringTableDelimiter;
	@Value("${impala.score.vpn.table.name}")
	private String impalaVpnScoringTableName;
	@Value("${hdfs.user.processeddata.vpnscores.path}")
	private String impalaVpnScoringDirectory;
	
	//SSH Data table
	@Value("${impala.data.ssh.table.fields}")
	private String impalaSshDataTableFields;
	@Value("${impala.data.ssh.table.delimiter}")
	private String impalaSshDataTableDelimiter;
	@Value("${impala.data.ssh.table.name}")
	private String impalaSshDataTableName;
	@Value("${hdfs.user.data.ssh.path}")
	private String impalaSshDataDirectory;
	
	//SSH Scoring table
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	@Value("${impala.score.ssh.table.delimiter}")
	private String impalaSshScoringTableDelimiter;
	@Value("${impala.score.ssh.table.name}")
	private String impalaSshScoringTableName;
	@Value("${hdfs.user.processeddata.sshscores.path}")
	private String impalaSshScoringDirectory;
	
	//Total Score table
	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
	@Value("${impala.total.scores.table.name}")
	private String impalaTotalScoringTableName;
	@Value("${hdfs.user.processeddata.totalscore.path}")
	private String impalaTotalScoringDirectory;
	
	
	//AD Computers table
	@Value("${impala.ldapcomputers.table.fields}")
	private String impalaAdComputerTableFields;
	@Value("${impala.ldapcomputers.table.delimiter}")
	private String impalaAdComputerTableDelimiter;
	@Value("${impala.ldapcomputers.table.name}")
	private String impalaAdComputerTableName;
	@Value("${hdfs.user.data.ldap.computers.path}")
	private String impalaAdComputerDirectory;
	
	//AD OUs table
	@Value("${impala.ldapous.table.fields}")
	private String impalaAdOUTableFields;
	@Value("${impala.ldapous.table.delimiter}")
	private String impalaAdOUTableDelimiter;
	@Value("${impala.ldapous.table.name}")
	private String impalaAdOUTableName;
	@Value("${hdfs.user.data.ldap.ous.path}")
	private String impalaAdOUDirectory;
	
	//AD Group table
	@Value("${impala.ldapgroups.table.fields}")
	private String impalaAdGroupTableFields;
	@Value("${impala.ldapgroups.table.delimiter}")
	private String impalaAdGroupTableDelimiter;
	@Value("${impala.ldapgroups.table.name}")
	private String impalaAdGroupTableName;
	@Value("${hdfs.user.data.ldap.groups.path}")
	private String impalaAdGroupDirectory;
	
	//AD User table
	@Value("${impala.ldapusers.table.fields}")
	private String impalaAdUserTableFields;
	@Value("${impala.ldapusers.table.delimiter}")
	private String impalaAdUserTableDelimiter;
	@Value("${impala.ldapusers.table.name}")
	private String impalaAdUserTableName;
	@Value("${hdfs.user.data.ldap.users.path}")
	private String impalaAdUserDirectory;
	
	//Group Membership Score table
	@Value("${impala.ldap.group.membership.scores.table.fields}")
	private String impalaGroupMembershipScoringTableFields;
	@Value("${impala.ldap.group.membership.scores.table.delimiter}")
	private String impalaGroupMembershipScoringTableDelimiter;
	@Value("${impala.ldap.group.membership.scores.table.name}")
	private String impalaGroupMembershipScoringTableName;
	@Value("${hdfs.user.processeddata.group.membership.score.path}")
	private String impalaGroupMembershipScoringDirectory;
		
	public void createImpalaTables() throws IOException{
		MonthlyPartitionStrategy monthlyPartitionStrategy = new MonthlyPartitionStrategy();
		RuntimePartitionStrategy runtimePartitionStrategy = new RuntimePartitionStrategy();
		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		
		//Security Events Data table
		createTable(impalaSecDataTableName, impalaSecDataTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaSecDataTableDelimiter, impalaSecDataDirectory);
		
		//Security Events Login table
		createTable(impalaSecLoginTableName, impalaSecLoginTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaSecLoginTableDelimiter, impalaSecLoginDirectory);
		
		//Security Events Scoring table
		createTable(impalaSecScoringTableName, impalaSecScoringTableFields, runtimePartitionStrategy.getTablePartitionDefinition(), impalaSecScoringTableDelimiter, impalaSecScoringDirectory);
		
		//VPN Data table
		createTable(impalaVpnDataTableName, impalaVpnDataTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaVpnDataTableDelimiter, impalaVpnDataDirectory);
		
		//VPN Scoring table
		createTable(impalaVpnScoringTableName, impalaVpnScoringTableFields, runtimePartitionStrategy.getTablePartitionDefinition(), impalaVpnScoringTableDelimiter, impalaVpnScoringDirectory);
		
		//VPN View Table
		createTableView("view_vpndata", "SELECT date_time,date_time_unix,username,if(hostname != \"\",hostname,source_ip) as source_ip,local_ip,status,country,yearmonth FROM vpndata");
				
		//SSH Data table
		createTable(impalaSshDataTableName, impalaSshDataTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaSshDataTableDelimiter, impalaSshDataDirectory);
		
		//SSH Scoring table
		createTable(impalaSshScoringTableName, impalaSshScoringTableFields, runtimePartitionStrategy.getTablePartitionDefinition(), impalaSshScoringTableDelimiter, impalaSshScoringDirectory);
		
		//Total Scoring table
		createTable(impalaTotalScoringTableName, impalaTotalScoringTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaTotalScoringTableDelimiter, impalaTotalScoringDirectory);
		
		//AD Computer table
		createTable(impalaAdComputerTableName, impalaAdComputerTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaAdComputerTableDelimiter, impalaAdComputerDirectory);

		//AD OU table
		createTable(impalaAdOUTableName, impalaAdOUTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaAdOUTableDelimiter, impalaAdOUDirectory);
				
		//AD Group table
		createTable(impalaAdGroupTableName, impalaAdGroupTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaAdGroupTableDelimiter, impalaAdGroupDirectory);
		
		//AD User table
		createTable(impalaAdUserTableName, impalaAdUserTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaAdUserTableDelimiter, impalaAdUserDirectory);
				
		//Group Membership Scoring table
		createTable(impalaGroupMembershipScoringTableName, impalaGroupMembershipScoringTableFields, monthlyPartitionStrategy.getTablePartitionDefinition(), impalaGroupMembershipScoringTableDelimiter, impalaGroupMembershipScoringDirectory);
		
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
	
	private void createTableView(String tableViewName, String selectStatement){
		try{
			impalaClient.createTableView(tableViewName, selectStatement);
		} catch(Exception e){
			//Nothing to do. just making sure that the table exist.
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {	
		createImpalaTables();
	}
}
