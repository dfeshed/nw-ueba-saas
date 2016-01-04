package fortscale.common.dataqueries.querygenerators.mysqlgenerator;

import com.google.common.base.Joiner;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.QuerySort;
import fortscale.common.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;
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
