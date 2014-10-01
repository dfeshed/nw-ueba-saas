package fortscale.dataqueries.querygenerators;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.mysqlgenerator.MySqlQueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory for creating DataQueryRunner
 */
@Component
public class DataQueryRunnerFactory {

	@Autowired
	private MySqlQueryRunner mySqlQueryRunner;

	/**
	 * Get the relevant query runner according to DTO
	 * @param dataQueryDTO the query object
	 * @return query runner
	 */
	public DataQueryRunner getDataQueryRunner(DataQueryDTO dataQueryDTO){

		// today we support only MySql format (for Impala queries)
		// later we will choose the matching runner according to the entity in the DTO
		return mySqlQueryRunner;

	}

}
