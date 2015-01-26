package fortscale.services.dataqueries.querygenerators.mysqlgenerator;


import fortscale.services.dataentity.QueryFieldFunction;
import fortscale.services.dataqueries.querydto.DataQueryDTO;
import fortscale.services.dataqueries.querydto.DataQueryField;
import fortscale.services.dataqueries.querydto.FieldFunction;
import fortscale.services.dataqueries.querygenerators.*;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for MySQL Queries
 */
@Component
public class MySqlQueryRunner extends DataQueryRunner {

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

    @Autowired
    private QueryPartGenerator mySqlJoinPartGenerator;

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
    public List<Map<String, Object>> executeQuery(String query) {
        return impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
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
                mySqlJoinPartGenerator,
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
