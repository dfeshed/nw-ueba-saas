package fortscale.domain.fe.dao.impl;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("amtDAO")
public class AmtDAOImpl extends AccessDAO {
	@Value("${impala.score.amt.table.name}")
	private String tableName;
	@Value("${impala.score.amt.table.fields}")
	private String tableFields;
	@Value("${impala.score.amt.table.partition.type}")
	private String tablePartitionType;

	@Value("${impala.score.amt.table.field.date_time}")
	public String DATE_TIME;
	@Value("${impala.score.amt.table.field.epochtime}")
	public String EPOCHTIME;
	@Value("${impala.score.amt.table.field.date_time_score}")
	public String DATE_TIME_SCORE;
	@Value("${impala.score.amt.table.field.username}")
	public String USERNAME;
	@Value("${impala.score.amt.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
	@Value("${impala.score.amt.table.field.status}")
	public String STATUS;
	@Value("${impala.score.amt.table.field.source_ip}")
	public String SOURCE_IP;
	@Value("${impala.score.amt.table.field.is_nat}")
	public String IS_NAT;
	@Value("${impala.score.amt.table.field.hostname}")
	public String HOSTNAME;
	@Value("${impala.score.amt.table.field.normalized_src_machine}")
	public String NORMALIZED_SRC_MACHINE;
	@Value("${impala.score.amt.table.field.normalized_src_machine_score}")
	public String NORMALIZED_SRC_MACHINE_SCORE;
	@Value("${impala.score.amt.table.field.target_machine}")
	public String TARGET_MACHINE;
	@Value("${impala.score.amt.table.field.normalized_dst_machine}")
	public String NORMALIZED_DST_MACHINE;
	@Value("${impala.score.amt.table.field.normalized_dst_machine_score}")
	public String NORMALIZED_DST_MACHINE_SCORE;
	@Value("${impala.score.amt.table.field.yid}")
	public String YID;
	@Value("${impala.score.amt.table.field.yid_score}")
	public String YID_SCORE;
	@Value("${impala.score.amt.table.field.action_code}")
	public String ACTION_CODE;
	@Value("${impala.score.amt.table.field.action_code_score}")
	public String ACTION_CODE_SCORE;
	@Value("${impala.score.amt.table.field.target_ip}")
	public String TARGET_IP;
	@Value("${impala.score.amt.table.field.EventScore}")
	public String EVENT_SCORE;

	@Override
	public String getDestinationFieldName() {
		return TARGET_MACHINE;
	}

	@Override
	public String getStatusFieldName() {
		return STATUS;
	}

	@Override
	public String getStatusSuccessValue() {
		return "Accepted";
	}

	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.amt;
	}

	@Override
	public PartitionStrategy getPartitionStrategy() {
		return PartitionsUtils.getPartitionStrategy(tablePartitionType);
	}

	@Override
	public String getUsernameFieldName() {
		return USERNAME;
	}

	@Override
	public String getEventTimeFieldName() {
		return DATE_TIME.toLowerCase();
	}

	@Override
	public String getEventEpochTimeFieldName() {
		return EPOCHTIME.toLowerCase();
	}

	@Override
	public String getEventTimeScoreFieldName() {
		return DATE_TIME_SCORE.toLowerCase();
	}

	@Override
	public String getEventScoreFieldName() {
		return EVENT_SCORE.toLowerCase();
	}

	@Override
	public String getSourceFieldName() {
		return SOURCE_IP;
	}

	@Override
	public String getSourceIpFieldName() {
		return SOURCE_IP;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getTableName(int minScore) {
		return tableName + (minScore < minScoreForTopTable ? "" : "_top");
	}

	@Override
	public String getInputFileHeaderDesc() {
		return tableFields;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
