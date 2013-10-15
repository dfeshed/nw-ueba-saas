package fortscale.domain.fe.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.impala.ImpalaDAO;
import fortscale.utils.impala.ImpalaCriteria;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;

public abstract class AccessDAO<T>  extends ImpalaDAO<T>{
	
	private Date lastRunDate = null;
	
	public abstract RowMapper<T> getMapper();
	
	public abstract String getTimestampFieldName();
	
	public abstract String getUsernameFieldName();
	
	public abstract String getEventScoreFieldName();
	
	public abstract String getGlobalScoreFieldName();
	
	public abstract T createAccessObject(String userName, double globalScore, double eventScore, Date timestamp);	
	
	
	public List<T> findAll(Pageable pageable){
		return super.findAll(pageable, getMapper());
	}
	
	public T findCurrentByUsername(String username){
		T ret = null;
		Pageable pageable = new ImpalaPageRequest(1, new Sort(Direction.DESC, getTimestampFieldName()));
		List<T> scoreList = findEventsByUsername(username, pageable);
		if(scoreList.size() > 0){
			ret = scoreList.get(0);
		}
		
		return ret;
	}

	public List<T> findEventsByUsername(String username, Pageable pageable){
		List<T> ret = new ArrayList<>();
		String query = String.format("select * from %s where %s", getTableName(), getUserNameEqualComparison(username));
		if(pageable != null){
			query = String.format("%s %s",query, pageable.toString());
		}
		ret.addAll(impalaJdbcTemplate.query(query,getMapper()));
		
		return ret;
	}

	public List<T> findEventsByUsernameAndTimestamp(String username, Date timestamp, Pageable pageable){
		List<T> ret = new ArrayList<>();
		String query = String.format("select * from %s where %s=%s and %s %s",
				getTableName(), 
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getUserNameEqualComparison(username),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query,getMapper()));
		
		return ret;
	}

	public List<T> findEventsByTimestamp(Date timestamp, Pageable pageable){
		return findEventsByTimestamp(timestamp, pageable, null);
	}

	public List<T> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery){
		List<T> ret = new ArrayList<>();
		ImpalaQuery query = new ImpalaQuery();
		query.select("*").from(getTableName()).where(ImpalaCriteria.equalsTo(getTimestampFieldName(), formatTimestampDate(timestamp)));
		if(additionalWhereQuery != null && additionalWhereQuery.length() > 0){
			query.andWhere(additionalWhereQuery);
		}
		query.limitAndSort(pageable);
		
		ret.addAll(impalaJdbcTemplate.query(query.toSQL(),getMapper()));
		
		return ret;
	}

	public List<T> findGlobalScoreByUsername(String username, int limit){
		List<T> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, getTimestampFieldName()));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s group by %s, %s, %s %s", 
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(), getEventScoreFieldName(), getEventScoreFieldName(),
				getTableName(),
				getUserNameEqualComparison(username),
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}

	public List<T> findGlobalScoreByTimestamp(Date timestamp){
		List<T> ret = new ArrayList<>();
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s group by %s, %s, %s", 
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(), getEventScoreFieldName(), getEventScoreFieldName(),
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}


	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp){
		String query = String.format("select count(distinct(%s)) from %s where %s=%s and %s > %d",
				getUsernameFieldName(),
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), threshold.getValue());
		
		return impalaJdbcTemplate.queryForInt(query);
	}


	public Date getLastRunDate(){
		if(lastRunDate == null) {
			Calendar tmp = Calendar.getInstance();
			tmp.add(Calendar.DAY_OF_MONTH,-1);
			lastRunDate = new Date(tmp.getTimeInMillis());
		}
		String query = String.format("select max(%s) from %s", getTimestampFieldName(), getTableName());
		String queryWithHint = String.format("%s where %s >= %d", query, getTimestampFieldName(), lastRunDate.getTime()/1000);
		Long lastRun = impalaJdbcTemplate.queryForObject(queryWithHint, Long.class);
		if(lastRun == null) {
			lastRun = impalaJdbcTemplate.queryForObject(query, Long.class);
		}
		Date retDate = parseTimestampDate(lastRun);
		lastRunDate = retDate;
		return retDate;
	}


	public double calculateAvgScoreOfGlobalScore(Date timestamp){
		String query = String.format("select avg(tmp.g) from (select %s, max(%s) as g from %s where %s = %s group by %s) as tmp",
				getUsernameFieldName(), getGlobalScoreFieldName(),
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getUsernameFieldName());
		
		return impalaJdbcTemplate.queryForObject(query, Double.class);
	}


	public List<T> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit){
		List<T> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, getGlobalScoreFieldName()));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s > %d group by %s, %s, %s %s", 
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(), getEventScoreFieldName(), getEventScoreFieldName(),
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), threshold.getValue(),
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}


	public List<T> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit){
		List<T> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, getEventScoreFieldName()));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s >= %d and %s < %d group by %s, %s, %s %s", 
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(), getEventScoreFieldName(), getEventScoreFieldName(),
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), lowestVal,
				getGlobalScoreFieldName(), upperVal,
				getTimestampFieldName(), getUsernameFieldName(), getGlobalScoreFieldName(),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}


	public List<T> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit){
		List<T> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, getTimestampFieldName()));
		String query = String.format("select * from %s where %s=%s and %s > %d %s", 
				getTableName(),
				getTimestampFieldName(), formatTimestampDate(timestamp),
				getGlobalScoreFieldName(), threshold.getValue(),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query,getMapper()));
		
		return ret;
	}
	
	class GlobalScoreMapper implements RowMapper<T>{

		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {
			T ret = null;
			
			try{
				ret = createAccessObject(rs.getString(getUsernameFieldName()), 
						Double.parseDouble(rs.getString(getGlobalScoreFieldName())),
						Double.parseDouble(rs.getString(getEventScoreFieldName())),
						parseTimestampDate(rs.getLong(getTimestampFieldName())));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			}
			
			return ret;
		}
	}
	
	private String getUserNameEqualComparison(String username){
		return String.format("lower(%s) = \"%s\"", getUsernameFieldName(), username.toLowerCase());
//		return String.format("%s rlike \"(?i)%s\"", getUsernameFieldName(), username);
	}
	
	protected static Date parseTimestampDate(Long date){
		return new Date(date*1000);
	}
	
	protected static String formatTimestampDate(Date date){
		return Long.toString(date.getTime()/1000);
		
	}
	
}
