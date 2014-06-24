package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fortscale.domain.core.ImpalaResultSetToBeanItemConverter;
import fortscale.domain.events.LogEventsEnum;
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
	
	@Value("${impala.score.vpn.table.name}")
	private String tableName;
	
	@Value("${impala.score.vpn.table.fields}")
	private String tableFieldDefinition;
		
	
	@Value("${impala.score.vpn.table.field.date_time_unix}")
	public String DATE_TIME_UNIX;
			
	@Value("${impala.score.vpn.table.field.hostname.score}")
	public String HOSTNAME_SCORE;

	@Value("${impala.score.vpn.table.field.country.score}")
	public String COUNTRY_SCORE;
			
	@Value("${impala.score.vpn.table.field.countrycode}")
	public String COUNTRY_CODE;
			
	@Value("${impala.data.table.fields.normalized_username}")
	public String NORMALIZED_USERNAME;
	
	@Value("${impala.score.vpn.table.field.status}")
	public String STATUS;
	
	@Value("${impala.score.vpn.table.field.country}")
	public String COUNTRY;
	
	@Value("${impala.score.vpn.table.field.region}")
	public String REGION;
	
	@Value("${impala.score.vpn.table.field.city}")
	public String CITY;
	
	@Value("${impala.score.vpn.table.field.isp}")
	public String ISP;
	
	@Value("${impala.score.vpn.table.field.ipusage}")
	public String IP_USAGE;
	
	@Value("${impala.table.fields.username}")
	public String USERNAME;
	
	@Value("${impala.score.vpn.table.field.local_ip}")
	public String LOCAL_IP;
	
	@Value("${impala.score.vpn.table.field.source_ip}")
	public String SOURCE_IP;
	
	@Value("${impala.score.vpn.table.field.date_time}")
	public String DATE_TIME;
	
	@Value("${impala.score.vpn.table.field.date_timeScore}")
	public String TIME_SCORE;
	
	@Value("${impala.score.vpn.table.field.event.score}")
	public String EVENT_SCORE;
	
	@Value("${impala.score.vpn.table.field.hostname}")
	public String HOSTNAME;
	
	private ImpalaResultSetToBeanItemConverter<VpnScore> converter;
	

	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.vpn;
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
	

	@Override
	public String getCountryFieldName() {
		return COUNTRY;
	}

	@Override
	public String getRegionFieldName() {
		return REGION;
	}

	@Override
	public String getCityFieldName() {
		return CITY;
	}

	@Override
	public String getIspFieldName() {
		return ISP;
	}

	@Override
	public String getIpusageFieldName() {
		return IP_USAGE;
	}

	@Override
	public String getLocalIpFieldName() {
		return LOCAL_IP;
	}

	@Override
	public String getEventTimeScoreFieldName() {
		return TIME_SCORE;
	}

	@Override
	public String getEventTimeFieldName() {
		return DATE_TIME;
	}

	@Override
	public String getNormalizedUsernameField() {
		return NORMALIZED_USERNAME;
	}

	@Override
	public String getUsernameFieldName() {
		return USERNAME;
	}

	@Override
	public String getEventScoreFieldName() {
		return EVENT_SCORE;
	}

	@Override
	public String getSourceFieldName() {
		return HOSTNAME;
	}

	@Override
	public String getDestinationFieldName() {
		return LOCAL_IP;
	}

	@Override
	public String getStatusFieldName() {
		return STATUS;
	}

	@Override
	public String getStatusSuccessValue() {
		return "success";
	}

	@Override
	public String getSourceIpFieldName() {
		return SOURCE_IP;
	}
	
	@Override
	public RowMapper<VpnScore> getMapper() {
		return new VpnScoreMapper();
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(tableName);
		converter =  new ImpalaResultSetToBeanItemConverter<>(new VpnScore());
	}
	
	@Override
	public VpnScore createAccessObject(String normalizedUsername, String username) {
		VpnScore ret = new VpnScore();
		ret.setNormalized_username(normalizedUsername);
		ret.setUsername(username);
		return ret;
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
