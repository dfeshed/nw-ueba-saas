package fortscale.common.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.common.dataentity.QueryValueType;
import fortscale.common.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlStartsWithOperator extends MySqlOperator {
    public MySqlStartsWithOperator(){
        this.sqlOperator = "LIKE";
        this.requiresValue = true;
    }
    
    @Override
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type, boolean enforcefiledValueToLowererCase){
        return super.getOperatorValue(mySqlValueGenerator, value + "%", type, enforcefiledValueToLowererCase);
    }
}
