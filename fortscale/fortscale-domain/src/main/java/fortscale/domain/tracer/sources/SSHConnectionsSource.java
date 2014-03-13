package fortscale.domain.tracer.sources;

import static fortscale.utils.TimestampUtils.convertToSeconds;

import org.springframework.jdbc.core.JdbcOperations;

import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;

public class SSHConnectionsSource extends ConnectionsSource {

	public SSHConnectionsSource(JdbcOperations impalaJdbcTemplate) {
		super(impalaJdbcTemplate, new MonthlyPartitionStrategy(), new SSHConnetionsRowMapper());
	}
	
	@Override
	public String getSourceName() {
		return "ssh";
	}
	
	@Override
	protected String buildQuery(String source, boolean isSource, FilterSettings filter) {
		SQLQueryBuilder query = new SQLQueryBuilder();
		query.select("date_time_unix, username, source_ip, target_machine, status, hostname, yearmonth");
		query.from("sshdata");
		
		// add criteria for machine to pivot on
		query.where(isSource? 
				String.format("(source_ip='%s' OR hostname='%s')", source, source) :
				String.format("target_machine='%s'", source, source));
		
		// add criteria for start
		if (filter.getStart()!=0L) {
			query.where("date_time_unix>=%d", convertToSeconds(filter.getStart()));
			query.where("yearmonth>=%s", partition.getImpalaPartitionValue(filter.getStart()));
		}
		
		// add criteria for end
		if (filter.getEnd()!=0L) {
			query.where("date_time_unix<=%d", convertToSeconds(filter.getEnd()));
			query.where("yearmonth<=%s", partition.getImpalaPartitionValue(filter.getEnd()));
		}
			
		// add criteria for accounts
		if (!filter.getAccounts().isEmpty()) {
			if (filter.getAccountsListMode()==ListMode.Include) {
				query.where("lower(username) in (%s)", filter.getAccounts());
			} else {
				query.where("lower(username) not in (%s)", filter.getAccounts());
			}
		}
		
		// add criteria for machines
		if (!filter.getMachines().isEmpty()) {
			if (filter.getMachinesListMode()==ListMode.Include) {
				query.where("lower(hostname) in (%s)", filter.getMachines());
			} else {
				query.where("lower(hostname) not in (%s)", filter.getMachines());
			}
		}
		
		return query.toString();
	}
	
}
