package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.services.dataqueries.querydto.DataQueryCombineMethod;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.MultipleDataQueryDTO;
import fortscale.services.dataqueries.querygenerators.QueryPartGenerator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yossi on 23/12/2014.
 * Generates the SQL for combining multiple queries, such as with UNION
 */
@Component
public class MySqlMultipleQueryGenerator extends QueryPartGenerator<MultipleDataQueryDTO> {
    @Autowired
    MySqlQueryRunner mySqlQueryRunner;

    final static HashMap<DataQueryCombineMethod, String> combineMethods = new HashMap<>();
    static
    {
        combineMethods.put(DataQueryCombineMethod.UnionAll, " UNION ALL ");
        combineMethods.put(DataQueryCombineMethod.UnionDistinct, " UNION ");
    }

    public String generateQueryPart(MultipleDataQueryDTO multipleDataQueryDTO) throws InvalidQueryException{
        ArrayList<String> fieldsSql = new ArrayList<>();
        Joiner joiner = Joiner.on(combineMethods.get(multipleDataQueryDTO.getCombineMethod())).skipNulls();

        for(DataQueryDTO dataQueryDTO: multipleDataQueryDTO.getDataQueries()){
            fieldsSql.add(mySqlQueryRunner.generateQuery(dataQueryDTO));
        }

        return joiner.join(fieldsSql);
    }

    /**
     * Set the MySqlQueryRunner, for tests
     * @param mySqlQueryRunner
     */
    public void setMySqlQueryRunner(MySqlQueryRunner mySqlQueryRunner){
        this.mySqlQueryRunner = mySqlQueryRunner;
    }
}
