package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.fe.AuthScore;
import fortscale.utils.logging.Logger;

public class SshDAOImpl extends AuthDAOImpl{
	private static Logger logger = Logger.getLogger(SshDAOImpl.class);
	
	@Value("${impala.ssh.table.name}")
	private String tableName;
	public static final String STATUS_FIELD_NAME = "status";
	
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
			authScore.setStatus(rs.getString(STATUS_FIELD_NAME));
		} catch (Exception e) {
			logger.info("no status found in the login event");
			authScore.setStatus("");
		}
	}
	@Override
	public String getStatusFieldName() {
		return STATUS_FIELD_NAME;
	}
}
