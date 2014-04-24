package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;

public class SshDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(SshDAOImpl.class);
	
	@Value("${impala.score.ssh.table.name}")
	private String tableName;
	@Value("${impala.score.ssh.table.fields}")
	private String impalaSshScoringTableFields;
	@Value("${impala.score.ssh.table.fields.status}")
	private String statusFieldName;
	@Value("${impala.score.ssh.table.fields.time}")
	private String eventTimeFieldName;
	
	
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
}
