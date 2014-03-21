package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fortscale.domain.core.ImpalaResultSetToBeanItemConverter;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

@Component("vpnDAO")
public class VpnDAOImpl extends AccessDAO<VpnScore> implements VpnDAO, InitializingBean{
	private static Logger logger = Logger.getLogger(VpnDAOImpl.class);
	
	
	@Value("${impala.data.table.fields.normalized_username}")
	private String normalizedUsernameField;
	
	@Value("${impala.score.vpn.table.field.status}")
	private String statusFieldName;
	
	@Value("${impala.score.vpn.table.field.country}")
	private String countryFieldName;
	
	@Value("${impala.score.vpn.table.field.region}")
	private String regionFieldName;
	
	@Value("${impala.score.vpn.table.field.city}")
	private String cityFieldName;
	
	@Value("${impala.score.vpn.table.field.isp}")
	private String ispFieldName;
	
	@Value("${impala.score.vpn.table.field.ipusage}")
	private String ipusageFieldName;
	
	@Value("${impala.table.fields.username}")
	private String usernameFieldName;
	
	@Value("${impala.score.vpn.table.field.local_ip}")
	private String localIpFieldName;
	
	@Value("${impala.score.vpn.table.field.source_ip}")
	private String sourceIpFieldName;
	
	@Value("${impala.score.vpn.table.field.date_time}")
	private String eventTimeFieldName;
	
	@Value("${impala.score.vpn.table.field.date_time.score}")
	private String eventTimeScoreFieldName;
	
	@Value("${impala.table.field.event.score}")
	private String eventScoreFieldName;
	
	@Value("${impala.table.field.global.score}")
	private String globalScoreFieldName;
	
	@Value("${impala.score.vpn.table.fields}")
	private String tableFieldDefinition;
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${impala.score.vpn.table.name}")
	private String tableName;
		
	private ImpalaResultSetToBeanItemConverter<VpnScore> converter;
	

	@Override
	public RowMapper<VpnScore> getMapper() {
		return new VpnScoreMapper();
	}

	@Override
	public String getTimestampFieldName() {
		return VpnScore.TIMESTAMP_FIELD_NAME;
	}
	
	@Override
	public String getNormalizedUsernameField() {
		return normalizedUsernameField;
	}

	@Override
	public String getUsernameFieldName() {
		return usernameFieldName;
	}
	
	@Override
	public String getStatusFieldName(){
		return statusFieldName;
	}
	
	@Override
	public String getCountryFieldName(){
		return countryFieldName;
	}
	
	@Override
	public String getRegionFieldName(){
		return regionFieldName;
	}
	
	@Override
	public String getCityFieldName(){
		return cityFieldName;
	}
	
	@Override
	public String getIspFieldName(){
		return ispFieldName;
	}
	
	@Override
	public String getIpusageFieldName(){
		return ipusageFieldName;
	}
	
	@Override
	public String getSourceIpFieldName(){
		return sourceIpFieldName;
	}
	
	@Override
	public String getLocalIpFieldName(){
		return localIpFieldName;
	}
	
	public String getEventTimeFieldName(){
		return eventTimeFieldName;
	}
	
	public String getEventTimeScoreFieldName(){
		return eventTimeScoreFieldName;
	}

	@Override
	public String getEventScoreFieldName() {
		return eventScoreFieldName;
	}

	@Override
	public String getGlobalScoreFieldName() {
		return globalScoreFieldName;
	}

	@Override
	public VpnScore createAccessObject(String normalizedUsername, double globalScore, double eventScore, Date timestamp) {
		VpnScore ret = new VpnScore();
		ret.setNormalized_username(normalizedUsername);
		ret.setGlobalScore(globalScore);
		ret.setEventScore(eventScore);
		ret.setRuntime((int) (timestamp.getTime()/1000));
		return ret;
	}
	
	@Override
	public VpnScore createAccessObject(String normalizedUsername, String username) {
		VpnScore ret = new VpnScore();
		ret.setNormalized_username(normalizedUsername);
		ret.setUsername(username);
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
		return tableFieldDefinition;
	}

	
	class VpnScoreMapper implements RowMapper<VpnScore>{
		
		private int numOfErrors = 0;

		@Override
		public VpnScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			VpnScore ret = new VpnScore();
			
			
			try{
				converter.convert(rs, ret);
				
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
					String columnName = resultSetMetaData.getColumnName(i);
					ret.putFieldValue(columnName, rs.getObject(i));
				}
				
//				ret.setTimestamp(parseTimestampDate(rs.getLong(VpnScore.TIMESTAMP_FIELD_NAME)));
//				
//				ret.setEventTime(impalaParser.parseTimeDate(rs.getString(VpnScore.EVENT_TIME_FIELD_NAME)));
//				ret.setNormalizedUsername(rs.getString(normalizedUsernameField));
//				ret.setUserName(rs.getString(VpnScore.USERNAME_FIELD_NAME));
//				ret.setLocalIp(rs.getString(VpnScore.LOCAL_IP_FIELD_NAME));
//				ret.setSourceIp(rs.getString(VpnScore.SOURCE_IP_FIELD_NAME));
//				ret.setStatus(rs.getString(statusFieldName));
//				try{
//					ret.setCountry(rs.getString(VpnScore.COUNTRY_FIELD_NAME));
//				} catch(Exception e){
//					logger.warn("got the following exception while trying to retrieve the country from the vpn score results table",e);
//				}
//				
//				ret.setEventTimeScore(rs.getDouble(VpnScore.EVENT_TIME_SCORE_FIELD_NAME));
//				ret.setUserNameScore(rs.getDouble(VpnScore.USERNAME_SCORE_FIELD_NAME));
//				ret.setSourceIpScore(rs.getDouble(VpnScore.SOURCE_IP_SCORE_FIELD_NAME));
//				ret.setStatusScore(rs.getDouble(VpnScore.STATUS_SCORE_FIELD_NAME));
//				try{
//					ret.setCountryScore(rs.getDouble(VpnScore.COUNTRY_SCORE_FIELD_NAME));
//				} catch(Exception e){
//					logger.warn("got the following exception while trying to retrieve the country score from the vpn score results table",e);
//				}
//				
//				
//				ret.setEventScore(rs.getDouble(VpnScore.EVENT_SCORE_FIELD_NAME));
//				ret.setGlobalScore(rs.getDouble(VpnScore.GLOBAL_SCORE_FIELD_NAME));
				
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
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(tableName);
		HashMap<String, Class<?>> fieldsToClassMap = ImpalaParser.getTableFieldDefinitionMap(tableFieldDefinition);
		//Adding runtime. TODO: Add this by using partition definition.
		fieldsToClassMap.put(VpnScore.TIMESTAMP_FIELD_NAME, String.class);
		converter =  new ImpalaResultSetToBeanItemConverter<>(new VpnScore());
	}
	
}
