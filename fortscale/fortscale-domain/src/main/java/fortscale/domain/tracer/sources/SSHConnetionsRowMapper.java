package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToMilliSeconds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.tracer.Connection;

public class SSHConnetionsRowMapper implements RowMapper<Connection> {
	
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