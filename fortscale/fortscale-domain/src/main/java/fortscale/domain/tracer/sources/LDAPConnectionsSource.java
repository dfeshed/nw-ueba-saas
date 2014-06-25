package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.TimestampUtils.convertToSeconds;
import static fortscale.utils.impala.ImpalaCriteria.equalsTo;
import static fortscale.utils.impala.ImpalaCriteria.gte;
import static fortscale.utils.impala.ImpalaCriteria.in;
import static fortscale.utils.impala.ImpalaCriteria.lower;
import static fortscale.utils.impala.ImpalaCriteria.lte;
import static fortscale.utils.impala.ImpalaCriteria.neq;
import static fortscale.utils.impala.ImpalaCriteria.notIn;
import static fortscale.utils.impala.ImpalaCriteriaString.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import fortscale.domain.fe.dao.impl.LoginDAOImpl;
import fortscale.domain.schema.LDAPEvents;
import fortscale.domain.system.ServersListConfiguration;
import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.impala.ImpalaQuery;

@Component
public class LDAPConnectionsSource extends ConnectionsSource {

	@Value("${ldap.default_session_length_hours:10}")
	private int sessionLength;
	
	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	@Autowired
	private LoginDAOImpl schema;
	
	@Override
	public String getSourceName() {
		return "ldap";
	}
	
	@Override
	protected String buildExpandQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();	
		query.select(schema.TIMEGENERATED, schema.ACCOUNT_NAME, schema.CLIENT_ADDRESS, schema.MACHINE_NAME, 
				schema.SERVICE_NAME, schema.getPartitionStrategy().getImpalaPartitionFieldName());
		query.from(schema.getTableName());
		query.andEq(schema.FAILURE_CODE, "'0x0'");
		
		// add criteria for machine to pivot on
		if (isSource)
			query.andWhere(statement(String.format("(%s='%s' OR lower(%s)=lower('%s'))", schema.CLIENT_ADDRESS, source, schema.MACHINE_NAME, source)));
		else
			query.andWhere(equalsTo(lower(schema.SERVICE_NAME), source.toLowerCase(), true));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			// assuming ldap session is 10 hours, look for all events that their probable
			// end time is after the start date
			long timeBoundry = convertToSeconds(filter.getStart()) - (60*60*sessionLength);
			query.andWhere(gte(schema.TIMEGENERATED, Long.toString(timeBoundry)));
			query.andWhere(gte(schema.getPartitionStrategy().getImpalaPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte("timegeneratedunixtime", Long.toString(convertToSeconds(filter.getEnd()))));
			query.andWhere(lte(schema.getPartitionStrategy().getImpalaPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getEnd())));
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
		String dcFilter = serversListConfiguration.getLoginServiceRegex().toLowerCase();
		query.andWhere(statement(String.format("not (lower(service_name) regexp '%s')", dcFilter)));
		
		return query.toSQL();
	}
	
	protected RowMapper<Connection> getExpandQueryRowMapper() {
		return new RowMapper<Connection>() {
			@Override
			public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
				Connection connection = new Connection();
			
				// try and get the hostname, if it does not exist use the ip address instead 
				String hostname = rs.getString(schema.MACHINE_NAME.toLowerCase());
				if (hostname==null || hostname.isEmpty())
					connection.setSource(rs.getString(schema.CLIENT_ADDRESS.toLowerCase()));
				else
					connection.setSource(hostname);
				
				connection.setDestination(rs.getString(schema.SERVICE_NAME.toLowerCase()));
				connection.setUserAccount(rs.getString(schema.ACCOUNT_NAME.toLowerCase()).toLowerCase());
				
				connection.setStart(new Date(convertToMilliSeconds(rs.getLong(schema.TIMEGENERATED.toLowerCase()))));
				connection.setSourceType("ldap");
				// assume 10 hours session
				connection.setEnd(new Date(convertToMilliSeconds(rs.getLong(schema.TIMEGENERATED.toLowerCase())) + (1000*60*60*sessionLength)));
				
				return connection;
			}
		};
	}

	protected String buildLookupQuery(String name, int count) {
		
		return String.format("select name from (select distinct lower(%s) name from %s "
				+ "where lower(%s) like '%s' "
				+ "union select distinct lower(%s) name from %s "
				+ "where lower(%s) like '%s') nested order by name asc limit %s", 
				schema.SERVICE_NAME, schema.getTableName(),schema.SERVICE_NAME, name.toLowerCase(), 
				schema.MACHINE_NAME, schema.getTableName(), schema.MACHINE_NAME, name.toLowerCase(), count);
	}
}
