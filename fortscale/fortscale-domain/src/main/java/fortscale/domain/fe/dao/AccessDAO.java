package fortscale.domain.fe.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.domain.impala.ImpalaDAO;
import fortscale.utils.TimestampUtils;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;

public abstract class AccessDAO extends ImpalaDAO<Map<String, Object>> implements EventScoreDAO{
	private static Logger logger = Logger.getLogger(AccessDAO.class);
	
	protected static final String EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME = "day";
	protected static final String EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME = "status";
	protected static final String EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME = "eventcount";
	protected static final String EVENT_SOURCE_COALESCED_FIELD_NAME = "source";
	
	@Value("${impala.data.table.fields.normalized_username}")
	public String NORMALIZED_USERNAME;

		
	public abstract String getDestinationFieldName();
	
	public abstract String getStatusFieldName();
	
	public abstract String getStatusSuccessValue();
		
	public abstract LogEventsEnum getLogEventsEnum();
	

	
	@Override
	public String getNormalizedUsernameField() {
		return NORMALIZED_USERNAME.toLowerCase();
	}
	
	
	public List<EventScore> getEventScores(String username, int daysBack, int limit) {
		// build query
		ImpalaQuery query = new ImpalaQuery();
		query.select(getEventTimeFieldName(),
				String.format("if (%s='', %s, %s) as %s", getSourceFieldName(), getSourceIpFieldName(), getSourceFieldName(), EVENT_SOURCE_COALESCED_FIELD_NAME),
				getDestinationFieldName(),
				String.format("if(%s='%s','%s','%s') as %s", getStatusFieldName(), getStatusSuccessValue(), EventLoginDayCount.STATUS_SUCCESS, EventLoginDayCount.STATUS_FAILURE, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME),
				getEventScoreFieldName());
		query.from(getTableName());
		query.andWhere(getNormalizedUserNameEqualComparison(username));
		query.andWhere(String.format("datediff(to_date(now()),%s)<%d", getEventTimeFieldName(), daysBack));
		query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Direction.DESC, getEventTimeFieldName())));
				
		// perform query
		return impalaJdbcTemplate.query(query.toSQL(), new EventScoreMapper());
	}
	
	public String getEventLoginDayCountSqlQuery(String username, int numberOfDays) {
		ImpalaQuery query = new ImpalaQuery();
		
		String selectArgs = String.format("to_date(%s) as %s, if(%s='%s','%s','%s') as %s, count(*) as %s", getEventTimeFieldName(), EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME, getStatusFieldName(), getStatusSuccessValue(), EventLoginDayCount.STATUS_SUCCESS, EventLoginDayCount.STATUS_FAILURE, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME, EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME);
		query.select(selectArgs).from(getTableName()).andWhere(getNormalizedUserNameEqualComparison(username)).andWhere(String.format("datediff(to_date(now()),%s)<%d", getEventTimeFieldName(), numberOfDays))
				.groupBy(EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME).limitAndSort(new ImpalaPageRequest(numberOfDays*2, new Sort(Direction.ASC, EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME)));
		
		return query.toSQL();
	}
	
	public List<EventLoginDayCount> getEventLoginDayCount(String username, int numberOfDays) {
		return impalaJdbcTemplate.query(getEventLoginDayCountSqlQuery(username, numberOfDays), new EventLoginDayCountMapper());
	}

	public List<Map<String, Object>> findAll(Pageable pageable) {
		return super.findAll(pageable, getMapper());
	}

	private List<Map<String, Object>> getListResults(String query){
		return impalaJdbcTemplate.query(query, getMapper());
	}
	
	private List<Map<String, Object>> getListUsernamesResults(String query){
		return impalaJdbcTemplate.query(query, new UsernameMapper());
	}

	public List<Map<String, Object>> findEventsByNormalizedUsername(String username, Pageable pageable) {
		
		String query = String.format("select * from %s where %s %s",
				getTableName(),
				getNormalizedUserNameEqualComparison(username), pageable.toString());
		

		return getListResults(query);
	}
	
	public List<Map<String, Object>> findTopEventsByNormalizedUsername(String username, int limit, DateTime oldestEventTime, String decayScoreFieldName) {
//		String decayScoreFieldName = "decayscore";
		ImpalaQuery query = new ImpalaQuery();
		
		query.select(String.format("*, exp(-(unix_timestamp()-unix_timestamp(%s))/(60*60*24*50))*%s as %s", getEventTimeFieldName(),getEventScoreFieldName(),decayScoreFieldName));
		query.from(getTableName());
		query.andWhere(getNormalizedUserNameEqualComparison(username));
		query.andWhere(String.format("unix_timestamp(%s) >= %d", getEventTimeFieldName(), oldestEventTime.getMillis()/1000));
		PageRequest pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, decayScoreFieldName));
		query.limitAndSort(pageable);

		return getListResults(query.toSQL());
	}
	
	public List<Map<String, Object>> findEventsByNormalizedUsernameAndGtEventScoreAndBetweenTimes(String username, int minScore, Long latestDate, Long earliestDate, Pageable pageable) {		
		ImpalaQuery query = getFindAllEventsQuery(pageable);
		query.andGte(getEventScoreFieldName(), minScore);
		query.andWhere(getNormalizedUserNameEqualComparison(username));
		query.andWhere(String.format("unix_timestamp(%s) >= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(earliestDate)));
		query.andWhere(String.format("unix_timestamp(%s) <= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(latestDate)));
		
		return getListResults(query.toSQL());
	}
	
	public List<Map<String, Object>> findEventsByGtEventScore(Pageable pageable, int minScore) {
		return findEvents(pageable, String.format("%s >= %d", getEventScoreFieldName(), minScore));
	}
	
	

	public List<Map<String, Object>> findEvents(Pageable pageable, String additionalWhereQuery) {
		
		ImpalaQuery query = getFindAllEventsQuery(pageable);
		if (additionalWhereQuery != null && additionalWhereQuery.length() > 0) {
			query.andWhere(additionalWhereQuery);
		}

		return getListResults(query.toSQL());
	}
	
	public List<Map<String, Object>> findUsernames() {
		
		String query = String
				.format("select distinct %s, %s from %s",
						getNormalizedUsernameField(), getUsernameFieldName(),
						getTableName(),
						getNormalizedUsernameField(), getUsernameFieldName());
		

		return getListUsernamesResults(query);
	}

	public int countNumOfUsers() {
		String query = String.format(
				"select count(distinct(%s)) from %s",
				getNormalizedUsernameField(), getTableName());

		return impalaJdbcTemplate.queryForObject(query, Integer.class);
	}

	public int countNumOfEvents() {
		String query = String.format("select count(*) from %s",
				getTableName());

		return impalaJdbcTemplate.queryForObject(query, Integer.class);
	}

	public int countNumOfEventsByNormalizedUsername(String username){
		String query = String.format("select count(*) from %s where %s",
				getTableName(),
				getNormalizedUserNameEqualComparison(username));
		
		return impalaJdbcTemplate.queryForObject(query, Integer.class);
	}
	
	public int countNumOfEventsByNormalizedUsernameAndGtEScoreAndBetweenTimes(String username, int minScore, Long latestDate, Long earliestDate){
		ImpalaQuery impalaQuery = new ImpalaQuery();
		impalaQuery.select("count(*)").from(getTableName()).andWhere(getNormalizedUserNameEqualComparison(username)).andGte(getEventScoreFieldName(), minScore);
		impalaQuery.andWhere(String.format("unix_timestamp(%s) >= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(earliestDate)));
		impalaQuery.andWhere(String.format("unix_timestamp(%s) <= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(latestDate)));
		
		return impalaJdbcTemplate.queryForObject(impalaQuery.toSQL(), Integer.class);
	}
	
	public int countNumOfEventsByGTEScoreAndBetweenTimesAndNormalizedUsernameList(int minScore, Long latestDate, Long earliestDate, Collection<String> usernames){
		ImpalaQuery impalaQuery = new ImpalaQuery();
		impalaQuery.select("count(*)").from(getTableName()).andGte(getEventScoreFieldName(), minScore);
		impalaQuery.andWhere(String.format("unix_timestamp(%s) >= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(earliestDate)));
		impalaQuery.andWhere(String.format("unix_timestamp(%s) <= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(latestDate)));
		impalaQuery.andIn(getNormalizedUsernameField(), usernames);
		
		return impalaJdbcTemplate.queryForObject(impalaQuery.toSQL(), Integer.class);
	}
	
	public List<Map<String, Object>> findEventsByGtEventScoreBetweenTimeInUsernameList(Pageable pageable, Integer minScore, Long latestDate, Long earliestDate, Collection<String> usernames) {
		ImpalaQuery query = getFindAllEventsQuery(pageable);
		if(minScore != null){
			query.andGte(getEventScoreFieldName(), minScore);
		}
		query.andIn(getNormalizedUsernameField(), usernames);
		query.andWhere(String.format("unix_timestamp(%s) >= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(earliestDate)));
		query.andWhere(String.format("unix_timestamp(%s) <= %d", getEventTimeFieldName(), TimestampUtils.convertToSeconds(latestDate)));
		
		return getListResults(query.toSQL());
	}
	
	public ImpalaQuery getFindAllEventsQuery(Pageable pageable) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select("*").from(getTableName());
		
		query.limitAndSort(pageable);

		return query;
	}
	
	public int countNumOfEventsByNormalizedUsernameAndStatusRegex(String username, String statusVal){
		String query = String.format("select count(*) from %s where lower(%s) regexp \"%s\" and %s",
				getTableName(),
				getStatusFieldName(), statusVal.toLowerCase(),
				getNormalizedUserNameEqualComparison(username));
		
		return impalaJdbcTemplate.queryForObject(query, Integer.class);
	}

	public int countNumOfRecords(){
		String query = String.format("select count(*) from %s", getTableName());
		
		return impalaJdbcTemplate.queryForObject(query, Integer.class);
	}
	
	public List<Map<String, Object>> getTopEventsAboveThreshold(Threshold threshold,int limit) {
		
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, getEventScoreFieldName()));
		String query = String.format(
				"select * from %s where %s > %d %s", getTableName(),
				getEventScoreFieldName(), threshold.getValue(),
				pageable.toString());
		

		return getListResults(query);
	}
	
	
	private Map<String, Object> createAccessObject(String normalizedUsername, String username) {
		Map<String, Object> allFields = new HashMap<String, Object>();
		allFields.put(getNormalizedUsernameField(), normalizedUsername);
		allFields.put(getUsernameFieldName(), username);
		return allFields;
	}
	
	public RowMapper<Map<String, Object>> getMapper() {
		return new AuthScoreMapper();
	}
	
	class AuthScoreMapper implements RowMapper<Map<String, Object>>{
		
		private int numOfErrors = 0;

		@Override
		public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
			Map<String, Object> allFields;
			try{				
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				allFields = new HashMap<String, Object>(resultSetMetaData.getColumnCount());
				for(int i = 1; i <= resultSetMetaData.getColumnCount(); i++){
					allFields.put(resultSetMetaData.getColumnName(i), rs.getObject(i));
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
			
			return allFields;
		}
	}
	
	class EventLoginDayCountMapper implements RowMapper<EventLoginDayCount> {
		@Override
		public EventLoginDayCount mapRow(ResultSet rs, int rowNum) throws SQLException {
			String day = rs.getString(EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME);
			String status = rs.getString(EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME);
			int count = rs.getInt(EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME);

			return new EventLoginDayCount(day, status, count);
		}
	}
	
	class EventScoreMapper implements RowMapper<EventScore> {
		@Override
		public EventScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			long ts = rs.getTimestamp(getEventTimeFieldName()).getTime();
			String source = rs.getString(EVENT_SOURCE_COALESCED_FIELD_NAME);
			String destination = rs.getString(getDestinationFieldName().toLowerCase());
			String status = rs.getString(EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME);
			int score = rs.getInt(getEventScoreFieldName().toLowerCase());
			
			return new EventScore(getLogEventsEnum(), ts, source, destination, status, score); 
		}
	}
	
	class UsernameMapper implements RowMapper<Map<String, Object>> {
		
		private int numOfErrors = 0;

		@Override
		public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
			Map<String, Object> ret = null;

			try {
				ret = createAccessObject(rs.getString(getNormalizedUsernameField()), rs.getString(getUsernameFieldName()));
			} catch (SQLException se){
				throw se;
			} catch (Exception e) {
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

	private String getNormalizedUserNameEqualComparison(String username) {
		return String.format("lower(%s) = \"%s\"", getNormalizedUsernameField(),
				username.toLowerCase());
		// return String.format("%s rlike \"(?i)%s\"", getNormalizedUsernameField(),
		// username);
	}
}
