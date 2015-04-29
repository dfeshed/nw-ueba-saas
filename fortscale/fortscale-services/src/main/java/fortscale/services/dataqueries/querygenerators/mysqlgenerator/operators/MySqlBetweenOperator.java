package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataentity.QueryValueType;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueComparator;
import fortscale.services.dataqueries.querygenerators.mysqlgenerator.MySqlValueGenerator;

import java.util.Arrays;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlBetweenOperator extends MySqlOperator {
    public MySqlBetweenOperator(){
        this.sqlOperator = "BETWEEN";
        this.requiresValue = true;
    }

    @Override
    public String getOperatorValue(MySqlValueGenerator mySqlValueGenerator, String value, QueryValueType type, boolean enforcefiledValueToLowererCase) {
        if (value != null) {
            String[] valuesArray = value.split(",");
            if (valuesArray != null && valuesArray.length == 2) {
                StringBuilder sb = new StringBuilder(" ");
                Arrays.sort(valuesArray, new MySqlValueComparator(type));
                sb.append(super.getOperatorValue(mySqlValueGenerator, valuesArray[0], type, enforcefiledValueToLowererCase));
                sb.append(" AND ");
                sb.append(super.getOperatorValue(mySqlValueGenerator, valuesArray[1], type, enforcefiledValueToLowererCase));
                sb.append(" ");
                return sb.toString();
            }
        }
        return super.getOperatorValue(mySqlValueGenerator, value, type, enforcefiledValueToLowererCase);
    }
}
