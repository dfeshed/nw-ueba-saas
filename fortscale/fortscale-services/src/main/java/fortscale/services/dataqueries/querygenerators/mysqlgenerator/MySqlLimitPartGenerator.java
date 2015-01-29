package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

/**
 * Generates the "limit" part of the query in MySql
 */
@Component
public class MySqlLimitPartGenerator extends QueryPartGenerator {
    public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException {
        if (dataQueryDTO.getLimit() <= 0)
            return "";

        StringBuilder sb = new StringBuilder("LIMIT ").append(dataQueryDTO.getLimit());

        if (dataQueryDTO.getOffset() > 0){
            // Impala limitation, if using OFFSET, ORDER BY has to be used:
            if (dataQueryDTO.getSort() == null)
                throw new InvalidQueryException("Can't set offset to a query that doesn't have sort.");

            sb.append(" OFFSET ").append(dataQueryDTO.getOffset());

        }
        return sb.toString();
    }
}
