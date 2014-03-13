package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToSeconds;

import org.springframework.jdbc.core.JdbcOperations;

import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;

public class LDAPConnectionsSource extends ConnectionsSource {

	public LDAPConnectionsSource(JdbcOperations impalaJdbcTemplate) {
		super(impalaJdbcTemplate, new MonthlyPartitionStrategy(), new LDAPConnectionsRowMapper());
	}
	
	@Override
	public String getSourceName() {
		return "ldap";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		SQLQueryBuilder query = new SQLQueryBuilder();
		query.select("timegeneratedunixtime, account_name, client_address, machine_name, service_name, yearmonth");
		query.from("wmievents4769");
		
		// add criteria for machine to pivot on
		query.where(isSource? 
				String.format("(client_address='%s' OR machine_name='%s')", source, source) :
				String.format("service_name='%s'", source, source));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.where("timegeneratedunixtime>=%d", convertToSeconds(filter.getStart()));
			query.where("yearmonth>=%s", partition.getImpalaPartitionValue(filter.getStart()));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.where("timegeneratedunixtime<=%d", convertToSeconds(filter.getEnd()));
			query.where("yearmonth<=%s", partition.getImpalaPartitionValue(filter.getEnd()));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.where("lower(account_name) in (%s)", filter.getAccounts());
			} else {
				query.where("lower(account_name) not in (%s)", filter.getAccounts());
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.where("lower(machine_name) in (%s)", filter.getMachines());
				query.where("lower(service_name) in (%s)", filter.getMachines());
			} else {
				query.where("lower(machine_name) not in (%s)", filter.getMachines());
				query.where("lower(service_name) not in (%s)", filter.getMachines());
			}
		}
		
		return query.toString();
	}
}
