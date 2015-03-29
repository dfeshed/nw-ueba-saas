package fortscale.domain.fe.dao.impl;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.utils.TimestampUtils;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.impala.ImpalaQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static fortscale.utils.impala.ImpalaCriteria.gte;
import static fortscale.utils.impala.ImpalaCriteria.lte;

@Component("amtsessionDAO")
public class AmtSessionDAOImpl extends AccessDAO {
	@Value("${impala.sessiondata.amt.table.name}")
	private String tableName;
	@Value("${impala.sessiondata.amt.table.fields}")
	private String tableFields;
	@Value("${impala.sessiondata.amt.table.partition.type}")
	private String tablePartitionType;

	@Value("${impala.sessiondata.amt.table.field.EventScore}")
	public String EVENT_SCORE;
	@Value("${impala.sessiondata.amt.table.field.end_time}")
	public String DATE_TIME;
	@Value("${impala.sessiondata.amt.table.field.date_time_unix}")
	public String EPOCHTIME;
	@Value("${impala.sessiondata.amt.table.field.duration}")
	public String DURATION;
	@Value("${impala.sessiondata.amt.table.field.duration_score}")
	public String DURATION_SCORE;
	@Value("${impala.sessiondata.amt.table.field.distinct_yid_count}")
	public String DISTINCT_YID_COUNT;
	@Value("${impala.sessiondata.amt.table.field.distinct_yid_count_score}")
	public String DISTINCT_YID_COUNT_SCORE;
	@Value("${impala.sessiondata.amt.table.field.username}")
	public String USERNAME;
	@Value("${impala.sessiondata.amt.table.field.normalized_username}")
	public String NORMALIZED_USERNAME;
	@Value("${impala.sessiondata.amt.table.field.amt_host}")
	public String HOSTNAME;
	@Value("${impala.sessiondata.amt.table.field.yid_rate}")
	public String YID_RATE;
	@Value("${impala.sessiondata.amt.table.field.avg_yid_counts}")
	public String AVG_YID_COUNTS;
	@Value("${impala.sessiondata.amt.table.field.avg_time_in_yid}")
	public String AVG_TIME_IN_YID;
	@Value("${impala.sessiondata.amt.table.field.avg_time_in_yid_score}")
	public String AVG_TIME_IN_YID_SCORE;
	@Value("${impala.sessiondata.amt.table.field.vip_yid}")
	public String VIP_YID;
	@Value("${impala.sessiondata.amt.table.field.sensitive_action_count}")
	public String SENSITIVE_ACTION_COUNT;
	@Value("${impala.sessiondata.amt.table.field.sensitive_action_count_score}")
	public String SENSITIVE_ACTION_COUNT_SCORE;
	@Value("${impala.sessiondata.amt.table.field.amt_host}")
	public String AMT_HOST;
	@Value("${impala.sessiondata.amt.table.field.amt_host_score}")
	public String AMT_HOST_SCORE;

	@Override
	public String getDestinationFieldName() {
		return "";
	}

	@Override
	public String getStatusFieldName() {
		return "";
	}

	@Override
	public String getStatusSuccessValue() {
		return "Accepted";
	}

	@Override
	public LogEventsEnum getLogEventsEnum() {
		return LogEventsEnum.amtsession;
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
		return "";
	}

	@Override
	public String getEventScoreFieldName() {
		return EVENT_SCORE.toLowerCase();
	}

	@Override
	public String getSourceFieldName() {
		return "";
	}

	@Override
	public String getSourceIpFieldName() {
		return "";
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

	@Override
	public void updateQueryTimeRanges(ImpalaQuery query, Long earliestDate, Long latestDate) {
		query.andWhere(gte(getEventEpochTimeFieldName(), Long.toString(TimestampUtils.convertToSeconds(earliestDate))));
		query.andWhere(lte(getEventEpochTimeFieldName(), Long.toString(TimestampUtils.convertToSeconds(latestDate))));
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
