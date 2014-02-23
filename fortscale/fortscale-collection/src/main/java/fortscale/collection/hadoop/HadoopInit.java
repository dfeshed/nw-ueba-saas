package fortscale.collection.hadoop;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;

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
	
	//VPN Data table
	@Value("${impala.data.vpn.table.fields}")
	private String impalaVpnDataTableFields;
	@Value("${impala.data.vpn.table.delimiter}")
	private String impalaVpnDataTableDelimiter;
	@Value("${impala.data.vpn.table.name}")
	private String impalaVpnDataTableName;
	@Value("${hdfs.user.data.vpn.path}")
	private String impalaVpnDataDirectory;
	
	//SSH Data table
	@Value("${impala.data.ssh.table.fields}")
	private String impalaSshDataTableFields;
	@Value("${impala.data.ssh.table.delimiter}")
	private String impalaSshDataTableDelimiter;
	@Value("${impala.data.ssh.table.name}")
	private String impalaSshDataTableName;
	@Value("${hdfs.user.data.ssh.path}")
	private String impalaSshDataDirectory;
	
	
	
	

	public void createDirectories() throws IOException{
		for(String dir: impalaDirectories.split(",")){
			if(!hadoopFs.exists(new Path(dir))){
				hadoopFs.mkdirs(new Path(dir));
			}
		}
	}
	
	public void createImpalaTables(){
		MonthlyPartitionStrategy monthlyPartitionStrategy = new MonthlyPartitionStrategy();
		//Users table
		createTable(impalaUserTableName, impalaUserFields, null, impalaUserTableDelimiter, impalaUsersDirectory);
		
		//Security Events Data table
		createTable(impalaSecDataTableName, impalaSecDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaSecDataTableDelimiter, impalaSecDataDirectory);
		
		//VPN Data table
		createTable(impalaVpnDataTableName, impalaVpnDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaVpnDataTableDelimiter, impalaVpnDataDirectory);
				
		//SSH Data table
		createTable(impalaSshDataTableName, impalaSshDataTableFields, monthlyPartitionStrategy.getPartitionDefinition(), impalaSshDataTableDelimiter, impalaSshDataDirectory);
		
	}
	
	private void createTable(String tableName, String fields, String partition, String delimiter, String location){
		try{
			impalaClient.createTable(tableName, fields, partition, delimiter, location);
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
