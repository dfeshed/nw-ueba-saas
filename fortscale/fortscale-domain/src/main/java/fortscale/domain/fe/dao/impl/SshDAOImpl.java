package fortscale.domain.fe.dao.impl;

public class SshDAOImpl extends AuthDAOImpl{
	private String tableName = "sshscores";
	
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
