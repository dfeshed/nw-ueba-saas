package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;


public class LoginDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(LoginDAOImpl.class);
	
	@Value("${impala.score.ldapauth.table.name}")
	private String tableName;
	
	@Value("${impala.score.ldapauth.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.score.ldapauth.table.fields.time}")
	private String timeFieldName;
	@Value("${impala.score.ldapauth.table.fields.errorcode}")
	private String errorCodeFieldName;
	@Value("${impala.score.ldapauth.table.fields.SourceIp}")
	private String sourceFieldName;
	@Value("${impala.score.ldapauth.table.fields.TargetId}")
	private String destinationFieldName;
			
	
	
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
			authScore.setStatus(rs.getString(errorCodeFieldName));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	
	@Override
	public String getSourceFieldName() {
		return sourceFieldName;
	}
	@Override
	public String getDestinationFieldName() {
		return destinationFieldName;
	}
	@Override
	public String getStatusFieldName() {
		return errorCodeFieldName.toLowerCase();
	}
	@Override
	public String getInputFileHeaderDesc() {
		return impalaSecScoringTableFields;
	}
	@Override
	public String getEventTimeFieldName() {
		return timeFieldName.toLowerCase();
	}
	@Override
	public String getStatusSuccessValue() {
		return "0x0";
	}
	@Override
	public String getSourceIpFieldName() {
		return sourceFieldName;
	}
	
}
