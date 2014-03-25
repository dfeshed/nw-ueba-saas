package fortscale.domain.tracer.sources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;

public abstract class ConnectionsSource {

	private static Logger logger = LoggerFactory.getLogger(ConnectionsSource.class);
	
	@Autowired
	protected JdbcOperations impalaJdbcTemplate;
	
	public List<Connection> getConnections(String source, boolean isSource, FilterSettings filter) throws DataAccessException {
		// create sql query statement
		String query = buildExpandQuery(source, isSource, filter);
		logger.debug("executing tracer query: " + query);		
		
		// execute the query
		return impalaJdbcTemplate.query(query, getExpandQueryRowMapper());
	}

	public List<String> lookupMachines(String name, int count) {
		// add wildcard to name if not exist
		name = (name.contains("%"))? name : name + "%";
		
		// create sql query statement
		String query = buildLookupQuery(name, count);
		logger.debug("executing tracer name lookup query: " + query);		
		
		// execute the query
		return impalaJdbcTemplate.query(query, getLookupQueryRowMapper());
	}
	
	public abstract String getSourceName();
	
	
	protected abstract String buildExpandQuery(String source, boolean isSource, FilterSettings filter);
	protected abstract RowMapper<Connection> getExpandQueryRowMapper();
	protected abstract String buildLookupQuery(String name, int count);

	protected RowMapper<String> getLookupQueryRowMapper() {
		return new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("name");
			}
		};
	}
	
}
