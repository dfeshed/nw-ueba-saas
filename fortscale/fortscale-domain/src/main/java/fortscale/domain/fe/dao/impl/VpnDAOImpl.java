package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

@Component("vpnDAO")
public class VpnDAOImpl extends AccessDAO<VpnScore> implements VpnDAO, InitializingBean{
	private static Logger logger = Logger.getLogger(VpnDAOImpl.class);
	
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${impala.vpn.table.name}")
	private String tableName;// = VpnScore.TABLE_NAME;

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
		
		private int numOfErrors = 0;

		@Override
		public VpnScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			VpnScore ret = new VpnScore();
			
			try{
				ret.setTimestamp(parseTimestampDate(rs.getLong(VpnScore.TIMESTAMP_FIELD_NAME)));
				
				ret.setEventTime(impalaParser.parseTimeDate(rs.getString(VpnScore.EVENT_TIME_FIELD_NAME)));
				ret.setUserName(rs.getString(VpnScore.USERNAME_FIELD_NAME));
				ret.setLocalIp(rs.getString(VpnScore.LOCAL_IP_FIELD_NAME));
				ret.setSourceIp(rs.getString(VpnScore.SOURCE_IP_FIELD_NAME));
				ret.setStatus(rs.getString(VpnScore.STATUS_FIELD_NAME));
				try{
					ret.setCountry(rs.getString(VpnScore.COUNTRY_FIELD_NAME));
				} catch(Exception e){
					logger.warn("got the following exception while trying to retrieve the country from the vpn score results table",e);
				}
				
				ret.setEventTimeScore(rs.getDouble(VpnScore.EVENT_TIME_SCORE_FIELD_NAME));
				ret.setUserNameScore(rs.getDouble(VpnScore.USERNAME_SCORE_FIELD_NAME));
				ret.setSourceIpScore(rs.getDouble(VpnScore.SOURCE_IP_SCORE_FIELD_NAME));
				ret.setStatusScore(rs.getDouble(VpnScore.STATUS_SCORE_FIELD_NAME));
				try{
					ret.setCountryScore(rs.getDouble(VpnScore.COUNTRY_SCORE_FIELD_NAME));
				} catch(Exception e){
					logger.warn("got the following exception while trying to retrieve the country score from the vpn score results table",e);
				}
				
				
				ret.setEventScore(rs.getDouble(VpnScore.EVENT_SCORE_FIELD_NAME));
				ret.setGlobalScore(rs.getDouble(VpnScore.GLOBAL_SCORE_FIELD_NAME));
				
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


	@Override
	public String getStatusFieldName() {
		return VpnScore.STATUS_FIELD_NAME;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(tableName);
	}
	
}
