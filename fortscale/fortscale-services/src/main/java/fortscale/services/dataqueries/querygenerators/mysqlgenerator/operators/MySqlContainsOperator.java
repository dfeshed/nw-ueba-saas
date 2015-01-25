package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

/**
 * Created by Yossi on 21/01/2015.
 */
public class MySqlContainsOperator extends MySqlOperator {
    public MySqlContainsOperator(){
        this.sqlOperator = "LIKE";
        this.requiresValue = true;
    }
    public String getOperatorValue(String value){
        return "%" + value + "%";
    }
}
