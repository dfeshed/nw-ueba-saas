package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;
import static fortscale.utils.TimestampUtils.convertToSeconds;
import static fortscale.utils.impala.ImpalaCriteria.*;
import static fortscale.utils.impala.ImpalaCriteriaString.statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import fortscale.domain.schema.SSHEvents;
import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.impala.ImpalaQuery;

@Component
public class SSHConnectionsSource extends ConnectionsSource {

	@Value("${ssh.default_session_length_hours:10}")
	private int sessionLength;
	
	@Autowired
	private SSHEvents schema;
	
	@Override
	public String getSourceName() {
		return "ssh";
	}
	
	@Override
	protected String buildExpandQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select(schema.EPOCHTIME, schema.USERNAME, schema.SOURCE_IP, schema.TARGET_MACHINE, schema.STATUS, 
				schema.HOSTNAME, schema.getPartitionFieldName());
		query.from(schema.getTableName());
		
		// add criteria for machine to pivot on
		if (isSource)
			query.andWhere(statement(String.format("(%s='%s' OR lower(%s)=lower('%s'))", schema.SOURCE_IP, source, schema.HOSTNAME, source)));
		else
			query.andWhere(equalsTo(lower(schema.TARGET_MACHINE), source.toLowerCase(), true));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.andWhere(gte(schema.EPOCHTIME, Long.toString(convertToSeconds(filter.getStart()))));
			query.andWhere(gte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte("date_time_unix", Long.toString(convertToSeconds(filter.getEnd()))));
			query.andWhere(lte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getEnd())));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.andWhere(in(lower(schema.USERNAME), filter.getAccounts()));
			} else {
				query.andWhere(notIn(lower(schema.USERNAME), filter.getAccounts()));
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.andWhere(in(lower(schema.HOSTNAME), filter.getMachines()));
			} else {
				query.andWhere(notIn(lower(schema.HOSTNAME), filter.getMachines()));
			}
		}
		
		return query.toSQL();
	}
	
	protected RowMapper<Connection> getExpandQueryRowMapper() {
		return new RowMapper<Connection>() {
			@Override
			public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
				Connection connection = new Connection();
			
				// try and get the hostname, if it does not exist use the ip address instead 
				String hostname = rs.getString(schema.HOSTNAME.toLowerCase());
				if (hostname==null || hostname.isEmpty())
					connection.setSource(rs.getString(schema.SOURCE_IP.toLowerCase()));
				else
					connection.setSource(hostname);
				
				connection.setDestination(rs.getString(schema.TARGET_MACHINE.toLowerCase()));
				connection.setUserAccount(rs.getString(schema.USERNAME.toLowerCase()).toLowerCase());
				connection.setStart(new Date(convertToMilliSeconds(rs.getLong(schema.EPOCHTIME.toLowerCase()))));
				connection.setSourceType("ssh");
				// assume 10 hours session
				connection.setEnd(new Date(convertToMilliSeconds(rs.getLong(schema.EPOCHTIME.toLowerCase())) + (1000*60*60*sessionLength)));
				
				return connection;
			}
		};
	}
	
	protected String buildLookupQuery(String name, int start, int count) {
		
		return String.format("select name from (select distinct lower(%s) name from %s "
				+ "where lower(%s) like '%s' "
				+ "union select distinct lower(%s) name from %s "
				+ "where lower(%s) like '%s') nested order by name asc limit %s offset %s", 
				schema.TARGET_MACHINE, schema.getTableName(),schema.TARGET_MACHINE, name.toLowerCase(), 
				schema.HOSTNAME, schema.getTableName(), schema.HOSTNAME, name.toLowerCase(), count, start);
	}
	
}
