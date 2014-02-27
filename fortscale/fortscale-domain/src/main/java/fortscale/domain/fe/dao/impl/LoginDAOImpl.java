package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;


public class LoginDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(LoginDAOImpl.class);
	
	@Value("${impala.login.table.name}")
	private String tableName;
	
	@Value("${impala.login.table.fields}")
	private String impalaSecScoringTableFields;
	@Value("${impala.login.table.fields.errorcode}")
	private String errorCodeName;
	
	
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
			authScore.setStatus(rs.getString(errorCodeName));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getStatusFieldName() {
		return errorCodeName;
	}
	@Override
	public String getInputFileHeaderDesc() {
		return impalaSecScoringTableFields;
	}
}
