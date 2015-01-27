package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querydto.ConditionField;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlStartsWithOperator extends MySqlOperator {
    public MySqlStartsWithOperator(){
        this.sqlOperator = "LIKE";
        this.requiresValue = true;
    }
    
    @Override
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type){
        return super.getOperatorValue(mySqlValueGenerator, value + "%", type);
    }
}
