package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.AuthDAO;

public class AuthDAOImpl extends AccessDAO<AuthScore> implements AuthDAO{
	
	private String tableName = AuthScore.TABLE_NAME;

	@Override
	public RowMapper<AuthScore> getMapper() {
		return new AuthScoreMapper();
	}

	@Override
	public String getTimestampFieldName() {
		return AuthScore.TIMESTAMP_FIELD_NAME;
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
	public AuthScore createAccessObject(String userName, double globalScore, double eventScore, Date timestamp) {
		AuthScore ret = new AuthScore();
		ret.setUserName(userName);
		ret.setGlobalScore(globalScore);
		ret.setEventScore(eventScore);
		ret.setTimestamp(timestamp);
		return ret;
	}


	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String getInputFileHeaderDesc() {
		return AuthScore.implaValueTypeOrder;
	}

	
	class AuthScoreMapper implements RowMapper<AuthScore>{

		@Override
		public AuthScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthScore ret = new AuthScore();
			
			try{
				ret.setUserName(rs.getString(AuthScore.USERNAME_FIELD_NAME));
				ret.setTargetId(rs.getString(AuthScore.TARGET_ID_FIELD_NAME));
				ret.setSourceIp(rs.getString(AuthScore.SOURCE_IP_FIELD_NAME));
				ret.setErrorCode(rs.getString(AuthScore.ERROR_CODE_FIELD_NAME));
				ret.setEventScore(Double.parseDouble(rs.getString(AuthScore.EVENT_SCORE_FIELD_NAME)));
				ret.setGlobalScore(Double.parseDouble(rs.getString(AuthScore.GLOBAL_SCORE_FIELD_NAME)));
				ret.setTimestamp(parseTimestampDate(rs.getLong(AuthScore.TIMESTAMP_FIELD_NAME)));
				ret.setEventTime(parseEventTimeDate(rs.getString(AuthScore.EVENT_TIME_FIELD_NAME)));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			} catch (ParseException e) {
				throw new SQLException(e);
			}
			
			return ret;
		}
	}
	
	private static Date parseEventTimeDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(AuthScore.DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
	private static String formatEventTimeDate(Date date) {
		SimpleDateFormat pattern = new SimpleDateFormat(AuthScore.DATE_FORMAT);
		return pattern.format(date);
	}
	
	
	
	
	
	
	

	public static String toCsvLine(AuthScore authScore) {
		StringBuilder builder = new StringBuilder();
		appendValueToCsvLine(builder, authScore.getErrorCode(), ",");
		appendValueToCsvLine(builder, authScore.getSourceIp(), ",");
		appendValueToCsvLine(builder, authScore.getTargetId(), ",");
		appendValueToCsvLine(builder, authScore.getUserName(), ",");
		appendValueToCsvLine(builder, Double.toString(authScore.getEventScore()), ",");
		appendValueToCsvLine(builder, formatEventTimeDate(authScore.getEventTime()), ",");
		appendValueToCsvLine(builder, Double.toString(authScore.getGlobalScore()), ",");
		appendValueToCsvLine(builder, formatTimestampDate(authScore.getTimestamp()), "\n");

		return builder.toString();
	}
	
	public static String toCsvHeader() {
		StringBuilder builder = new StringBuilder();
		appendValueToCsvLine(builder, AuthScore.ERROR_CODE_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.SOURCE_IP_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.TARGET_ID_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.USERNAME_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.EVENT_SCORE_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.EVENT_TIME_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.GLOBAL_SCORE_FIELD_NAME, ",");
		appendValueToCsvLine(builder, AuthScore.TIMESTAMP_FIELD_NAME, "\n");
		return builder.toString();
	}
	
	private static void appendValueToCsvLine(StringBuilder builder, String value, String deleimiter) {
		builder.append(value).append(deleimiter);
	}
}
