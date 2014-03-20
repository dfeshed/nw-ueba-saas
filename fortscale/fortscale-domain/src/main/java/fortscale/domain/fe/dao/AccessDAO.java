package fortscale.domain.fe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.impala.ImpalaDAO;
import fortscale.utils.impala.ImpalaCriteria;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;
import fortscale.utils.logging.Logger;

public abstract class AccessDAO<T> extends ImpalaDAO<T> {
	private static Logger logger = Logger.getLogger(AccessDAO.class);

	private Date lastRunDate = null;

	public abstract RowMapper<T> getMapper();

	public abstract String getTimestampFieldName();
	
	public abstract String getNormalizedUsernameField();

	public abstract String getUsernameFieldName();

	public abstract String getEventScoreFieldName();

	public abstract String getGlobalScoreFieldName();
	
	public abstract String getStatusFieldName();

	public abstract T createAccessObject(String normalizedUsername, double globalScore,	double eventScore, Date timestamp);
	public abstract T createAccessObject(String normalizedUsername, String username);

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
	
	public List<T> findEventsByTimestampGtEventScoreInUsernameList(Date timestamp, Pageable pageable, Integer minScore, Collection<String> usernames) {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		if(minScore != null){
			builder.append(String.format("%s >= %d", getEventScoreFieldName(), minScore));
			isFirst = false;
		}
		if(usernames != null && !usernames.isEmpty()){
			if(!isFirst){
				builder.append(" and ");
			} else{
				isFirst = false;
			}
			builder.append(getUsernameFieldName()).append(" in (");
			boolean isFirstUsername = true;
			for(String username: usernames){
				if(isFirstUsername){
					isFirstUsername = false;
				} else{
					builder.append(",");
				}
				builder.append("\"").append(username).append("\"");
			}
			builder.append(")");
		}
		String additionalWhereQuery = null;
		if(!isFirst){
			additionalWhereQuery = builder.toString();
		}
		return findEventsByTimestamp(timestamp, pageable, additionalWhereQuery);
	}

	public List<T> findEventsByTimestamp(Date timestamp, Pageable pageable,
			String additionalWhereQuery) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select("*")
				.from(getTableName())
				.where(ImpalaCriteria.equalsTo(getTimestampFieldName(),
						formatTimestampDate(timestamp)));
		if (additionalWhereQuery != null && additionalWhereQuery.length() > 0) {
			query.andWhere(additionalWhereQuery);
		}
		query.limitAndSort(pageable);

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
			String message;
			if(countNumOfRecords() > 0){
				message = String.format("run time in the table (%s) is null", getTableName());
			} else{
				message = String.format("the table (%s) is empty", getTableName());
			}
			throw new RuntimeException(message);
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
		return Long.toString(date.getTime() / 1000);

	}

}
