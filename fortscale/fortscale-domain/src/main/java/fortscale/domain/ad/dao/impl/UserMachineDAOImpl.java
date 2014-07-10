package fortscale.domain.ad.dao.impl;

import static fortscale.utils.impala.ImpalaCriteria.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.schema.LoginEvents;
import fortscale.utils.TimestampUtils;
import fortscale.utils.impala.ImpalaQuery;

public class UserMachineDAOImpl implements UserMachineDAO, RowMapper<UserMachine> {
	
	private static final Logger logger = LoggerFactory.getLogger(UserMachineDAOImpl.class);
	
	private static final int DAYS_TO_CONSIDER = 14;
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	@Autowired
	private LoginEvents schema;
		
	@Override
	public List<UserMachine> findByUsername(String username){
		try {
			// create a query to fetch all successful login events for a user in the last 14 days where the machine was not over NAT
			ImpalaQuery query = new ImpalaQuery();
			query.select(schema.NORMALIZED_USERNAME, lower(schema.MACHINE_NAME) + " as machine", "count(*) as login_count",  
					String.format("max(%s) as last_login", schema.TIMEGENERATEDUNIXTIME));
			query.from(schema.getTableName());
			
			// filter login events for the requested user
			query.where(equalsTo(lower(schema.NORMALIZED_USERNAME), lower(quote(username))));
			
			// filter events in the last X days
			long since = TimestampUtils.convertToSeconds(DateTime.now().minusDays(DAYS_TO_CONSIDER).getMillis());
			query.where(gte(schema.TIMEGENERATEDUNIXTIME, Long.toString(since)));
			
			// filter partitions
			query.where(gte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(since)));
			
			// filter on success status
			query.where(equalsTo(schema.STATUS, "SUCCESS", true));
			
			// filter out nat addresses
			query.where(equalsTo(schema.IS_NAT, "false"));
			
			// filter out empty hostnames
			query.where(neq(schema.MACHINE_NAME, "''"));
			
			// add group by clause
			query.groupBy(schema.NORMALIZED_USERNAME, lower(schema.MACHINE_NAME));

			return impalaJdbcTemplate.query(query.toSQL(), this);
		} catch (UncategorizedSQLException e) {
			logger.error("error finding host logins for username " + username, e);
			return new LinkedList<UserMachine>();
		}
	}
	
	@Override
	public List<UserMachine> findByHostname(String hostname, int daysToConsider) {
		try {
			// create a query to fetch all successful login events for the machine in the last days
			ImpalaQuery query = new ImpalaQuery();
			query.select(schema.NORMALIZED_USERNAME, String.format("'%s' as machine", hostname), "count(*) as login_count",  
					String.format("max(%s) as last_login", schema.TIMEGENERATEDUNIXTIME));
			query.from(schema.getTableName());
			
			// filter login events for the requested computer
			query.where(equalsTo(lower(schema.MACHINE_NAME), lower(quote(hostname))));
			
			// filter events in the last x days
			long since = TimestampUtils.convertToSeconds(DateTime.now().minusDays(daysToConsider).getMillis());
			query.where(gte(schema.TIMEGENERATEDUNIXTIME, Long.toString(since)));
	
			// filter partitions
			query.where(gte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(since)));
			
			// filter on success status
	        query.where(equalsTo(schema.STATUS, "SUCCESS", true));
	
			// add group by clause
			query.groupBy(schema.NORMALIZED_USERNAME);
			
			return impalaJdbcTemplate.query(query.toSQL(), this);
		} catch (UncategorizedSQLException e) {
			// check if the query failed on time out, if it is then try 
			// to run it on half the time period
			if (e.getSQLException().getMessage().contains("exceeded timeout") && daysToConsider > 7) {
				logger.error("timeout while trying to get user login to hostname in period of " + daysToConsider + 
						" trying to query on half on period", e);
				return findByHostname(hostname, daysToConsider / 2);
			} else {
				logger.error("error getting user login to hostname " + hostname, e);
				return new LinkedList<UserMachine>();
			}
		}
	}

	
	@Override
	public UserMachine mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserMachine userMachine = new UserMachine();
		try{
			userMachine.setUsername(rs.getString(schema.NORMALIZED_USERNAME.toLowerCase()));
			userMachine.setHostname(rs.getString("machine"));
			userMachine.setLogonCount(rs.getInt("login_count"));
			userMachine.setLastlogon(rs.getLong("last_login"));
		} catch (Exception e) {
			logger.error("error converting result set record to usermachine", e);
		}
		
		return userMachine;
	}
		
}

