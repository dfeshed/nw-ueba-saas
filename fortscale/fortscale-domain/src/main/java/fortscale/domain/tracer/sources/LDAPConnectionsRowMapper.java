package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.tracer.Connection;

public class LDAPConnectionsRowMapper implements RowMapper<Connection> {
	
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
