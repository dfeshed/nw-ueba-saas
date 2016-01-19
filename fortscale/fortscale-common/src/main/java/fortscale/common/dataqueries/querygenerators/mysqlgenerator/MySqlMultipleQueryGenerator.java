package fortscale.common.dataqueries.querygenerators.mysqlgenerator;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

import fortscale.common.dataqueries.querydto.DataQueryCombineMethod;
import fortscale.common.dataqueries.querydto.DataQueryDTO;
import fortscale.common.dataqueries.querydto.MultipleDataQueryDTO;
import fortscale.common.dataqueries.querygenerators.exceptions.InvalidQueryException;

/**
 * Created by Yossi on 23/12/2014.
 * Generates the SQL for combining multiple queries, such as with UNION
 */
@Component
public class MySqlMultipleQueryGenerator {
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
     * Generates a multiple query's SQL and returns it as a subQuery. e.g instead of "[query1] UNION [query2]", returns "([query1] UNION [query2]) as t1"
     * @param subQuery
     * @return
     * @throws InvalidQueryException
     */
    public String getSubQuerySql(MultipleDataQueryDTO subQuery) throws InvalidQueryException {
        StringBuilder stringbuilder = new StringBuilder("(");
        stringbuilder.append(generateQueryPart(subQuery));
        stringbuilder.append(") as t1");
        return stringbuilder.toString();
    }

    /**
     * Set the MySqlQueryRunner, for tests
     * @param mySqlQueryRunner
     */
    public void setMySqlQueryRunner(MySqlQueryRunner mySqlQueryRunner){
        this.mySqlQueryRunner = mySqlQueryRunner;
    }
}
