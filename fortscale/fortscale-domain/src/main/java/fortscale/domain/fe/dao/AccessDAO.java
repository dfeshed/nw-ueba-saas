package fortscale.domain.fe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.domain.impala.ImpalaDAO;
import fortscale.utils.impala.ImpalaCriteria;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;

public abstract class AccessDAO<T> extends ImpalaDAO<T> {
	private static Logger logger = Logger.getLogger(AccessDAO.class);
	
	protected static final String EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME = "day";
	protected static final String EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME = "status";
	protected static final String EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME = "eventcount";
	

	private Date lastRunDate = null;

	public abstract RowMapper<T> getMapper();

	public abstract String getTimestampFieldName();
	
	public abstract String getEventTimeFieldName();
	
	public abstract String getNormalizedUsernameField();

	public abstract String getUsernameFieldName();

	public abstract String getEventScoreFieldName();

	public abstract String getGlobalScoreFieldName();
	
	public abstract String getSourceFieldName();
	
	public abstract String getDestinationFieldName();
	
	public abstract String getStatusFieldName();
	
	public abstract String getStatusSuccessValue();
	
	public abstract LogEventsEnum getLogEventsEnum();
	

	public abstract T createAccessObject(String normalizedUsername, double globalScore,	double eventScore, Date timestamp);
	public abstract T createAccessObject(String normalizedUsername, String username);
	
	
	public List<EventScore> getEventScores(String username, int daysBack, int limit) {
		// get latest runtime
		Date timestamp = getLastRunDate();
		
		// build query
		ImpalaQuery query = new ImpalaQuery();
		query.select(getEventTimeFieldName(), getSourceFieldName(), getDestinationFieldName(),
				String.format("if(%s='%s','%s','%s') as %s", getStatusFieldName(), getStatusSuccessValue(), EventLoginDayCount.STATUS_SUCCESS, EventLoginDayCount.STATUS_FAILURE, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME),
				getEventScoreFieldName());
		query.from(getTableName());
		query.andWhere(getNormalizedUserNameEqualComparison(username));
		query.andEq(getTimestampFieldName(), formatTimestampDate(timestamp));
		query.andWhere(String.format("datediff(to_date(now()),%s)<%d", getEventTimeFieldName(), daysBack));
		query.limitAndSort(new ImpalaPageRequest(limit, new Sort(Direction.DESC, getEventTimeFieldName())));
		
		logger.info("event score query: " + query.toSQL());
		
		// perform query
		return impalaJdbcTemplate.query(query.toSQL(), new EventScoreMapper());
	}
	
	public String getEventLoginDayCountSqlQuery(String username, int numberOfDays) {
		ImpalaQuery query = new ImpalaQuery();
		Date timestamp = getLastRunDate();
		
		String selectArgs = String.format("to_date(%s) as %s, if(%s='%s','%s','%s') as %s, count(*) as %s", getEventTimeFieldName(), EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME, getStatusFieldName(), getStatusSuccessValue(), EventLoginDayCount.STATUS_SUCCESS, EventLoginDayCount.STATUS_FAILURE, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME, EVENT_LOGIN_DAY_COUNT_COUNT_FIELD_NAME);
		query.select(selectArgs).from(getTableName()).andEq(getTimestampFieldName(), formatTimestampDate(timestamp)).andWhere(getNormalizedUserNameEqualComparison(username)).andWhere(String.format("datediff(to_date(now()),%s)<%d", getEventTimeFieldName(), numberOfDays))
				.groupBy(EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME, EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME).limitAndSort(new ImpalaPageRequest(numberOfDays*2, new Sort(Direction.DESC, EVENT_LOGIN_DAY_COUNT_DAY_FIELD_NAME)));
		
		return query.toSQL();
	}
	
	public List<EventLoginDayCount> getEventLoginDayCount(String username, int numberOfDays) {
		return impalaJdbcTemplate.query(getEventLoginDayCountSqlQuery(username, numberOfDays), new EventLoginDayCountMapper());
	}

	public List<T> findAll(Pageable pageable) {
		return super.findAll(pageable, getMapper());
	}

	public T findCurrentByNormalizedUsername(String username) {
		T ret = null;
		Pageable pageable = new ImpalaPageRequest(1, new Sort(Direction.DESC,
				getTimestampFieldName()));
		List<T> scoreList = findEventsByNormalizedUsername(username, pageable);
		if (scoreList.size() > 0) {
			ret = scoreList.get(0);
		}

		return ret;
	}

	public List<T> findEventsByNormalizedUsername(String username, Pageable pageable) {
		
		String query = String.format("select * from %s where %s",
				getTableName(), getNormalizedUserNameEqualComparison(username));
		if (pageable != null) {
			query = String.format("%s %s", query, pageable.toString());
		}
		

		return getListResults(query);
	}
	
	private List<T> getListResults(String query){
		List<T> ret = new ArrayList<>();
		for(T res: impalaJdbcTemplate.query(query, getMapper())){
			if(res != null){
				ret.add(res);
			}
		}
		return ret;
	}
	
	private List<T> getListGlobalResults(String query){
		List<T> ret = new ArrayList<>();
		for(T res: impalaJdbcTemplate.query(query, new GlobalScoreMapper())){
			if(res != null){
				ret.add(res);
			}
		}
		return ret;
	}
	
	private List<T> getListUsernamesResults(String query){
		List<T> ret = new ArrayList<>();
		for(T res: impalaJdbcTemplate.query(query, new UsernameMapper())){
			if(res != null){
				ret.add(res);
			}
		}
		return ret;
	}

	public List<T> findEventsByNormalizedUsernameAndTimestamp(String username,
			Date timestamp, Pageable pageable) {
		
		String query = String.format("select * from %s where %s=%s and %s %s",
				getTableName(), getTimestampFieldName(),
				formatTimestampDate(timestamp),
				getNormalizedUserNameEqualComparison(username), pageable.toString());
		

		return getListResults(query);
	}
	
	public List<T> findEventsByNormalizedUsernameAndTimestampGtEventScore(String username, Date timestamp, int minScore, Pageable pageable) {
		
		String query = String.format("select * from %s where %s=%s and %s and %s >= %d %s",
				getTableName(), 
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getNormalizedUserNameEqualComparison(username),
				getEventScoreFieldName(), minScore,
				pageable.toString());
		

		return getListResults(query);
	}

	public List<T> findEventsByTimestamp(Date timestamp, Pageable pageable) {
		return findEventsByTimestamp(timestamp, pageable, null);
	}
	
	public List<T> findEventsByTimestampGtEventScore(Date timestamp, Pageable pageable, int minScore) {
		return findEventsByTimestamp(timestamp, pageable, String.format("%s >= %d", getEventScoreFieldName(), minScore));
	}
	
	

	public List<T> findEventsByTimestamp(Date timestamp, Pageable pageable,
			String additionalWhereQuery) {
		
		ImpalaQuery query = getFindEventsByTimestampQuery(timestamp, pageable);
		if (additionalWhereQuery != null && additionalWhereQuery.length() > 0) {
			query.andWhere(additionalWhereQuery);
		}

		return getListResults(query.toSQL());
	}

	public List<T> findGlobalScoreByNormalizedUsername(String username, int limit) {
		
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(
				Direction.DESC, getTimestampFieldName()));
		String query = String
				.format("select %s, %s, %s, max(%s) as %s from %s where %s group by %s, %s, %s %s",
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), getEventScoreFieldName(),
						getEventScoreFieldName(), getTableName(),
						getNormalizedUserNameEqualComparison(username),
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), pageable.toString());
		

		return getListGlobalResults(query);
	}

	public List<T> findGlobalScoreByTimestamp(Date timestamp) {
		
		String query = String
				.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s group by %s, %s, %s",
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), getEventScoreFieldName(),
						getEventScoreFieldName(), getTableName(),
						getTimestampFieldName(),
						formatTimestampDate(timestamp),
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName());
		

		return getListGlobalResults(query);
	}
	
	public List<T> findUsernamesByTimestamp(Date timestamp) {
		
		String query = String
				.format("select %s, %s from %s where %s=%s group by %s, %s",
						getNormalizedUsernameField(), getUsernameFieldName(),
						getTableName(),
						getTimestampFieldName(), formatTimestampDate(timestamp),
						getNormalizedUsernameField(), getUsernameFieldName());
		

		return getListUsernamesResults(query);
	}

	@SuppressWarnings("deprecation")
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp) {
		String query = String.format(
				"select count(distinct(%s)) from %s where %s=%s and %s > %d",
				getNormalizedUsernameField(), getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), threshold.getValue());

		return impalaJdbcTemplate.queryForInt(query);
	}

	@SuppressWarnings("deprecation")
	public int countNumOfUsers(Date timestamp) {
		String query = String.format(
				"select count(distinct(%s)) from %s where %s=%s",
				getNormalizedUsernameField(), getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp));

		return impalaJdbcTemplate.queryForInt(query);
	}

	@SuppressWarnings("deprecation")
	public int countNumOfEvents(Date timestamp) {
		String query = String.format("select count(*) from %s where %s=%s",
				getTableName(), getTimestampFieldName(),
				formatTimestampDate(timestamp));

		return impalaJdbcTemplate.queryForInt(query);
	}

	@SuppressWarnings("deprecation")
	public int countNumOfEventsByNormalizedUsername(Date timestamp, String username){
		String query = String.format("select count(*) from %s where %s=%s and %s",
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getNormalizedUserNameEqualComparison(username));
		
		return impalaJdbcTemplate.queryForInt(query);
	}
	
	public int countNumOfEventsByNormalizedUsernameAndGtEScore(Date timestamp, String username, int minScore){
		ImpalaQuery impalaQuery = new ImpalaQuery();
		impalaQuery.select("count(*)").from(getTableName()).andEq(getTimestampFieldName(), timestampDateToLong(timestamp)).andWhere(getNormalizedUserNameEqualComparison(username)).andGte(getEventScoreFieldName(), minScore);
		
		return impalaJdbcTemplate.queryForObject(impalaQuery.toSQL(), Integer.class);
	}
	
	public int countNumOfEventsByGTEScoreAndNormalizedUsernameList(Date timestamp, int minScore, Collection<String> usernames){
		ImpalaQuery impalaQuery = new ImpalaQuery();
		impalaQuery.select("count(*)").from(getTableName()).andEq(getTimestampFieldName(), timestampDateToLong(timestamp)).andGte(getEventScoreFieldName(), minScore).andIn(getNormalizedUsernameField(), usernames);
		
		return impalaJdbcTemplate.queryForObject(impalaQuery.toSQL(), Integer.class);
	}
	
	public List<T> findEventsByTimestampGtEventScoreInUsernameList(Date timestamp, Pageable pageable, Integer minScore, Collection<String> usernames) {
		ImpalaQuery query = getFindEventsByTimestampQuery(timestamp, pageable);
		if(minScore != null){
			query.andGte(getEventScoreFieldName(), minScore);
		}
		query.andIn(getNormalizedUsernameField(), usernames);
		
		return getListResults(query.toSQL());
	}
	
	public ImpalaQuery getFindEventsByTimestampQuery(Date timestamp, Pageable pageable) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select("*")
				.from(getTableName())
				.where(ImpalaCriteria.equalsTo(getTimestampFieldName(),
						formatTimestampDate(timestamp)));
		
		query.limitAndSort(pageable);

		return query;
	}
	
	@SuppressWarnings("deprecation")
	public int countNumOfEventsByNormalizedUsernameAndStatusRegex(Date timestamp, String username, String statusVal){
		String query = String.format("select count(*) from %s where lower(%s) regexp \"%s\" and %s=%s and %s",
				getTableName(),
				getStatusFieldName(), statusVal.toLowerCase(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getNormalizedUserNameEqualComparison(username));
		
		return impalaJdbcTemplate.queryForInt(query);
	}

	public Date getLastRunDate() {
		Long lastRun = getLastRuntime();
		if(lastRun == null){
			if(countNumOfRecords() > 0){
				logger.debug("run time in the table {} is null", getTableName());
				throw new RuntimeException(String.format("run time in the table (%s) is null", getTableName()));
			} else{
				logger.debug("the table {} is empty", getTableName());
				throw new EmptyTableException(getTableName());
			}
		}
		Date retDate = parseTimestampDate(lastRun);
		lastRunDate = retDate;
		return retDate;
	}
	
	@SuppressWarnings("deprecation")
	public int countNumOfRecords(){
		String query = String.format("select count(*) from %s", getTableName());
		
		return impalaJdbcTemplate.queryForInt(query);
	}

	public Long getLastRuntime() {
		if (lastRunDate == null) {
			Calendar tmp = Calendar.getInstance();
			tmp.add(Calendar.DAY_OF_MONTH, -1);
			lastRunDate = new Date(tmp.getTimeInMillis());
		}
		String query = String.format("select max(%s) from %s",
				getTimestampFieldName(), getTableName());
		String queryWithHint = String.format("%s where %s >= %d", query,
				getTimestampFieldName(), lastRunDate.getTime() / 1000);
		Long lastRun = impalaJdbcTemplate.queryForObject(queryWithHint,
				Long.class);
		if (lastRun == null) {
			lastRun = impalaJdbcTemplate.queryForObject(query, Long.class);
		}

		return lastRun;
	}

	public List<Long> getDistinctRuntime() {
		String query = String.format("select distinct(%s) from %s",
				getTimestampFieldName(), getTableName());
		return impalaJdbcTemplate.queryForList(query, Long.class);
	}

	public double calculateAvgScoreOfGlobalScore(Date timestamp) {
		String query = String
				.format("select avg(tmp.g) from (select %s, max(%s) as g from %s where %s = %s group by %s) as tmp",
						getNormalizedUsernameField(), getGlobalScoreFieldName(),
						getTableName(), getTimestampFieldName(),
						formatTimestampDate(timestamp), getNormalizedUsernameField());

		return impalaJdbcTemplate.queryForObject(query, Double.class);
	}

	public List<T> getTopUsersAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(
				Direction.DESC, getGlobalScoreFieldName()));
		String query = String
				.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s > %d group by %s, %s, %s %s",
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), getEventScoreFieldName(),
						getEventScoreFieldName(), getTableName(),
						getTimestampFieldName(),
						formatTimestampDate(timestamp),
						getGlobalScoreFieldName(), threshold.getValue(),
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), pageable.toString());
		

		return getListGlobalResults(query);
	}

	public List<T> findByTimestampAndGlobalScoreBetweenSortByEventScore(
			Date timestamp, int lowestVal, int upperVal, int limit) {
		
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(
				Direction.DESC, getEventScoreFieldName()));
		String query = String
				.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s >= %d and %s < %d group by %s, %s, %s %s",
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), getEventScoreFieldName(),
						getEventScoreFieldName(), getTableName(),
						getTimestampFieldName(),
						formatTimestampDate(timestamp),
						getGlobalScoreFieldName(), lowestVal,
						getGlobalScoreFieldName(), upperVal,
						getTimestampFieldName(), getNormalizedUsernameField(),
						getGlobalScoreFieldName(), pageable.toString());
		

		return getListGlobalResults(query);
	}

	public List<T> getTopEventsAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(
				Direction.DESC, getTimestampFieldName()));
		String query = String.format(
				"select * from %s where %s=%s and %s > %d %s", getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), threshold.getValue(),
				pageable.toString());
		

		return getListResults(query);
	}

	class GlobalScoreMapper implements RowMapper<T> {
		
		private int numOfErrors = 0;

		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			T ret = null;

			try {
				ret = createAccessObject(rs.getString(getNormalizedUsernameField().toLowerCase()),
						Double.parseDouble(rs
								.getString(getGlobalScoreFieldName().toLowerCase())),
						Double.parseDouble(rs
								.getString(getEventScoreFieldName().toLowerCase())),
						parseTimestampDate(Long.parseLong(rs.getString(getTimestampFieldName().toLowerCase()))));
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
			String source = rs.getString(getSourceFieldName().toLowerCase());
			String destination = rs.getString(getDestinationFieldName().toLowerCase());
			String status = rs.getString(EVENT_LOGIN_DAY_COUNT_STATUS_FIELD_NAME);
			int score = rs.getInt(getEventScoreFieldName().toLowerCase());
			
			return new EventScore(getLogEventsEnum(), ts, source, destination, status, score); 
		}
	}
	
	class UsernameMapper implements RowMapper<T> {
		
		private int numOfErrors = 0;

		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			T ret = null;

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

	protected static Date parseTimestampDate(Long date) {
		return new Date(date * 1000);
	}

	protected static String formatTimestampDate(Date date) {
		return Long.toString(timestampDateToLong(date));
	}
	
	protected static long timestampDateToLong(Date date) {
		return date.getTime() / 1000;
	}

}
