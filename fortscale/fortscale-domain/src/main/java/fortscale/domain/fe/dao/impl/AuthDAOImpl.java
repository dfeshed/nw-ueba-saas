package fortscale.domain.fe.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.utils.impala.ImpalaCriteria;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaQuery;

public class AuthDAOImpl implements AuthDAO{
public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss" ;
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	@Override
	public AuthScore findCurrentAuthScoreByUsername(String username){
		AuthScore ret = null;
		Pageable pageable = new ImpalaPageRequest(1, new Sort(Direction.DESC, AuthScore.TIMESTAMP_FIELD_NAME));
		List<AuthScore> authScores = findEventsByUsername(username, pageable);
		if(authScores.size() > 0){
			ret = authScores.get(0);
		}
		
		return ret;
	}
	@Override
	public List<AuthScore> findEventsByUsername(String username, Pageable pageable){
		List<AuthScore> ret = new ArrayList<>();
		String query = String.format("select * from %s where %s", AuthScore.TABLE_NAME, getUserNameEqualComparison(username));
		if(pageable != null){
			query = String.format("%s %s",query, pageable.toString());
		}
		ret.addAll(impalaJdbcTemplate.query(query, new AuthScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> findEventsByUsernameAndTimestamp(String username, Date timestamp, Pageable pageable){
		List<AuthScore> ret = new ArrayList<>();
		String query = String.format("select * from %s where %s=%s and %s %s",
				AuthScore.TABLE_NAME, 
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				getUserNameEqualComparison(username),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new AuthScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable){
		return findEventsByTimestamp(timestamp, pageable, null);
	}
	@Override
	public List<AuthScore> findEventsByTimestamp(Date timestamp, Pageable pageable, String additionalWhereQuery){
		List<AuthScore> ret = new ArrayList<>();
		ImpalaQuery query = new ImpalaQuery();
		query.select("*").from(AuthScore.TABLE_NAME).where(ImpalaCriteria.equalsTo(AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp)));
		if(additionalWhereQuery != null && additionalWhereQuery.length() > 0){
			query.andWhere(additionalWhereQuery);
		}
		query.limitAndSort(pageable);
		
		ret.addAll(impalaJdbcTemplate.query(query.toSQL(), new AuthScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> findGlobalScoreByUsername(String username, int limit){
		List<AuthScore> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, AuthScore.TIMESTAMP_FIELD_NAME));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s group by %s, %s, %s %s", 
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME,
				AuthScore.TABLE_NAME,
				getUserNameEqualComparison(username),
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME,
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> findGlobalScoreByTimestamp(Date timestamp){
		List<AuthScore> ret = new ArrayList<>();
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s group by %s, %s, %s", 
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME,
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME);
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}
	@Override
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp){
		String query = String.format("select count(distinct(%s)) from %s where %s=%s and %s > %d",
				AuthScore.USERNAME_FIELD_NAME,
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.GLOBAL_SCORE_FIELD_NAME, threshold.getValue());
		
		return impalaJdbcTemplate.queryForInt(query);
	}
	@Override
	public Date getLastRunDate(){
		Calendar tmp = Calendar.getInstance();
		tmp.add(Calendar.DAY_OF_MONTH,-1);
		String query = String.format("select max(%s) from %s", AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.TABLE_NAME);
		String queryWithHint = String.format("%s where %s > %d", query, AuthScore.TIMESTAMP_FIELD_NAME, tmp.getTimeInMillis()/1000);
		Long lastRun = impalaJdbcTemplate.queryForObject(queryWithHint, Long.class);
		if(lastRun == null) {
			lastRun = impalaJdbcTemplate.queryForObject(query, Long.class);
		}
		return parseTimestampDate(lastRun);
	}
	@Override
	public double calculateAvgScoreOfGlobalScore(Date timestamp){
		String query = String.format("select avg(tmp.g) from (select %s, max(%s) as g from %s where %s = %s group by %s) as tmp",
				AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME,
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.USERNAME_FIELD_NAME);
		
		return impalaJdbcTemplate.queryForObject(query, Double.class);
	}
	@Override
	public List<AuthScore> getTopUsersAboveThreshold(Threshold threshold, Date timestamp, int limit){
		List<AuthScore> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, AuthScore.GLOBAL_SCORE_FIELD_NAME));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s > %d group by %s, %s, %s %s", 
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME,
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.GLOBAL_SCORE_FIELD_NAME, threshold.getValue(),
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME,
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(Date timestamp, int lowestVal, int upperVal, int limit){
		List<AuthScore> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		String query = String.format("select %s, %s, %s, max(%s) as %s from %s where %s=%s and %s >= %d and %s < %d group by %s, %s, %s %s", 
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME, AuthScore.EVENT_SCORE_FIELD_NAME,
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.GLOBAL_SCORE_FIELD_NAME, lowestVal,
				AuthScore.GLOBAL_SCORE_FIELD_NAME, upperVal,
				AuthScore.TIMESTAMP_FIELD_NAME, AuthScore.USERNAME_FIELD_NAME, AuthScore.GLOBAL_SCORE_FIELD_NAME,
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new GlobalScoreMapper()));
		
		return ret;
	}
	@Override
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold, Date timestamp, int limit){
		List<AuthScore> ret = new ArrayList<>();
		Pageable pageable = new ImpalaPageRequest(limit, new Sort(Direction.DESC, AuthScore.TIMESTAMP_FIELD_NAME));
		String query = String.format("select * from %s where %s=%s and %s > %d %s", 
				AuthScore.TABLE_NAME,
				AuthScore.TIMESTAMP_FIELD_NAME, formatTimestampDate(timestamp),
				AuthScore.GLOBAL_SCORE_FIELD_NAME, threshold.getValue(),
				pageable.toString());
		ret.addAll(impalaJdbcTemplate.query(query, new AuthScoreMapper()));
		
		return ret;
	}
	
	class AuthScoreMapper implements RowMapper<AuthScore>{

		@Override
		public AuthScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthScore ret = new AuthScore();
			
			try{
				ret.setUserName(rs.getString(AuthScore.USERNAME_FIELD_NAME));
				ret.setTargetId(rs.getString(AuthScore.TARGET_ID_FIELD_NAME));
				ret.setSourceIp(rs.getString(AuthScore.SOURCE_IP_FIELD_NAME));
				ret.setErrorCode(rs.getString(AuthScore.ERROR_CODE_FIELD_NAME));
				ret.setEventScore(Double.parseDouble(rs.getString(AuthScore.EVENT_SCORE_FIELD_NAME)));
				ret.setGlobalScore(Double.parseDouble(rs.getString(AuthScore.GLOBAL_SCORE_FIELD_NAME)));
				ret.setTimestamp(parseTimestampDate(rs.getLong(AuthScore.TIMESTAMP_FIELD_NAME)));
				ret.setEventTime(parseEventTimeDate(rs.getString(AuthScore.EVENT_TIME_FIELD_NAME)));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			} catch (ParseException e) {
				throw new SQLException(e);
			}
			
			return ret;
		}
	}
	
	class GlobalScoreMapper implements RowMapper<AuthScore>{

		@Override
		public AuthScore mapRow(ResultSet rs, int rowNum) throws SQLException {
			AuthScore ret = new AuthScore();
			
			try{
				ret.setUserName(rs.getString(AuthScore.USERNAME_FIELD_NAME));
				ret.setEventScore(Double.parseDouble(rs.getString(AuthScore.EVENT_SCORE_FIELD_NAME)));
				ret.setGlobalScore(Double.parseDouble(rs.getString(AuthScore.GLOBAL_SCORE_FIELD_NAME)));
				ret.setTimestamp(parseTimestampDate(rs.getLong(AuthScore.TIMESTAMP_FIELD_NAME)));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			}
			
			return ret;
		}
	}
	
	private String getUserNameEqualComparison(String username){
		return String.format("lower(%s) = \"%s\"", AuthScore.USERNAME_FIELD_NAME, username);
//		return String.format("%s rlike \"(?i)%s\"", AuthScore.USERNAME_FIELD_NAME, username);
	}
	
	private Date parseTimestampDate(Long date){
		return new Date(date*1000);
	}
	
	private String formatTimestampDate(Date date){
		return Long.toString(date.getTime()/1000);
		
	}
	
	private Date parseEventTimeDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
//	private String formatEventTimeDate(Date date){
//		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
//	return pattern.format(date);
//	}

}
