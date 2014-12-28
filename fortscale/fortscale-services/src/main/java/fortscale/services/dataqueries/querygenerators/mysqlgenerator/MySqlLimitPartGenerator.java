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
        sb.append(dataQueryDTO.getOffset() > 0 ? " OFFSET " + dataQueryDTO.getOffset() : "");
        return sb.toString();
    }
}
