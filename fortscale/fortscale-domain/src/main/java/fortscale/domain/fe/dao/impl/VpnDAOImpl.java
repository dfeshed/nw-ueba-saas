package fortscale.domain.fe.dao.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaParser;

@Component("vpnDAO")
public class VpnDAOImpl extends AccessDAO implements InitializingBean{	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${impala.score.vpn.table.name}")
	private String tableName;
	
	@Value("${impala.score.vpn.table.fields}")
	private String tableFieldDefinition;
	
	@Value("${impala.score.vpn.table.partition.type}")
	private String partitionName;
	
	@Value("${impala.score.vpn.table.field.date_time_unix}")
	public String DATE_TIME_UNIX;
			
	@Value("${impala.score.vpn.table.field.hostname.score}")
	public String HOSTNAME_SCORE;

	@Value("${impala.score.vpn.table.field.country.score}")
	public String COUNTRY_SCORE;
			
	@Value("${impala.score.vpn.table.field.countrycode}")
	public String COUNTRY_CODE;
		
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

	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.vpn;
	}

	@Override
	public String getTableName() {
		return tableName;
	}
	@Override
	public String getTableName(int minScore) {
		if (minScore<50)
			return tableName;
		else
			return tableName + "_top";
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String getInputFileHeaderDesc() {
		return tableFieldDefinition;
	}
	

	public String getCountryFieldName() {
		return COUNTRY;
	}

	public String getRegionFieldName() {
		return REGION;
	}

	public String getCityFieldName() {
		return CITY;
	}

	public String getIspFieldName() {
		return ISP;
	}

	public String getIpusageFieldName() {
		return IP_USAGE;
	}

	public String getLocalIpFieldName() {
		return LOCAL_IP;
	}

	public String getEventTimeScoreFieldName() {
		return TIME_SCORE;
	}

	@Override
	public String getEventTimeFieldName() {
		return DATE_TIME;
	}
	@Override
	public String getEventEpochTimeFieldName() {
		return DATE_TIME_UNIX.toLowerCase();
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
		return "SUCCESS";
	}

	@Override
	public String getSourceIpFieldName() {
		return SOURCE_IP;
	}

	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(tableName);
	}
		
	@Override
	public PartitionStrategy getPartitionStrategy() {
		return PartitionsUtils.getPartitionStrategy(partitionName);
	}
}
