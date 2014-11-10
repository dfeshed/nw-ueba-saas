package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querydto.QuerySort;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Generate the "order by" part of the query in MySql
 */
@Component
public class MySqlOrderByPartGenerator implements QueryPartGenerator {
    @Autowired
    MySqlFieldGenerator mySqlFieldGenerator;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (dataQueryDTO.getSort() == null || dataQueryDTO.getSort().size() == 0)
            return "";

        ArrayList<String> sorts = new ArrayList<String>();
        Joiner joiner = Joiner.on(", ").skipNulls();

        for(QuerySort sort: dataQueryDTO.getSort()){
            String field = mySqlFieldGenerator.generateSql(sort.getField(), dataQueryDTO);
            sorts.add( field+ (sort.getDirection() != null ? " " + sort.getDirection().name() : ""));
        }

		return "ORDER BY " + joiner.join(sorts);
	}

    public void setMySqlFieldGenerator(MySqlFieldGenerator mySqlFieldGenerator){
        this.mySqlFieldGenerator = mySqlFieldGenerator;
    }
}
