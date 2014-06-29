package fortscale.domain.schema;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionStrategy;

/**
 * Schema Descriptor class for raw ldap authentication events impala table and hdfs storage 
 */
@Component
public class LDAPEvents implements TableSchema {

	@Value("${impala.data.security.events.4769.table.name}")
	private String tableName;
	
	@Value("${impala.data.security.events.4769.table.morphline.fields.username}")
	public String ACCOUNT_NAME;
	@Value("${impala.data.security.events.4769.table.field.timeGeneratedRaw}") 
	public String TIMEGENERATEDRAW;
	@Value("${impala.data.security.events.4769.table.field.timeGenerated}") 
	public String TIMEGENERATED;
	@Value("${impala.data.security.events.4769.table.field.categoryString}") 
	public String CATEGORYSTRING;
	@Value("${impala.data.security.events.4769.table.field.eventCode}")
	public String EVENTCODE;
	@Value("${impala.data.security.events.4769.table.field.logfile}")
	public String LOGFILE;
	@Value("${impala.data.security.events.4769.table.field.recordNumber}")
	public String RECORDNUMBER;
	@Value("${impala.data.security.events.4769.table.field.sourceName}")
	public String SOURCENAME;
	@Value("${impala.data.security.events.4769.table.field.username}")
	public String USERNAME;
	@Value("${impala.data.security.events.4769.table.field.account_domain}")
	public String ACCOUNT_DOMAIN;
	@Value("${impala.data.security.events.4769.table.field.service_name}")
	public String SERVICE_NAME;
	@Value("${impala.data.security.events.4769.table.field.service_id}")
	public String SERVICE_ID;
	@Value("${impala.data.security.events.4769.table.field.client_address}")
	public String CLIENT_ADDRESS;
	@Value("${impala.data.security.events.4769.table.field.ticket_options}")
	public String TICKET_OPTIONS;
	@Value("${impala.data.security.events.4769.table.field.failure_code}")
	public String FAILURE_CODE;
	@Value("${impala.data.security.events.4769.table.field.source_network_address}")
	public String SOURCE_NETWORK_ADDRESS;
	@Value("${impala.data.security.events.4769.table.field.timeGeneratedUnixTime}")
	public String TIMEGENERATEDUNIXTIME;
	@Value("${impala.data.security.events.4769.table.field.machine_name}")
	public String MACHINE_NAME;
	@Value("${impala.data.security.events.4769.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
	@Value("${impala.data.security.events.4769.table.field.dst_class}")
	public String DST_CLASS;
	@Value("${impala.data.security.events.4769.table.field.src_class}")
	public String SRC_CLASS;
	@Value("${impala.data.security.events.4769.table.field.is_user_serivce_account}")
	public String IS_USER_SERVICE_ACCOUNT;	
	@Value("${impala.data.security.events.4769.table.field.is_administrator_account}")
	public String IS_ADMINISTRATOR_ACCOUNT;	
	@Value("${impala.data.security.events.4769.table.field.is_executive_account}")
	public String IS_EXECUTIVE_ACCOUNT;
	@Value("${impala.data.security.events.4769.table.field.is_sensitive_machine}")
	public String IS_SENSITIVE_MACHINE;
	
	private PartitionStrategy partition = new MonthlyPartitionStrategy();
	
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
