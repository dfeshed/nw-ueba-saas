package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

public abstract class AuthDAOImpl extends AccessDAO<AuthScore> implements AuthDAO{
	private static Logger logger = Logger.getLogger(AuthDAOImpl.class);
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${impala.data.table.fields.normalized_username}")
	private String normalizedUsernameField;
	
	

	@Override
	public RowMapper<AuthScore> getMapper() {
		return new AuthScoreMapper();
	}

	@Override
	public String getNormalizedUsernameField() {
		return normalizedUsernameField.toLowerCase();
	}
	
	@Override
	public AuthScore createAccessObject(String normalizedUsername, String username) {
		AuthScore ret = new AuthScore();
		Map<String, Object> allFields = new HashMap<String, Object>();
		allFields.put(getNormalizedUsernameField(), normalizedUsername);
		allFields.put(getUsernameFieldName(), username);
		return ret;
	}


	
	

	
	class AuthScoreMapper implements RowMapper<AuthScore>{
		
		private int numOfErrors = 0;

		@Override
		public AuthScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthScore ret = new AuthScore();
			
			try{				
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				Map<String, Object> allFields = new HashMap<String, Object>(resultSetMetaData.getColumnCount());
				for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
					allFields.put(resultSetMetaData.getColumnName(i), rs.getObject(i));
				}
				ret.setAllFields(allFields);
				
				
			} catch (SQLException se){
				throw se;
			} catch (Exception e)  {
				numOfErrors++;
				if(numOfErrors < 5){
					ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
					logger.error("the following record caused an excption. record: {}", columnMapRowMapper.mapRow(rs, rowNum));
					logger.error("here is the exception",e);
				}
				return null;
			}
			
			return ret;
		}
	}	
}
