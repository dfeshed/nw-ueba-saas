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
import fortscale.utils.impala.*;

public class LDAPConnectionsSource extends ConnectionsSource {

	public LDAPConnectionsSource(JdbcOperations impalaJdbcTemplate) {
		super(impalaJdbcTemplate, new MonthlyPartitionStrategy());
	}
	
	@Override
	public String getSourceName() {
		return "ldap";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();	
		query.select("timegeneratedunixtime, account_name, client_address, machine_name, service_name, " + partition.getImpalaPartitionFieldName());
		query.from("wmievents4769");
		
		// add criteria for machine to pivot on
		query.andWhere(isSource? 
				statement(String.format("(client_address='%s' OR machine_name='%s')", source, source)) :
				equalsTo("service_name", source));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.andWhere(gte("timegeneratedunixtime", Long.toString(convertToSeconds(filter.getStart()))));
			query.andWhere(gte(partition.getImpalaPartitionFieldName(), partition.getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte("timegeneratedunixtime", Long.toString(convertToSeconds(filter.getEnd()))));
			query.andWhere(lte(partition.getImpalaPartitionFieldName(), partition.getImpalaPartitionValue(filter.getEnd())));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.andWhere(in("lower(account_name)", filter.getAccounts()));
			} else {
				query.andWhere(notIn("lower(account_name)", filter.getAccounts()));
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.andWhere(in("lower(machine_name)", filter.getMachines()));
				query.andWhere(in("lower(service_name)", filter.getMachines()));
			} else {
				query.andWhere(notIn("lower(machine_name)", filter.getMachines()));
				query.andWhere(notIn("lower(service_name)", filter.getMachines()));
			}
		}
		
		return query.toSQL();
	}
	
	@Override
	public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
		Connection connection = new Connection();

		// timegeneratedunixtime, account_name, client_address, machine_name, service_name, yearmonth
		
		// try and get the hostname, if it does not exist use the ip address instead 
		String hostname = rs.getString("machine_name");
		if (hostname==null || hostname.isEmpty())
			connection.setSource(rs.getString("client_address"));
		else
			connection.setSource(hostname);
		
		connection.setDestination(rs.getString("service_name"));
		connection.setUserAccount(rs.getString("account_name").toLowerCase());
		connection.setStart(new Date(convertToMilliSeconds(rs.getLong("timegeneratedunixtime"))));
		connection.setSourceType("ldap");
		// assume 10 hours session
		connection.setEnd(new Date(convertToMilliSeconds(rs.getLong("timegeneratedunixtime")) + (1000*60*60*10)));
		
		return connection;
	}
}
