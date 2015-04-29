package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

/**
 * Created by Yossi on 03/11/2014.
 */
public class MySqlOperator{
    public MySqlOperator(){}

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



    /**
     * Gets an SQL value and returns the value with anything the operator should add. For example, the 'contains' operator should add '%' before and after the value.
     * @param value
     * @param type - the type of the value of the field
     * @param enforcefiledValueToLowererCase - if true, the value generator will convert the value to lower case
     * @return
     */
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type, boolean enforcefiledValueToLowererCase){
        return requiresValue ? mySqlValueGenerator.generateSql(value,type, enforcefiledValueToLowererCase) : "" ;
    }
}