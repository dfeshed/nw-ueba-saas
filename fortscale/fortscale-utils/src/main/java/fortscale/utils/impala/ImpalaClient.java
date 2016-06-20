package fortscale.utils.impala;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class ImpalaClient {

	private static Logger logger = LoggerFactory.getLogger(ImpalaClient.class);
	
	@Autowired
	protected JdbcOperations impalaJdbcTemplate;

	@Value("${impala.table.fields.updateTimestamp}")
	private String impalaUpdateTimestampField;

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

	/**
	 * Create EXTERNAL table in Impala
	 * @param tableName	the name of the table
	 * @param fields	the fields of the table (names and types)
	 * @param partition	the partition type (can be left null)
	 * @param delimiter	delimiter of the HDFS file
	 * @param location	location of the HDFS file
	 * @param onlyIfNotExist	true if we want to create the table only if doesn't exist
	 */
	public void createTable(String tableName, String fields, String partition, String delimiter, String location,
			boolean onlyIfNotExist){
		Assert.hasText(tableName);
		Assert.hasText(fields);
		Assert.hasText(delimiter);
		Assert.hasText(location);
		
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE EXTERNAL TABLE ");
		if (onlyIfNotExist) {
			builder.append(" IF NOT EXISTS ");
		}
		fields += ", " + impalaUpdateTimestampField + " BIGINT";
		builder.append(tableName).append("(").append(fields).append(")");
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

	public boolean dropTable(String tableViewName) {
		Assert.hasText(tableViewName);
		boolean success = false;
		String sql = String.format("DROP TABLE IF EXISTS %s", tableViewName);
		try {
			impalaJdbcTemplate.execute(sql);
			success = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return success;
	}

	public Set<String> getAllTables() {
		String sql = String.format("show tables");
		List<Map<String, Object>> result = impalaJdbcTemplate.queryForList(sql);
		Set<String> tableNames = new HashSet();
		for (Map<String, Object> entry: result) {
			tableNames.add((String)entry.get("name"));
		}
		return tableNames;
	}

	public boolean isTableExists(String tableViewName) {
		Assert.hasText(tableViewName);
		String sql = String.format("DESC %s", tableViewName);
		boolean exists = false;
		try {
			impalaJdbcTemplate.execute(sql);
			exists = true;
		} catch (Exception ex) {}
		return exists;
	}
	
}
