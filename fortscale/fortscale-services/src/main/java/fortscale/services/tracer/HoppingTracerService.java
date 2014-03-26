package fortscale.services.tracer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.domain.tracer.sources.ConnectionsSource;
import fortscale.domain.tracer.sources.LDAPConnectionsSource;
import fortscale.domain.tracer.sources.SSHConnectionsSource;
import fortscale.domain.tracer.sources.VPNConnectionsSource;
import fortscale.global.configuration.ServersListConfiguration;
import fortscale.utils.logging.Logger;

@Service("hoppingTracerService")
public class HoppingTracerService implements InitializingBean {

	private static final Logger logger = Logger.getLogger(HoppingTracerService.class);

	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	@Autowired
	private LDAPConnectionsSource ldapSource;
	@Autowired
	private SSHConnectionsSource sshSource;
	@Autowired
	private VPNConnectionsSource vpnSource;
	
	private List<ConnectionsSource> sources;

	
	@Override
	public void afterPropertiesSet() throws Exception {
		sources = new LinkedList<ConnectionsSource>();
		sources.add(ldapSource);
		sources.add(sshSource);
		sources.add(vpnSource);
	}
	
	public List<Connection> expandConnections(String machine, boolean isSource, FilterSettings filter) {
		
		List<Connection> connections = new LinkedList<Connection>();
		
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
	
	public List<String> lookupMachines(String name, int count) {
		
		// get the first matching names from each source, then sort them 
		// and return the first count from the merged collection
		SortedSet<String> names = new TreeSet<String>();
		for (ConnectionsSource source : sources) {
			try {
				List<String> sourceNames = source.lookupMachines(name, count);
				names.addAll(sourceNames);
			} catch (Exception e) {
				logger.error("error looking up machine names from source: " + source.getSourceName(), e);
			}
		}
		
		// get the top count names from the sorted set
		List<String> topNames = new ArrayList<String>(count);
		Iterator<String> iterator = names.iterator();
		int taken = 0;
		while (iterator.hasNext() && taken<count) {
			topNames.add(iterator.next());
			taken++;
		}
		
		return topNames;
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
		return false;
	}
	
}
