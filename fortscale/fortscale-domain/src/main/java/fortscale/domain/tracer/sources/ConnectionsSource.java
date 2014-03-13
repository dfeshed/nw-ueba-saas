package fortscale.domain.tracer.sources;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.utils.hdfs.partition.PartitionStrategy;

public abstract class ConnectionsSource implements RowMapper<Connection> {

	protected JdbcOperations impalaJdbcTemplate;
	protected PartitionStrategy partition;

	public ConnectionsSource(JdbcOperations impalaJdbcTemplate, PartitionStrategy partition) {
		checkNotNull(impalaJdbcTemplate);
		checkNotNull(partition);
		
		this.impalaJdbcTemplate = impalaJdbcTemplate;
		this.partition = partition;
	}
	
	public List<Connection> getConnections(String source, boolean isSource, FilterSettings filter) throws DataAccessException {
		
		// create sql query statement
		String query = buildQuery(source, isSource, filter);
		
		// execute the query
		return impalaJdbcTemplate.query(query, this);
	}

	public abstract String getSourceName();
	
	protected abstract String buildQuery(String source, boolean isSource, FilterSettings filter);
}
