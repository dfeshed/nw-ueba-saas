package fortscale.dataqueries.querydto;

import java.util.List;

/**
* Created by Yossi on 10/11/2014.
*/
public class ConditionTerm extends Term{
    private List<Term> terms;
    private LogicalOperator operator;

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public void setOperator(LogicalOperator operator) {
        this.operator = operator;
    }
}
