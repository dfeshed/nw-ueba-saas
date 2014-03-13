package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.TimestampUtils.convertToSeconds;
import static fortscale.utils.impala.ImpalaCriteria.*;
import static fortscale.utils.impala.ImpalaCriteriaString.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.JdbcOperations;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.impala.ImpalaQuery;

public class SSHConnectionsSource extends ConnectionsSource {

	public SSHConnectionsSource(JdbcOperations impalaJdbcTemplate) {
		super(impalaJdbcTemplate, new MonthlyPartitionStrategy());
	}
	
	@Override
	public String getSourceName() {
		return "ssh";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select("date_time_unix, username, source_ip, target_machine, status, hostname, " + partition.getImpalaPartitionFieldName());
		query.from("sshdata");
		
		// add criteria for machine to pivot on
		query.andWhere(isSource? 
				statement(String.format("(source_ip='%s' OR hostname='%s')", source, source)) :
				equalsTo("target_machine", source));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.andWhere(gte("date_time_unix", Long.toString(convertToSeconds(filter.getStart()))));
			query.andWhere(gte(partition.getImpalaPartitionFieldName(), partition.getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte("date_time_unix", Long.toString(convertToSeconds(filter.getEnd()))));
			query.andWhere(lte(partition.getImpalaPartitionFieldName(), partition.getImpalaPartitionValue(filter.getEnd())));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.andWhere(in("lower(username)", filter.getAccounts()));
			} else {
				query.andWhere(notIn("lower(username)", filter.getAccounts()));
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.andWhere(in("lower(hostname)", filter.getMachines()));
			} else {
				query.andWhere(notIn("lower(hostname)", filter.getMachines()));
			}
		}
		
		return query.toString();
	}
	
	@Override
	public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
		Connection connection = new Connection();

		// date_time_unix, username, source_ip, target_machine, status, hostname, yearmonth
		
		// try and get the hostname, if it does not exist use the ip address instead 
		String hostname = rs.getString("hostname");
		if (hostname==null || hostname.isEmpty())
			connection.setSource(rs.getString("source_ip"));
		else
			connection.setSource(hostname);
		
		connection.setDestination(rs.getString("target_machine"));
		connection.setUserAccount(rs.getString("username").toLowerCase());
		connection.setStart(new Date(convertToMilliSeconds(rs.getLong("date_time_unix"))));
		connection.setSourceType("ssh");
		// assume 10 hours session
		connection.setEnd(new Date(convertToMilliSeconds(rs.getLong("date_time_unix")) + (1000*60*60*10)));
		
		return connection;
	}
	
}
