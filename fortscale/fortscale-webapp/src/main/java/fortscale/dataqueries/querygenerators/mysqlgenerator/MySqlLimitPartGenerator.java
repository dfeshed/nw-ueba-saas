package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * Generates the "limit" part of the query in MySql
 */
@Component
public class MySqlLimitPartGenerator implements QueryPartGenerator {
    public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        if (dataQueryDTO.limit <= 0)
            return null;

        return "LIMIT " + dataQueryDTO.limit + (dataQueryDTO.offset > 0 ? " OFFSET " + dataQueryDTO.offset : "");
    }
}
