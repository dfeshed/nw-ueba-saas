package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlStartsWithOperator extends MySqlOperator {
    public MySqlStartsWithOperator(){
        this.sqlOperator = "LIKE";
        this.requiresValue = true;
    }
    public String getOperatorValue(String value){
        return value + "%";
    }
}
