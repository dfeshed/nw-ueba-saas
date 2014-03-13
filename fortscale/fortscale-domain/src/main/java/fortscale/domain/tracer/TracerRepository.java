package fortscale.domain.tracer;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

import fortscale.domain.tracer.sources.ConnectionsSource;
import fortscale.domain.tracer.sources.LDAPConnectionsSource;
import fortscale.domain.tracer.sources.SSHConnectionsSource;
import fortscale.domain.tracer.sources.VPNConnectionsSource;
import fortscale.utils.logging.Logger;

public class TracerRepository {

	private static final Logger logger = Logger.getLogger(TracerRepository.class);
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	private List<ConnectionsSource> sources;
		
	public List<Connection> expandConnections(String machine, boolean isSource, FilterSettings filter) {
		
		List<Connection> connections = new LinkedList<Connection>();
		
		fillConnectionsSources();
		for (ConnectionsSource source : sources) {
			if (shouldIncludeSource(filter, source.getSourceName())) {
				try {
					List<Connection> sourceConnections = source.getConnections(machine, isSource, filter); 
					connections.addAll(sourceConnections);
				} catch (Exception e) {
					logger.error("error getting hopping tracer connections from source: " + source.getSourceName(), e);
				}
			}
		}
		
		return connections;
	}
	
	private void fillConnectionsSources() {
		if (sources==null) {
			sources = new LinkedList<ConnectionsSource>();
			sources.add(new LDAPConnectionsSource(impalaJdbcTemplate));
			sources.add(new SSHConnectionsSource(impalaJdbcTemplate));
			sources.add(new VPNConnectionsSource(impalaJdbcTemplate));
		}
	}
	
	private boolean shouldIncludeSource(FilterSettings filter, String source) {
		if (filter.getSources().isEmpty())
			return true;
		
		if (filter.getSourcesListMode()==ListMode.Exclude)
			return !containsIgnoreCase(filter.getSources(), source);
		else
			return containsIgnoreCase(filter.getSources(), source);
	}

	private boolean containsIgnoreCase(List<String> sources, String source) {
		for (String item : sources) {
			if (item.equalsIgnoreCase(source))
				return true;
		}
		return true;
	}
	
}
