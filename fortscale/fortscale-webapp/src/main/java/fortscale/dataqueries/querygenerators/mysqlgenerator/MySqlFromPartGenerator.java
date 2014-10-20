package fortscale.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.ArrayList;

/**
 * Generate the "from" part of the query in MySql
 */
@Component
public class MySqlFromPartGenerator implements QueryPartGenerator {
    @Autowired
    private MySqlUtils mySqlUtils;

	public String generateQueryPart(DataQueryDTO dataQueryDTO) throws InvalidQueryException{
        // For now the generator supports only single table queries. When joins are supported, need to add the logic here.
        return "FROM " + mySqlUtils.getEntityTable(dataQueryDTO.entities[0]);
	}
}
