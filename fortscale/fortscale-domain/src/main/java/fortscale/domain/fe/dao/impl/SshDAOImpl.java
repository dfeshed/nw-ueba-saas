package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;

public class SshDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(SshDAOImpl.class);
	
	@Value("${impala.score.ssh.table.name}")
	private String tableName;
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	
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
	
	@Value("${impala.score.ssh.table.fields.EventScore}")
	public String EVENT_SCORE;
	
	
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	protected void setStatus(ResultSet rs, AuthScore authScore) {
		try {
			authScore.setStatus(rs.getString(STATUS));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getEventTimeFieldName() {
		return DATE_TIME.toLowerCase();
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
}
