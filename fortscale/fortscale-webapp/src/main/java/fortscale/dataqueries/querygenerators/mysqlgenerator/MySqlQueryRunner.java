package fortscale.dataqueries.querygenerators.mysqlgenerator;


import com.google.api.client.repackaged.com.google.common.base.Joiner;
import fortscale.dataqueries.querydto.DataQueryDTO;
import fortscale.dataqueries.querygenerators.*;
import fortscale.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.web.beans.DataBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handler for MySQL Queries
 */
@Component
public class MySqlQueryRunner implements DataQueryRunner {

    // The parts of the query

    @Autowired
    private QueryPartGenerator mySqlSelectPartGenerator;

    @Autowired
    private QueryPartGenerator mySqlFromPartGenerator;

    @Autowired
    private QueryPartGenerator mySqlWherePartGenerator;

    @Autowired
    private QueryPartGenerator mySqlGroupByPartGenerator;

    @Autowired
    private QueryPartGenerator mySqlLimitPartGenerator;

    @Autowired
    private QueryPartGenerator mySqlOrderByPartGenerator;

    // runner for impala

    @Autowired
    private JdbcOperations impalaJdbcTemplate;


    /**
     * Execute the query
     *
     * @param query the query to execute
     * @return the result of the query
     */
    @Override
    public DataBean<List<Map<String, Object>>> executeQuery(String query) {

        DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
        List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
        retBean.setData(resultsMap);
        retBean.setTotal(resultsMap.size());
        return retBean;
    }

    /**
     * Generates the query
     *
     * @param dataQueryDTO The DTO that represents the query
     * @throws InvalidQueryException in case the DTO is not a valid query
     */
    @Override
    public String generateQuery(DataQueryDTO dataQueryDTO)
            throws InvalidQueryException {

    	QueryPartGenerator[] partGenerators = new QueryPartGenerator[]{
                mySqlSelectPartGenerator,
                mySqlFromPartGenerator,
                mySqlWherePartGenerator,
                mySqlGroupByPartGenerator,
                mySqlOrderByPartGenerator,
                mySqlLimitPartGenerator
        };
    	
        StringBuilder sb = new StringBuilder();
        for(QueryPartGenerator generator: partGenerators){
            sb.append(generator.generateQueryPart(dataQueryDTO)).append(" ");
        }

        return sb.toString();
    }
}
