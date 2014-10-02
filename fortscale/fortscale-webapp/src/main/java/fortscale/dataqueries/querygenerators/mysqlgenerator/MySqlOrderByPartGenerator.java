package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * Generate the "order by" part of the query in MySql
 */
@Component
public class MySqlOrderByPartGenerator implements QueryPartGenerator {

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws
					InvalidQueryException{

		// TODO ...

		return " ORDER BY date_time_unix ASC NULLS LAST";
	}

}
