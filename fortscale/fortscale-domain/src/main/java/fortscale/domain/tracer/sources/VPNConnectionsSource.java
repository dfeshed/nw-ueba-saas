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
import org.springframework.stereotype.Component;

import fortscale.domain.schema.VpnEvents;
import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.impala.ImpalaQuery;

@Component
public class VPNConnectionsSource extends ConnectionsSource {

	@Value("${vpn.default_session_length_hours:10}")
	private int sessionLength;
	
	@Autowired
	private VpnEvents schema;
	
	@Override
	public String getSourceName() {
		return "vpn";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		
		ImpalaQuery query = new ImpalaQuery();
		query.select(schema.DATE_TIME_UNIX, schema.USERNAME, schema.SOURCE_IP, schema.LOCAL_IP, schema.STATUS, 
				schema.COUNTRY, schema.HOSTNAME, schema.getPartitionFieldName());
		query.from(schema.getTableName());
		
		// add criteria for machine to pivot on
		if (isSource)
			query.andWhere(statement(String.format("(%s='%s' OR %s='%s')", schema.SOURCE_IP, source, schema.HOSTNAME, source)));
		else
			query.andWhere(equalsTo(schema.LOCAL_IP, source, true));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.andWhere(gte(schema.DATE_TIME_UNIX, Long.toString(convertToSeconds(filter.getStart()))));
			query.andWhere(gte(schema.getPartitionFieldName(), schema.getPartitionStrategy().getImpalaPartitionValue(filter.getStart())));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.andWhere(lte(schema.DATE_TIME_UNIX, Long.toString(convertToSeconds(filter.getEnd()))));
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
				query.andWhere(in(lower(schema.USERNAME), filter.getMachines()));
			} else {
				query.andWhere(notIn(lower(schema.USERNAME), filter.getMachines()));
			}
		}
		
		return query.toSQL();
	}
	
	@Override
	public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
		Connection connection = new Connection();

		// try and get the hostname, if it does not exist use the ip address instead 
		String hostname = rs.getString(schema.HOSTNAME);
		if (hostname==null || hostname.isEmpty())
			connection.setSource(rs.getString(schema.SOURCE_IP));
		else
			connection.setSource(hostname);
		
		connection.setDestination(rs.getString(schema.LOCAL_IP));
		connection.setUserAccount(rs.getString(schema.USERNAME).toLowerCase());
		connection.setStart(new Date(convertToMilliSeconds(rs.getLong(schema.DATE_TIME_UNIX))));
		connection.setSourceType("vpn");
		// assume 10 hours session
		connection.setEnd(new Date(convertToMilliSeconds(rs.getLong(schema.DATE_TIME_UNIX)) + (1000*60*60*sessionLength)));
		
		return connection;
	}
	
}
