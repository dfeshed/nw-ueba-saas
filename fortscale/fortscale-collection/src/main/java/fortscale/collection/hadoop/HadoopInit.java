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
	
	@Value("${hdfs.user.data.security.events.4769.path},${hdfs.user.data.ssh.path},${hdfs.user.data.vpn.path},${hdfs.user.data.ldap.users.path},${hdfs.user.data.ldap.groups.path},${hdfs.user.data.users.path},${hdfs.user.processeddata.security.events.4769.path},${hdfs.user.processeddata.sshscores.path},${hdfs.user.processeddata.vpnscores.path}, ${hdfs.user.processeddata.totalscore.path}, ${hdfs.user.processeddata.group.membership.score.path}")
	private String impalaDirectories;
	
	//Users table
	@Value("${impala.user.fields}")
	private String impalaUserFields;
	@Value("${impala.user.table.delimiter}")
	private String impalaUserTableDelimiter;
	@Value("${impala.user.table.name}")
	private String impalaUserTableName;
	@Value("${hdfs.user.data.users.path}")
	private String impalaUsersDirectory;
	
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
	@Value("${impala.login.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.login.table.delimiter}")
	private String impalaSecScoringTableDelimiter;
	@Value("${impala.login.table.name}")
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
	@Value("${impala.vpn.table.fields}")
	private String impalaVpnScoringTableFields;
	@Value("${impala.vpn.table.delimiter}")
	private String impalaVpnScoringTableDelimiter;
	@Value("${impala.vpn.table.name}")
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
	@Value("${impala.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	@Value("${impala.ssh.table.delimiter}")
	private String impalaSshScoringTableDelimiter;
	@Value("${impala.ssh.table.name}")
	private String impalaSshScoringTableName;
	@Value("${hdfs.user.processeddata.sshscores.path}")
	private String impalaSshScoringDirectory;
	
	

	public void createDirectories() throws IOException{
		for(String dir: impalaDirectories.split(",")){
			if(!hadoopFs.exists(new Path(dir))){
				hadoopFs.mkdirs(new Path(dir));
			}
		}
	}
	
	public void createImpalaTables(){
		MonthlyPartitionStrategy monthlyPartitionStrategy = new MonthlyPartitionStrategy();
		RuntimePartitionStrategy runtimePartitionStrategy = new RuntimePartitionStrategy();
		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		
		//Security Events Data table
		createTable(impalaSecDataTableName, impalaSecDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaSecDataTableDelimiter, impalaSecDataDirectory);
		
		//Security Events Scoring table
		createTable(impalaSecScoringTableName, impalaSecScoringTableFields, runtimePartitionStrategy.getPartitionDefinition(), impalaSecScoringTableDelimiter, impalaSecScoringDirectory);
		
		//VPN Data table
		createTable(impalaVpnDataTableName, impalaVpnDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaVpnDataTableDelimiter, impalaVpnDataDirectory);
		
		//VPN Scoring table
		createTable(impalaVpnScoringTableName, impalaVpnScoringTableFields, runtimePartitionStrategy.getPartitionDefinition(), impalaVpnScoringTableDelimiter, impalaVpnScoringDirectory);
		
		//VPN View Table
		createTableView("view_vpndata", "SELECT date_time,date_time_unix,username,if(hostname != \"\",hostname,source_ip) as source_ip,local_ip,status,country,yearmonth FROM vpndata");
				
		//SSH Data table
		createTable(impalaSshDataTableName, impalaSshDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaSshDataTableDelimiter, impalaSshDataDirectory);
		
		//SSH Scoring table
		createTable(impalaSshScoringTableName, impalaSshScoringTableFields, runtimePartitionStrategy.getPartitionDefinition(), impalaSshScoringTableDelimiter, impalaSshScoringDirectory);
		
	}
	
	private void createTable(String tableName, String fields, String partition, String delimiter, String location){
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
		createDirectories();
		
		createImpalaTables();
	}
}
