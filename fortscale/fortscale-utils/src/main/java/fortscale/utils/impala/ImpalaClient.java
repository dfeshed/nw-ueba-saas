package fortscale.utils.impala;

import org.apache.commons.lang.StringUtils;
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
	
	public void refreshTable(String tableName) throws Exception {
		Assert.hasText(tableName);
			
		String sql = String.format("REFRESH %s", tableName);
		try {
			impalaJdbcTemplate.execute(sql);
		} catch (Exception e) {
			logger.error("error refreshing impala table " + tableName, e);
			throw new Exception("error refreshing impala table " + tableName, e);
		}
	}
	
	public void addPartitionToTable(String tableName, Long runtime) throws Exception{
		Assert.hasText(tableName);
		Assert.notNull(runtime);
		addPartitionToTable(tableName, String.format("runtime=%s", runtime.toString()));
	}
	
	public void addPartitionToTable(String tableName, String partition) throws Exception {
		Assert.hasText(tableName);
		Assert.hasText(partition);
		
		String sql = String.format("alter table %s add if not exists partition (%s)", tableName, partition);
		try {
			impalaJdbcTemplate.execute(sql);
		} catch (Exception e) {
			String errorMessage = String.format("failed to to run the following  sql command: %s", sql);
			logger.error(errorMessage, e);
			throw new Exception(errorMessage, e);
		}
	}
	
	public void dropPartitionFromTable(String tableName, String partition) throws DataAccessException {
		Assert.hasText(tableName);
		Assert.hasText(partition);
		
		String sql = String.format("alter table %s drop if exists partition (%s)", tableName, partition);
		impalaJdbcTemplate.execute(sql);
	}
	
	public void createTable(String tableName, String fields, String partition, String delimiter, String location){
		Assert.hasText(tableName);
		Assert.hasText(fields);
		Assert.hasText(delimiter);
		Assert.hasText(location);
		
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE EXTERNAL TABLE ").append(tableName).append("(").append(fields).append(")");
		if(!StringUtils.isEmpty(partition)){
			builder.append(" PARTITIONED BY (").append(partition).append(")");
		}
		builder.append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY '").append(delimiter).append("'");
		builder.append(" STORED AS TEXTFILE");
		builder.append(" LOCATION '").append(location).append("'");
		logger.debug(builder.toString());
		impalaJdbcTemplate.execute(builder.toString());
	}
	
	public void createTableView(String tableViewName, String selectStatement){
		Assert.hasText(tableViewName);
		Assert.hasText(selectStatement);
		String sql = String.format("CREATE VIEW %s AS %s", tableViewName, selectStatement);
		impalaJdbcTemplate.execute(sql);
	}
	
}
