package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.logging.Logger;


public class LoginDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(LoginDAOImpl.class);
	
	@Value("${impala.score.ldapauth.table.name}")
	private String tableName;	
	@Value("${impala.score.ldapauth.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.score.ldapauth.table.partition.type}")
	private String partitionName;
	@Value("${impala.score.ldapauth.table.fields.timeGenerated}")
	public String TIMEGENERATED;
	@Value("${impala.score.ldapauth.table.fields.timeGeneratedUnixTime}")
	public String TIMEGENERATED_UNIX;
	@Value("${impala.score.ldapauth.table.fields.failure_code}")
	public String FAILURE_CODE;
	@Value("${impala.score.ldapauth.table.fields.machine_name}")
	public String MACHINE_NAME;
	@Value("${impala.score.ldapauth.table.fields.service_name}")
	public String SERVICE_NAME;
	@Value("${impala.score.ldapauth.table.fields.account_name}")
	public String ACCOUNT_NAME;
	@Value("${impala.score.ldapauth.table.fields.eventscore}")
	public String EVENT_SCORE;
	@Value("${impala.score.ldapauth.table.fields.client_address}")
	public String SOURCE_IP;
	
		
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.login;
	}
	
	@Override
	protected void setStatus(ResultSet rs, AuthScore authScore) {
		try {
			authScore.setStatus(rs.getString(FAILURE_CODE));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getEventTimeFieldName() {
		return TIMEGENERATED.toLowerCase();
	}
	@Override
	public String getSourceFieldName() {
		return MACHINE_NAME;
	}
	@Override
	public String getDestinationFieldName() {
		return SERVICE_NAME;
	}
	@Override
	public String getStatusFieldName() {
		return FAILURE_CODE.toLowerCase();
	}
	@Override
	public String getStatusSuccessValue() {
		return "0x0";
	}
	@Override
	public String getSourceIpFieldName() {
		return SOURCE_IP;
	}
	@Override
	public String getInputFileHeaderDesc() {
		return impalaSecScoringTableFields;
	}
	@Override
	public PartitionStrategy getPartitionStrategy() {
		return PartitionsUtils.getPartitionStrategy(partitionName);
	}
}
