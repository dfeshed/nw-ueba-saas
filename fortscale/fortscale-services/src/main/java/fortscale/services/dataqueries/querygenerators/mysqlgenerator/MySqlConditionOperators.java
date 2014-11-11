package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

import fortscale.services.dataqueries.querydto.QueryOperator;
import fortscale.services.dataqueries.querygenerators.exceptions.InvalidQueryException;

import java.util.HashMap;

/**
 * Created by Yossi on 04/11/2014.
 * Defines how condition operators are handled by MySQL
 */
public class MySqlConditionOperators {
    private static HashMap<QueryOperator, MySqlOperator> operators = new HashMap<QueryOperator, MySqlOperator>();

    static{
        operators.put(QueryOperator.equals, new MySqlOperator("=", true));
        operators.put(QueryOperator.notEquals, new MySqlOperator("!=", true));
        operators.put(QueryOperator.greaterThan, new MySqlOperator(">", true));
        operators.put(QueryOperator.greaterThanOrEquals, new MySqlOperator(">=", true));
        operators.put(QueryOperator.lesserThan, new MySqlOperator("<", true));
        operators.put(QueryOperator.lesserThanOrEquals, new MySqlOperator("<=", true));
        operators.put(QueryOperator.in, new MySqlOperator("IN", true));
        operators.put(QueryOperator.like, new MySqlOperator("LIKE", true));
        operators.put(QueryOperator.hasValue, new MySqlOperator("IS NOT NULL", true));
        operators.put(QueryOperator.hasNoValue, new MySqlOperator("IS NULL", true));
    }

    public static MySqlOperator getOperator(QueryOperator operator) throws InvalidQueryException {
		MySqlOperator mySqlOperator = operators.get(operator);
		if (mySqlOperator == null)
			throw new InvalidQueryException("Unknown operator for MySql: " + operator + ".");
		return mySqlOperator;
    }
}
