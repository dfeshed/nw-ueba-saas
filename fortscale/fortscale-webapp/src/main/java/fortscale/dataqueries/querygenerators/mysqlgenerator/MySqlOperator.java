package fortscale.dataqueries.querygenerators.mysqlgenerator;

/**
 * Created by Yossi on 03/11/2014.
 */
public enum MySqlOperator{
    equals ("="),
    notEquals ("!="),
    greaterThan (">"),
    greaterThanOrEquals (">="),
    lesserThan ("<="),
    lesserThanOrEquals ("<="),
    in ("IN"),
    like ("LIKE"),
    hasValue ("IS NOT NULL", false),
    hasNoValue ("IS NULL", false);

    public final String sqlOperator;
    public final Boolean requiresValue;

    MySqlOperator(String operator){
        this.sqlOperator = operator;
        this.requiresValue = true;
    }

    MySqlOperator(String operator, Boolean requiresValue){
        this.sqlOperator = operator;
        this.requiresValue = requiresValue;
    }
}