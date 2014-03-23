package fortscale.domain.tracer.sources;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;

public abstract class ConnectionsSource implements RowMapper<Connection> {

	private static Logger logger = LoggerFactory.getLogger(ConnectionsSource.class);
	
	@Autowired
	protected JdbcOperations impalaJdbcTemplate;
	
	public List<Connection> getConnections(String source, boolean isSource, FilterSettings filter) throws DataAccessException {
		
		// create sql query statement
		String query = buildQuery(source, isSource, filter);
		logger.debug("executing tracer query: " + query);		
		
		// execute the query
		return impalaJdbcTemplate.query(query, this);
	}

	public abstract String getSourceName();
	
	protected abstract String buildQuery(String source, boolean isSource, FilterSettings filter);
}
