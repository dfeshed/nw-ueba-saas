package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;


public class LoginDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(LoginDAOImpl.class);
	private String tableName = "authenticationscores";
	public static final String ERROR_CODE_FIELD_NAME = "errorcode";
	
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
			authScore.setStatus(rs.getString(ERROR_CODE_FIELD_NAME));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getStatusFieldName() {
		return ERROR_CODE_FIELD_NAME;
	}
}
