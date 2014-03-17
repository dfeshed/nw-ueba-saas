package fortscale.domain.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;

/**
 * Schema Descriptor class for raw vpn events impala table and hdfs storage 
 */
@Component
public class VpnEvents implements TableSchema {

	@Value("${impala.data.vpn.table.name}")
	private String tableName;
	
	@Value("${impala.data.vpn.table.fiels.date_time}")
	public String DATE_TIME;
			
	@Value("${impala.data.vpn.table.field.epochtime}")
	public String DATE_TIME_UNIX;
	
	@Value("${impala.data.vpn.table.field.username}")
	public String USERNAME;
	
	@Value("${impala.data.vpn.table.field.source_ip}")
	public String SOURCE_IP;
	
	@Value("${impala.data.vpn.table.field.local_ip}")
	public String LOCAL_IP;
	
	@Value("${impala.data.vpn.table.field.status}")
	public String STATUS;
	
	@Value("${impala.data.vpn.table.field.country}")
	public String COUNTRY;
	
	@Value("${impala.data.vpn.table.field.hostname}")
	public String HOSTNAME;
	
	@Value("${impala.data.vpn.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
	
	private PartitionStrategy partition = new MonthlyPartitionStrategy();	
	
	@Override
	public String getTableName() {
		return tableName;
	}
	
	@Override
	public String getPartitionFieldName() {
		return partition.getImpalaPartitionFieldName();
	}
	
	@Override
	public PartitionStrategy getPartitionStrategy() {
		return partition;
	}
	
}
