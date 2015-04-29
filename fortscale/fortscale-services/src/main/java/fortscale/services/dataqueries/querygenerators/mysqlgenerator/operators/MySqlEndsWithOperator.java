package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querydto.ConditionField;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlEndsWithOperator extends MySqlOperator {
    public MySqlEndsWithOperator(){
        this.sqlOperator = "LIKE";
        this.requiresValue = true;
    }


    @Override
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type, boolean enforcefiledValueToLowererCase){
        return super.getOperatorValue(mySqlValueGenerator, "%" + value , type, enforcefiledValueToLowererCase);
    }
}
