package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.TimestampUtils.convertToSeconds;
import static fortscale.utils.impala.ImpalaCriteria.*;
import static fortscale.utils.impala.ImpalaCriteriaString.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.domain.schema.LDAPEvents;
import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.global.configuration.ServersListConfiguration;
import fortscale.utils.impala.*;

@Component
public class LDAPConnectionsSource extends ConnectionsSource {

	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	@Autowired
	private LDAPEvents schema;
	
	@Override
	public String getSourceName() {
		return "ldap";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();	
		query.select(schema.TIMEGENERATEDUNIXTIME, schema.ACCOUNT_NAME, schema.CLIENT_ADDRESS, schema.MACHINE_NAME, 
				schema.SERVICE_NAME, schema.getPartitionFieldName());
		query.from(schema.getTableName());
		
		// add criteria for machine to pivot on
		if (isSource)
			query.andWhere(statement(String.format("(%s='%s' OR %s='%s')", schema.CLIENT_ADDRESS, source, schema.MACHINE_NAME, source)));
		else
			query.andWhere(equalsTo(schema.SERVICE_NAME, source, true));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.andWhere(gte(schema.TIMEGENERATEDUNIXTIME, Long.toString(convertToSeconds(filter.getStart()))));
			query.andWhere(gte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte("timegeneratedunixtime", Long.toString(convertToSeconds(filter.getEnd()))));
			query.andWhere(lte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getEnd())));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.andWhere(in(lower(schema.ACCOUNT_NAME), filter.getAccounts()));
			} else {
				query.andWhere(notIn(lower(schema.ACCOUNT_NAME), filter.getAccounts()));
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.andWhere(in(lower(schema.MACHINE_NAME), filter.getMachines()));
				query.andWhere(in(lower(schema.SERVICE_NAME), filter.getMachines()));
			} else {
				query.andWhere(notIn(lower(schema.MACHINE_NAME), filter.getMachines()));
				query.andWhere(notIn(lower(schema.SERVICE_NAME), filter.getMachines()));
			}
		}
		
		// filter out source that equals to destinations
		query.andWhere(neq(lower(schema.MACHINE_NAME), "lower(service_name)"));
		
		// filter out domain controllers destinations
		String dcFilter = serversListConfiguration.getLoginServiceRegex();
		query.andWhere(statement(String.format("not (lower(service_name) regexp lower('%s'))", dcFilter)));
		
		return query.toSQL();
	}
	
	@Override
	public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
		Connection connection = new Connection();
	
		// try and get the hostname, if it does not exist use the ip address instead 
		String hostname = rs.getString(schema.MACHINE_NAME);
		if (hostname==null || hostname.isEmpty())
			connection.setSource(rs.getString(schema.CLIENT_ADDRESS));
		else
			connection.setSource(hostname);
		
		connection.setDestination(rs.getString(schema.SERVICE_NAME));
		connection.setUserAccount(rs.getString(schema.ACCOUNT_NAME).toLowerCase());
		connection.setStart(new Date(convertToMilliSeconds(rs.getLong(schema.TIMEGENERATEDUNIXTIME))));
		connection.setSourceType("ldap");
		// assume 10 hours session
		connection.setEnd(new Date(convertToMilliSeconds(rs.getLong(schema.TIMEGENERATEDUNIXTIME)) + (1000*60*60*10)));
		
		return connection;
	}
}
