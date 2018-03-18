package fortscale.utils.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class FProjectionConditionalSubExpressionCmp implements FIProjectionConditionalSubExpression{
	private String operator;
	private FIProjectionConditionalSubExpression exp1;
	private FIProjectionConditionalSubExpression exp2;
	
	public FProjectionConditionalSubExpressionCmp(String operator,FIProjectionConditionalSubExpression exp1, FIProjectionConditionalSubExpression exp2){
		this.operator = operator;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	@Override
	public Object toElement() {
		BasicDBList cmpValues = new BasicDBList();
		cmpValues.add(exp1.toElement());
		cmpValues.add(exp2.toElement());
		return new BasicDBObject(String.format("$%s",operator), cmpValues);
	}
	
	public static FProjectionConditionalSubExpressionCmp generateGTECmp(FIProjectionConditionalSubExpression exp1, FIProjectionConditionalSubExpression exp2){
		return new FProjectionConditionalSubExpressionCmp("gte", exp1, exp2);
	}
}
