package fortscale.domain.schema;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Schema Descriptor class for raw vpn events impala table and hdfs storage 
 */
@Component
public class VpnEvents implements TableSchema, InitializingBean {

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
	
	@Value("${impala.data.vpn.table.field.region}")
	public String REGION;
	
	@Value("${impala.data.vpn.table.field.city}")
	public String CITY;
	
	@Value("${impala.data.vpn.table.field.isp}")
	public String ISP;
	
	@Value("${impala.data.vpn.table.field.ipusage}")
	public String IPUSAGE;
	
	@Value("${impala.data.vpn.table.field.hostname}")
	public String HOSTNAME;
	
	@Value("${impala.data.vpn.table.field.totalbytes}")
	public String TOTAL_BYTES;
	
	@Value("${impala.data.vpn.table.field.readbytes}")
	public String READ_BYTES;
	
	@Value("${impala.data.vpn.table.field.writebytes}")
	public String WRITE_BYTES;
	
	@Value("${impala.data.vpn.table.field.duration}")
	public String DURATION;
	
	@Value("${impala.data.vpn.table.field.databucket}")
	public String DATA_BUCKET;
	
	@Value("${impala.data.vpn.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;

	@Value("${impala.data.vpn.table.field.is_user_administrator}")
	public String IS_ADMINISTRATOR_ACCOUNT;
	
	@Value("${impala.data.vpn.table.field.is_user_executive}")
	public String IS_EXECUTIVE_ACCOUNT;

    @Value("${impala.data.vpn.table.partition.type}")
    public String impalaVpnDataTablePartitionType;

	private PartitionStrategy partition;
	
	@Override
	public void afterPropertiesSet()
		throws Exception {
		partition = PartitionsUtils.getPartitionStrategy(impalaVpnDataTablePartitionType);
	}
	
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
