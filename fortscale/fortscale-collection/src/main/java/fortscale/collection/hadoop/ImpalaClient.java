package fortscale.collection.hadoop;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.dao.DataAccessException;

@Component
public class ImpalaClient {

	private static Logger logger = LoggerFactory.getLogger(ImpalaClient.class);
	
	@Autowired
	protected JdbcOperations impalaJdbcTemplate;
	
	public void refreshTable(String tableName) throws JobExecutionException {
		Assert.hasText(tableName);
			
		String sql = String.format("REFRESH %s", tableName);
		try {
			impalaJdbcTemplate.execute(sql);
		} catch (DataAccessException e) {
			logger.error("error refreshing impala table " + tableName, e);
			throw new JobExecutionException("error refreshing impala table " + tableName, e);
		}
	}
	
}
