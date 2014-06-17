package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;

public class SshDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(SshDAOImpl.class);
	
	
	@Value("${impala.score.ssh.table.fields.date_time}")
	private String eventTimeFieldName;
	
	@Value("${impala.score.ssh.table.field.epochtime}")
	private String epochTimeFieldName;
	
	@Value("${impala.score.ssh.table.fields.date_time_score}")
	private String dateTimeScoreFieldName;
							
	@Value("${impala.score.ssh.table.field.username}")
	private String usernameFieldName;
									
	@Value("${impala.score.ssh.table.fields.normalized_username}")
	private String normalizedUsernameFieldName;
	
	@Value("${impala.score.ssh.table.fields.status}")
	private String statusFieldName;
													
	@Value("${impala.score.ssh.table.fields.auth_method}")
	private String authMethodFieldName;
															
	@Value("${impala.score.ssh.table.fields.auth_method_score}")
	private String authMethodScoreFieldName;
																	
	@Value("${impala.score.ssh.table.fields.source_ip}")
	private String sourceIpFieldName;
																			
	@Value("${impala.score.ssh.table.field.is_nat}")
	private String isNatFieldName;
																					
	@Value("${impala.score.ssh.table.field.normalized_src_machine}")
	private String normalizedSrcMachineFieldName;
																							
	@Value("${impala.score.ssh.table.field.normalized_src_machine_score}")
	private String normalizedSrcMachineScoreFieldName;
																									
	@Value("${impala.score.ssh.table.field.target_machine}")
	private String targetMachineFieldName;
																											
	@Value("${impala.score.ssh.table.field.normalized_dst_machine}")
	private String normalizedDstMachineFieldName;
																													
	@Value("${impala.score.ssh.table.field.normalized_dst_machine_score}")
	private String normalizedDstMachineScoreFieldName;
	
	@Value("${impala.score.ssh.table.fields.EventScore}")
	private String eventScoreFieldName;
	
	
	@Value("${impala.score.ssh.table.name}")
	private String tableName;
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	
	
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
			authScore.setStatus(rs.getString(statusFieldName));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getStatusFieldName() {
		return statusFieldName.toLowerCase();
	}
	@Override
	public String getInputFileHeaderDesc() {
		return impalaSshScoringTableFields;
	}
	@Override
	public String getEventTimeFieldName() {
		return eventTimeFieldName.toLowerCase();
	}
	@Override
	public String getStatusSuccessValue() {
		return "Accepted";
	}
	@Override
	public String getSourceFieldName() {
		return sourceIpFieldName;
	}
	@Override
	public String getDestinationFieldName() {
		return targetMachineFieldName;
	}
	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.ssh;
	}
	@Override
	public String getSourceIpFieldName() {
		return sourceIpFieldName;
	}
	public String getEpochTimeFieldName() {
		return epochTimeFieldName;
	}
	public String getDateTimeScoreFieldName() {
		return dateTimeScoreFieldName;
	}
	public String getNormalizedUsernameFieldName() {
		return normalizedUsernameFieldName;
	}
	public String getAuthMethodFieldName() {
		return authMethodFieldName;
	}
	public String getAuthMethodScoreFieldName() {
		return authMethodScoreFieldName;
	}
	public String getIsNatFieldName() {
		return isNatFieldName;
	}
	public String getNormalizedSrcMachineFieldName() {
		return normalizedSrcMachineFieldName;
	}
	public String getNormalizedSrcMachineScoreFieldName() {
		return normalizedSrcMachineScoreFieldName;
	}
	public String getTargetMachineFieldName() {
		return targetMachineFieldName;
	}
	public String getNormalizedDstMachineFieldName() {
		return normalizedDstMachineFieldName;
	}
	public String getNormalizedDstMachineScoreFieldName() {
		return normalizedDstMachineScoreFieldName;
	}
	@Override
	public String getUsernameFieldName() {
		return usernameFieldName;
	}
	@Override
	public String getEventScoreFieldName() {
		return eventScoreFieldName;
	}
}
