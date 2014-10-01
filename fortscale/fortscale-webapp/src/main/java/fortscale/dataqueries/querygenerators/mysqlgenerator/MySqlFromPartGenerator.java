package fortscale.dataqueries.querygenerators.mysqlgenerator;

import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Generate the "from" part of the query in MySql
 */
@Component
public class MySqlFromPartGenerator implements QueryPartGenerator {

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws
					InvalidQueryException{

		// TODO ...

		return " from sshdata ";
	}

}
