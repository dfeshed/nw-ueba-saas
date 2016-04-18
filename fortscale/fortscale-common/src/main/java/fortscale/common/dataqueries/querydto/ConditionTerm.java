package fortscale.common.dataqueries.querydto;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Yossi on 10/11/2014.
*/
public class ConditionTerm extends Term{
    private List<Term> terms;
    private LogicalOperator logicalOperator;


	public ConditionTerm(){}
	public ConditionTerm(ConditionTerm other)
	{
		this.terms = new ArrayList<>(other.getTerms());
		this.logicalOperator = other.logicalOperator;
	}

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public boolean isEmpty(){
        return terms.isEmpty();
    }
}
