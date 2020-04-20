package fortscale.utils.mongodb;

public class FProjectionConditionalSubExpressionRefValue implements FIProjectionConditionalSubExpression{
	
	private String refValue;
	
	public FProjectionConditionalSubExpressionRefValue(String refValue){
		this.refValue = refValue;
	}

	@Override
	public String toElement() {
		return String.format("$%s", refValue);
	}

}
