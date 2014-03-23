package fortscale.utils.impala;

public class ImpalaCriteriaString implements ImpalaQueryElementInterface{
	
	private String criteria;
	
	
	public ImpalaCriteriaString(String criteria){
		this.criteria = criteria;
	}

	public static ImpalaCriteriaString statement(String criteria) {
		return new ImpalaCriteriaString(criteria);
	}
		
	@Override
	public void appendTo(StringBuilder builder) {
		builder.append(criteria);
	}

}
