package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.common.cache.Cache;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.*;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.web.beans.DataBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Handler for MySQL Queries
 */
@Component
public class MySqlQueryRunner implements DataQueryRunner {

	// The parts of the query

	@Autowired
	private QueryPartGenerator mySqlSelectPartGenerator;

	@Autowired
	private QueryPartGenerator mySqlFromPartGenerator;

	@Autowired
	private QueryPartGenerator mySqlWherePartGenerator;


	// runner for impala

	@Autowired
	private JdbcOperations impalaJdbcTemplate;


	// TODO move cache capabilities from ApiController to here



	@Override public DataBean<List<Map<String, Object>>> runQuery(
					DataQueryDTO dataQueryDTO, boolean useCache) throws InvalidQueryException {


		// Generates query
		String query = generatesQuery(dataQueryDTO);

		// execute Query
		return executeQuery(query);

	}

	/**
	 * Execute the query
	 * @param query	the query to execute
	 * @return the result of the query
	 */
	private DataBean<List<Map<String, Object>>> executeQuery(String query) {

		// TODO copy ApiController.investigate() logic to here (cache, paginf, etc.)
		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		retBean.setData(resultsMap);
		retBean.setTotal(resultsMap.size());
		return retBean;
	}

	/**
	 * Generates the query
	 * @param dataQueryDTO
	 * @throws InvalidQueryException
	 */
	private String generatesQuery(DataQueryDTO dataQueryDTO)
					throws InvalidQueryException {

		StringBuilder sb = new StringBuilder();
		sb.append(mySqlSelectPartGenerator.generateQueryPart(dataQueryDTO))
						.append(mySqlFromPartGenerator.generateQueryPart(dataQueryDTO))
						.append(mySqlWherePartGenerator.generateQueryPart(dataQueryDTO));
		return sb.toString();
	}
}
