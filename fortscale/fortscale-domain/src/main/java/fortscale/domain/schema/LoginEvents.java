package fortscale.domain.schema;

import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Schema Descriptor class for raw login events impala table and hdfs storage 
 */
@Component
public class LoginEvents implements TableSchema, InitializingBean {

	@Value("${impala.enricheddata.kerberos_tgt.table.name}")
	private String tableName;

	
	@Value("${impala.data.security.events.login.table.field.timeGeneratedRaw}")
	public String TIMEGENERATEDRAW;
	@Value("${impala.data.security.events.login.table.field.date_time}")
	public String TIMEGENERATED;
	@Value("${impala.data.security.events.login.table.field.date_time_unix}")
	public String TIMEGENERATEDUNIXTIME;
	@Value("${impala.data.security.events.login.table.field.account_name}")
	public String ACCOUNT_NAME;
	@Value("${impala.data.security.events.login.table.field.account_domain}")
	public String ACCOUNT_DOMAIN;
	@Value("${impala.data.security.events.login.table.field.security_id}")
	public String SECURITY_ID;
	@Value("${impala.data.security.events.login.table.field.eventCode}")
	public String EVENTCODE;
	@Value("${impala.data.security.events.login.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
	@Value("${impala.data.security.events.login.table.field.client_address}")
	public String CLIENT_ADDRESS;
	@Value("${impala.data.security.events.login.table.field.machine_name}")
	public String MACHINE_NAME;
	@Value("${impala.data.security.events.login.table.field.status}")
	public String STATUS;
	@Value("${impala.data.security.events.login.table.field.failure_code}")
	public String FAILURE_CODE;
	@Value("${impala.data.security.events.login.table.field.authentication_type}")
	public String AUTHENTICATION_TYPE;
	@Value("${impala.data.security.events.login.table.field.ticket_options}")
	public String TICKET_OPTIONS;
	@Value("${impala.data.security.events.login.table.field.forwardable}")
	public String FORWARDABLE;
	@Value("${impala.data.security.events.login.table.field.forwarded}")
	public String FORWARDED;
	@Value("${impala.data.security.events.login.table.field.proxied}")
	public String PROXIED;
	@Value("${impala.data.security.events.login.table.field.postdated}")
	public String POSTDATED;
	@Value("${impala.data.security.events.login.table.field.renew_request}")
	public String RENEW_REQUEST;
	@Value("${impala.data.security.events.login.table.field.constraint_delegation}")
	public String CONSTRAINT_DELEGATION;
	@Value("${impala.data.security.events.login.table.field.is_nat}")
	public String IS_NAT;
	@Value("${impala.data.security.events.login.table.field.src_class}")
	public String SRC_CLASS;
    @Value("${impala.enricheddata.kerberos_tgt.table.partition.type}")
    public String  impalaSecLoginTablePartitionType;

	private PartitionStrategy partition;
	
	@Override
	public void afterPropertiesSet()
		throws Exception {
		partition = PartitionsUtils.getPartitionStrategy(impalaSecLoginTablePartitionType);
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
