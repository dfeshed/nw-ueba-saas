package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.VpnDAO;

public class VpnDAOImpl extends AccessDAO<VpnScore> implements VpnDAO{
	
	private String tableName = VpnScore.TABLE_NAME;

	@Override
	public RowMapper<VpnScore> getMapper() {
		return new VpnScoreMapper();
	}

	@Override
	public String getTimestampFieldName() {
		return VpnScore.TIMESTAMP_FIELD_NAME;
	}

	@Override
	public String getUsernameFieldName() {
		return VpnScore.USERNAME_FIELD_NAME;
	}

	@Override
	public String getEventScoreFieldName() {
		return VpnScore.EVENT_SCORE_FIELD_NAME;
	}

	@Override
	public String getGlobalScoreFieldName() {
		return VpnScore.GLOBAL_SCORE_FIELD_NAME;
	}

	@Override
	public VpnScore createAccessObject(String userName, double globalScore,
			double eventScore, Date timestamp) {
		VpnScore ret = new VpnScore();
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
		return VpnScore.implaValueTypeOrder;
	}

	
	class VpnScoreMapper implements RowMapper<VpnScore>{

		@Override
		public VpnScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			VpnScore ret = new VpnScore();
			
			try{
				ret.setUserName(rs.getString(VpnScore.USERNAME_FIELD_NAME));
				ret.setLocalIp(rs.getString(VpnScore.LOCAL_IP_FIELD_NAME));
				ret.setSourceIp(rs.getString(VpnScore.SOURCE_IP_FIELD_NAME));
				ret.setStatus(rs.getString(VpnScore.STATUS_FIELD_NAME));
				ret.setEventScore(Double.parseDouble(rs.getString(VpnScore.EVENT_SCORE_FIELD_NAME)));
				ret.setGlobalScore(Double.parseDouble(rs.getString(VpnScore.GLOBAL_SCORE_FIELD_NAME)));
				ret.setTimestamp(parseTimestampDate(rs.getLong(VpnScore.TIMESTAMP_FIELD_NAME)));
				ret.setEventTime(parseEventTimeDate(rs.getString(VpnScore.EVENT_TIME_FIELD_NAME)));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			} catch (ParseException e) {
				throw new SQLException(e);
			}
			
			return ret;
		}
	}
	
	private static Date parseEventTimeDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(VpnScore.DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
}
