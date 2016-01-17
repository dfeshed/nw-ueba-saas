package fortscale.common.dataqueries.querygenerators.mysqlgenerator.operators;

import java.util.List;

import fortscale.common.dataqueries.querydto.LogicalOperator;

/**
 * Created by rans on 19/04/15.
 * A Helper class that holds list of SQL operators and the logical operator that needs to be assigned to them
 */
public class MySqlOperatorsList {


    /**
     * list of SQL operators that is be applied in a WHERE condition for one fields
     */
    List<MySqlOperator> mySqlOperators;
    /**
     * a logical operator for the relationship between the SQL operators
     * Can be null when only one SQL operator exists
     */
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
