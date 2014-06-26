package fortscale.domain.fe.dao.impl;

import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.fe.dao.AuthDAO;

public abstract class AuthDAOImpl extends AuthDAO{
	
	
	@Value("${impala.data.table.fields.normalized_username}")
	private String normalizedUsernameField;
	
	

	@Override
	public String getNormalizedUsernameField() {
		return normalizedUsernameField.toLowerCase();
	}
}
