package fortscale.services.dataqueries.querygenerators.mysqlgenerator.operators;

import fortscale.services.dataqueries.querydto.LogicalOperator;
import fortscale.services.dataqueries.querydto.QueryOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rans on 19/04/15.
 */
public class MySqlOperatorsList {


    List<MySqlOperator> mySqlOperators;
    LogicalOperator logicalOperator;

    public MySqlOperatorsList(List<MySqlOperator> mySqlOperators, LogicalOperator logicalOperator) {
        this.mySqlOperators = mySqlOperators;
        this.logicalOperator = logicalOperator;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public List<MySqlOperator> getMySqlOperators() {
        return mySqlOperators;
    }

    public void setMySqlOperators(List<MySqlOperator> mySqlOperators) {
        this.mySqlOperators = mySqlOperators;
    }
}
