package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.querydto.DataQueryDTO;
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
    private MySqlUtils mySqlUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        if (dataQueryDTO.sort == null || dataQueryDTO.sort.size() == 0)
            return null;

        ArrayList<String> sorts = new ArrayList<String>();
        Joiner joiner = Joiner.on(", ").skipNulls();

        for(DataQueryDTO.Sort sort: dataQueryDTO.sort){
            String field = mySqlUtils.getFieldSql(sort.field, dataQueryDTO);
            sorts.add( field+ (sort.direction != null ? " " + sort.direction.name() : ""));
        }

		return "ORDER BY " + joiner.join(sorts);
	}

    public void setMySqlUtils(MySqlUtils mySqlUtils) {
        this.mySqlUtils = mySqlUtils;
    }
}
