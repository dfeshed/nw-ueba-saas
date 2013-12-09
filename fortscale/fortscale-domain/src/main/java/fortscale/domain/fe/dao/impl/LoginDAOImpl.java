package fortscale.domain.fe.dao.impl;


public class LoginDAOImpl extends AuthDAOImpl{
	private String tableName = "authenticationscores";
	
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
