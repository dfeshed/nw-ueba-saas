package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.EventScore;
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
	public String getTimestampFieldName() {
		return AuthScore.TIMESTAMP_FIELD_NAME;
	}

	@Override
	public String getNormalizedUsernameField() {
		return normalizedUsernameField.toLowerCase();
	}

	@Override
	public String getUsernameFieldName() {
		return AuthScore.USERNAME_FIELD_NAME;
	}

	@Override
	public String getEventScoreFieldName() {
		return AuthScore.EVENT_SCORE_FIELD_NAME;
	}

	@Override
	public String getGlobalScoreFieldName() {
		return AuthScore.GLOBAL_SCORE_FIELD_NAME;
	}

	@Override
	public AuthScore createAccessObject(String normalizedUsername, double globalScore, double eventScore, Date timestamp) {
		AuthScore ret = new AuthScore();
		ret.setNormalizedUsername(normalizedUsername);
		ret.setGlobalScore(globalScore);
		ret.setEventScore(eventScore);
		ret.setTimestamp(timestamp);
		return ret;
	}
	
	@Override
	public AuthScore createAccessObject(String normalizedUsername, String username) {
		AuthScore ret = new AuthScore();
		ret.setNormalizedUsername(normalizedUsername);
		ret.setUserName(username);
		return ret;
	}


	
	

	
	class AuthScoreMapper implements RowMapper<AuthScore>{
		
		private int numOfErrors = 0;

		@Override
		public AuthScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthScore ret = new AuthScore();
			
			try{
				ret.setTimestamp(parseTimestampDate(rs.getLong(AuthScore.TIMESTAMP_FIELD_NAME)));
				
				ret.setNormalizedUsername(rs.getString(normalizedUsernameField));
				ret.setUserName(rs.getString(AuthScore.USERNAME_FIELD_NAME));
				ret.setTargetId(rs.getString(AuthScore.TARGET_ID_FIELD_NAME));
				ret.setSourceIp(rs.getString(AuthScore.SOURCE_IP_FIELD_NAME));
				ret.setEventTime(impalaParser.parseTimeDate(rs.getString(AuthScore.EVENT_TIME_FIELD_NAME)));
				
				ret.setUserNameScore(Double.parseDouble(rs.getString(AuthScore.USERNAME_SCORE_FIELD_NAME)));
				ret.setTargetIdScore(Double.parseDouble(rs.getString(AuthScore.TARGET_ID_SCORE_FIELD_NAME)));
				ret.setSourceIpScore(Double.parseDouble(rs.getString(AuthScore.SOURCE_IP_SCORE_FIELD_NAME)));
				ret.setEventTimeScore(Double.parseDouble(rs.getString(AuthScore.EVENT_TIME_SCORE_FIELD_NAME)));
				
				
				ret.setEventScore(Double.parseDouble(rs.getString(AuthScore.EVENT_SCORE_FIELD_NAME)));
				ret.setGlobalScore(Double.parseDouble(rs.getString(AuthScore.GLOBAL_SCORE_FIELD_NAME)));
				
				setStatus(rs, ret);
				
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				Map<String, Object> allFields = new HashMap<String, Object>(resultSetMetaData.getColumnCount());
				for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
					String columnName = resultSetMetaData.getColumnName(i);
					if(AuthScore.ERROR_CODE_FIELD_NAME.equals(columnName)){
						columnName = "errorCode";
					} else if(AuthScore.USERNAME_FIELD_NAME.equals(columnName)){
						columnName = "username";
					} else if(AuthScore.ERROR_CODE_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "errorCodeScore";
					} else if(AuthScore.EVENT_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "eventScore";
					} else if(AuthScore.EVENT_TIME_FIELD_NAME.equals(columnName)){
						columnName = "eventTime";
					} else if(AuthScore.EVENT_TIME_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "eventTimeScore";
					} else if(AuthScore.SOURCE_IP_FIELD_NAME.equals(columnName)){
						columnName = "sourceIp";
					} else if(AuthScore.SOURCE_IP_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "sourceIpScore";
					} else if(AuthScore.TARGET_ID_FIELD_NAME.equals(columnName)){
						columnName = "destinationHostname";
					} else if(AuthScore.TARGET_ID_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "targetIdScore";
					} else if(AuthScore.TIMESTAMP_FIELD_NAME.equals(columnName)){
						columnName = "timestamp";
					} else if(AuthScore.USERNAME_SCORE_FIELD_NAME.equals(columnName)){
						columnName = "userNameScore";
					}
					allFields.put(columnName, rs.getObject(i));
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
	
	protected abstract void setStatus(ResultSet rs, AuthScore authScore);
	
	
	
	
	

//	public static String toCsvLine(AuthScore authScore) {
//		StringBuilder builder = new StringBuilder();
//		appendValueToCsvLine(builder, authScore.getErrorCode(), ",");
//		appendValueToCsvLine(builder, authScore.getSourceIp(), ",");
//		appendValueToCsvLine(builder, authScore.getTargetId(), ",");
//		appendValueToCsvLine(builder, authScore.getUserName(), ",");
//		appendValueToCsvLine(builder, Double.toString(authScore.getEventScore()), ",");
//		appendValueToCsvLine(builder, ImpalaParser.formatTimeDate(authScore.getEventTime()), ",");
//		appendValueToCsvLine(builder, Double.toString(authScore.getGlobalScore()), ",");
//		appendValueToCsvLine(builder, formatTimestampDate(authScore.getTimestamp()), "\n");
//
//		return builder.toString();
//	}
//	
//	public static String toCsvHeader() {
//		StringBuilder builder = new StringBuilder();
//		appendValueToCsvLine(builder, AuthScore.ERROR_CODE_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.SOURCE_IP_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.TARGET_ID_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.USERNAME_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.EVENT_SCORE_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.EVENT_TIME_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.GLOBAL_SCORE_FIELD_NAME, ",");
//		appendValueToCsvLine(builder, AuthScore.TIMESTAMP_FIELD_NAME, "\n");
//		return builder.toString();
//	}
	
//	private static void appendValueToCsvLine(StringBuilder builder, String value, String deleimiter) {
//		builder.append(value).append(deleimiter);
//	}
}
