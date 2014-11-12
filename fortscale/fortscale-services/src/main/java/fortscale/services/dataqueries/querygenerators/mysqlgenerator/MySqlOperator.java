package fortscale.services.dataqueries.querygenerators.mysqlgenerator;

/**
 * Created by Yossi on 03/11/2014.
 */
public class MySqlOperator{
    public MySqlOperator(String sqlOperator, Boolean requiresValue){
        this.sqlOperator = sqlOperator;
        this.requiresValue = requiresValue;
    }

    /**
     * The SQL this operator generates
     */
    public String sqlOperator;

    /**
     * Whether this operator requires a value to go along with the operator, or the operator works by itself, like in "IS NULL"
     */
    public Boolean requiresValue;
}