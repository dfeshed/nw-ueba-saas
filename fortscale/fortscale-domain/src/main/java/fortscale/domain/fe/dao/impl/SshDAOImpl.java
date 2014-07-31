package fortscale.domain.fe.dao.impl;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;

public class SshDAOImpl extends AccessDAO{
	
	@Value("${impala.score.ssh.table.name}")
	private String tableName;
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	@Value("${impala.score.ssh.table.partition.type}")
	private String partitionName;
	
	@Value("${impala.score.ssh.table.fields.date_time}")
	public String DATE_TIME;
	
	@Value("${impala.score.ssh.table.field.epochtime}")
	public String EPOCHTIME;
	
	@Value("${impala.score.ssh.table.fields.date_time_score}")
	public String DATE_TIME_SCORE;
							
	@Value("${impala.score.ssh.table.field.username}")
	public String USERNAME;
									
	@Value("${impala.score.ssh.table.fields.normalized_username}")
	public String NORMALIZED_USERNAME;
	
	@Value("${impala.score.ssh.table.fields.status}")
	public String STATUS;
													
	@Value("${impala.score.ssh.table.fields.auth_method}")
	public String AUTH_METHOD;
															
	@Value("${impala.score.ssh.table.fields.auth_method_score}")
	public String AUTH_METHOD_SCORE;
																	
	@Value("${impala.score.ssh.table.fields.source_ip}")
	public String SOURCE_IP;
																			
	@Value("${impala.score.ssh.table.field.is_nat}")
	public String IS_NAT;
	
	@Value("${impala.score.ssh.table.field.hostname}")
	public String HOSTNAME;
																					
	@Value("${impala.score.ssh.table.field.normalized_src_machine}")
	public String NORMALIZED_SRC_MACHINE;
																							
	@Value("${impala.score.ssh.table.field.normalized_src_machine_score}")
	public String NORMALIZED_SRC_MACHINE_SCORE;
																									
	@Value("${impala.score.ssh.table.field.target_machine}")
	public String TARGET_MACHINE;
																											
	@Value("${impala.score.ssh.table.field.normalized_dst_machine}")
	public String NORMALIZED_DST_MACHINE;
																													
	@Value("${impala.score.ssh.table.field.normalized_dst_machine_score}")
	public String NORMALIZED_DST_MACHINE_SCORE;
	
	@Value("${impala.score.ssh.table.field.EventScore}")
	public String EVENT_SCORE;
	
	
	@Override
	public String getTableName() {
		return tableName;
	}
	@Override
	public String getTableName(int minScore) {
		if (minScore<50)
			return tableName;
		else
			return tableName + "_top";
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String getEventTimeFieldName() {
		return DATE_TIME.toLowerCase();
	}
	@Override
	public String getEventEpochTimeFieldName() {
		return EPOCHTIME.toLowerCase();
	}

	@Override
	public String getDestinationFieldName() {
		return TARGET_MACHINE;
	}
	@Override
	public String getStatusFieldName() {
		return STATUS;
	}
	@Override
	public String getStatusSuccessValue() {
		return "Accepted";
	}
	@Override
	public String getSourceIpFieldName() {
		return SOURCE_IP;
	}
	@Override
	public String getSourceFieldName() {
		return SOURCE_IP;
	}
	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.ssh;
	}
	@Override
	public String getInputFileHeaderDesc() {
		return impalaSshScoringTableFields;
	}
	@Override
	public PartitionStrategy getPartitionStrategy() {
		return PartitionsUtils.getPartitionStrategy(partitionName);
	}
	@Override
	public String getEventTimeScoreFieldName() {
		return DATE_TIME_SCORE.toLowerCase();
	}
	@Override
	public String getUsernameFieldName() {
		return USERNAME;
	}
	@Override
	public String getEventScoreFieldName() {
		return EVENT_SCORE.toLowerCase();
	}
}
