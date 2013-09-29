package fortscale.utils.impala;

public class ImpalaCriteriaString implements ImpalaQueryElementInterface{
	
	private String criteria;
	
	
	public ImpalaCriteriaString(String criteria){
		this.criteria = criteria;
	}

	@Override
	public void appendTo(StringBuilder builder) {
		builder.append(criteria);
	}

}
