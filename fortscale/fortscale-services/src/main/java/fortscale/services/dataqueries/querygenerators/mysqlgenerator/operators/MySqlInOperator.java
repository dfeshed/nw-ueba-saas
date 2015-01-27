package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querydto.ConditionField;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlInOperator extends MySqlOperator {
    public MySqlInOperator(){
        this.sqlOperator = "IN";
        this.requiresValue = true;
    }

    @Override
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type){
        if (value != null) {
            String[] valuesArray = value.split(",");
            if (valuesArray != null && valuesArray.length > 0) {
                StringBuilder sb = new StringBuilder("( ");
                for (String singleValue : valuesArray) {
                    sb.append(super.getOperatorValue(mySqlValueGenerator, singleValue, type));
                    sb.append(" , ");
                }
                sb.replace(sb.length()-3,sb.length(),"");
                sb.append(" )");
                return sb.toString();
            }
        }
        return super.getOperatorValue(mySqlValueGenerator, value, type);
    }
}
