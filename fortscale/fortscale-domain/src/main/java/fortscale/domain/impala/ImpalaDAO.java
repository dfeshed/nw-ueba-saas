package fortscale.domain.impala;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.utils.impala.ImpalaQuery;

public abstract class ImpalaDAO<T> {
	@Autowired
	protected JdbcOperations impalaJdbcTemplate;
	
	public abstract String getTableName();
	
	public abstract String getInputFileHeaderDesc();
	
	public void createTable(String inputFile) {
		String sql = String.format("drop table %s",getTableName());
		try {
			
			impalaJdbcTemplate.execute(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		sql = String.format("create table if not exists %s (%s) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'",getTableName(), getInputFileHeaderDesc());
		impalaJdbcTemplate.execute(sql);
		sql = String.format("load data inpath '%s' into table %s", inputFile, getTableName());
		impalaJdbcTemplate.execute(sql);
	}
	
	public List<T> findAll(Pageable pageable, RowMapper<T> mapper){
		List<T> ret = new ArrayList<>();
		ImpalaQuery query = new ImpalaQuery();
		query.select("*").from(getTableName()).limitAndSort(pageable);
		
		ret.addAll(impalaJdbcTemplate.query(query.toSQL(), mapper));
		
		return ret;
	}
}
