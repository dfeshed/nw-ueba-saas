package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querydto.LogicalOperator;
import fortscale.services.dataqueries.querydto.QueryOperator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Yossi on 04/11/2014.
 * Defines how condition operators are handled by MySQL
 */
public class MySqlConditionOperators {
    static HashMap<QueryOperator, MySqlOperatorsList> operatorsList = new HashMap<QueryOperator, MySqlOperatorsList>();

    static{
        operatorsList.put(QueryOperator.equals, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("=", true))), null));
        operatorsList.put(QueryOperator.notEquals, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("!=", true))), null));
        operatorsList.put(QueryOperator.greaterThan, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator(">", true))), null));
        operatorsList.put(QueryOperator.greaterThanOrEquals, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator(">=", true))), null));
        operatorsList.put(QueryOperator.lesserThan, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("<", true))), null));
        operatorsList.put(QueryOperator.lesserThanOrEquals, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("<=", true))), null));
        operatorsList.put(QueryOperator.in, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlInOperator())), null));
        operatorsList.put(QueryOperator.between, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlBetweenOperator())), null));
        operatorsList.put(QueryOperator.like, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("LIKE", true))), null));
        operatorsList.put(QueryOperator.startsWith, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlStartsWithOperator())), null));
        operatorsList.put(QueryOperator.endsWith, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlEndsWithOperator())), null));
        operatorsList.put(QueryOperator.contains, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlContainsOperator())), null));
        operatorsList.put(QueryOperator.hasValue, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("IS NOT NULL", false))), null));
        operatorsList.put(QueryOperator.hasNoValue, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("IS NULL", false))), null));
        operatorsList.put(QueryOperator.stringHasValue, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("IS NOT NULL", false), new MySqlOperator("!= ''", false))), LogicalOperator.AND));
        operatorsList.put(QueryOperator.stringHasNoValue, new MySqlOperatorsList(new ArrayList<MySqlOperator>(Arrays.asList(new MySqlOperator("IS NULL", false), new MySqlOperator("=''", false))), LogicalOperator.OR));
    }

    /**
     *
     * @param operator an operator that is passed from the REST API
     * @return MySqlOperatorsList: list of {@link MySqlOperatorsList} that holds SQL condition expression
     * @throws InvalidQueryException
     */
    public static MySqlOperatorsList getOperator(QueryOperator operator,QueryValueType queryValueType) throws InvalidQueryException {
        if (QueryValueType.STRING.equals(queryValueType) || QueryValueType.SELECT.equals(queryValueType)) {
            if (QueryOperator.hasValue.equals(operator)) {
                operator = QueryOperator.stringHasValue;
            } else if (QueryOperator.hasNoValue.equals(operator)) {
                operator = QueryOperator.stringHasNoValue;
            }
        }
        MySqlOperatorsList mySqlOperatorList = operatorsList.get(operator);
        //The mySqlOperatorList should not be null or empty. If there is more than one entry, then the LogicalOperator should not be null
		if (mySqlOperatorList == null || mySqlOperatorList.getMySqlOperators().size() == 0
                || mySqlOperatorList.getMySqlOperators().size() > 1 && mySqlOperatorList.getLogicalOperator() == null) {
            throw new InvalidQueryException("Unknown operator for MySql: " + operator + ".");
        }
		return mySqlOperatorList;
    }
}
