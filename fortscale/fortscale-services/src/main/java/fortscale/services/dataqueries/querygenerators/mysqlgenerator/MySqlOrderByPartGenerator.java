package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.QuerySort;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Generate the "order by" part of the query in MySql
 */
@Component
public class MySqlOrderByPartGenerator extends QueryPartGenerator {
	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (dataQueryDTO.getSort() == null || dataQueryDTO.getSort().size() == 0)
            return "";

        ArrayList<String> sorts = new ArrayList<String>();
        Joiner joiner = Joiner.on(", ").skipNulls();

        for(QuerySort sort: dataQueryDTO.getSort()){
            String field = sort.getField().getAlias();
            if (field == null)
                field = mySqlFieldGenerator.generateSql(sort.getField(), dataQueryDTO,  false, false);

            sorts.add( field+ (sort.getDirection() != null ? " " + sort.getDirection().name() : "") + " NULLS LAST ");
        }

        StringBuilder sb = new StringBuilder("ORDER BY ").append(joiner.join(sorts));
		return sb.toString();
	}
}
