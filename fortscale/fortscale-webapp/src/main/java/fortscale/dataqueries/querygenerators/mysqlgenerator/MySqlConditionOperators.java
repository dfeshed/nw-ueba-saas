package fortscale.dataqueries.querygenerators.mysqlgenerator;

import fortscale.dataqueries.querydto.DataQueryDTO;

import java.util.HashMap;

/**
 * Created by Yossi on 04/11/2014.
 * Defines how condition operators are handled by MySQL
 */
public class MySqlConditionOperators {
    private static HashMap<DataQueryDTO.Operator, MySqlOperator> operators = new HashMap<DataQueryDTO.Operator, MySqlOperator>();

    static{
        operators.put(DataQueryDTO.Operator.equals, new MySqlOperator("=", true));
        operators.put(DataQueryDTO.Operator.notEquals, new MySqlOperator("!=", true));
        operators.put(DataQueryDTO.Operator.greaterThan, new MySqlOperator(">", true));
        operators.put(DataQueryDTO.Operator.greaterThanOrEquals, new MySqlOperator(">=", true));
        operators.put(DataQueryDTO.Operator.lesserThan, new MySqlOperator("<", true));
        operators.put(DataQueryDTO.Operator.lesserThanOrEquals, new MySqlOperator("<=", true));
        operators.put(DataQueryDTO.Operator.in, new MySqlOperator("IN", true));
        operators.put(DataQueryDTO.Operator.like, new MySqlOperator("LIKE", true));
        operators.put(DataQueryDTO.Operator.hasValue, new MySqlOperator("IS NOT NULL", true));
        operators.put(DataQueryDTO.Operator.hasNoValue, new MySqlOperator("IS NULL", true));
    }

    public static MySqlOperator getOperator(DataQueryDTO.Operator operator){
        return operators.get(operator);
    }
}
