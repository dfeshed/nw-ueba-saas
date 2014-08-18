package fortscale.domain.schema;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Schema Descriptor class for raw ssh events impala table and hdfs storage 
 */
@Component
public class SSHEvents implements TableSchema {

	@Value("${impala.data.ssh.table.name}")
	private String tableName;
	
	@Value("${impala.data.ssh.table.field.date_time}")
	public String DATE_TIME;
	@Value("${impala.data.ssh.table.field.epochtime}")
	public String EPOCHTIME;
	@Value("${impala.data.ssh.table.field.source_ip}")
	public String SOURCE_IP;
	@Value("${impala.data.ssh.table.field.target_machine}")
	public String TARGET_MACHINE;
	@Value("${impala.data.ssh.table.field.username}")
	public String USERNAME;
	@Value("${impala.data.ssh.table.field.status}")
	public String STATUS;
	@Value("${impala.data.ssh.table.field.auth_method}")
	public String AUTH_METHOD;
	@Value("${impala.data.ssh.table.field.hostname}")
	public String HOSTNAME;
	@Value("${impala.data.ssh.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
    @Value("${impala.data.ssh.table.partition.type}")
    private String impalaSshDataTablePartitionType;

	
	private PartitionStrategy partition = PartitionsUtils.getPartitionStrategy(impalaSshDataTablePartitionType);
	
	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public PartitionStrategy getPartitionStrategy() {
		return partition;
	}

	@Override
	public String getPartitionFieldName() {
		return partition.getImpalaPartitionFieldName();
	}
}
