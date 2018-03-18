package fortscale.utils.mongodb;

public class FProjectionConditionalSubExpressionSimpleValue implements FIProjectionConditionalSubExpression{
	
	private Object value;
	
	public FProjectionConditionalSubExpressionSimpleValue(Object value){
		this.value = value;
	}

	@Override
	public Object toElement() {
		return value;
	}

}