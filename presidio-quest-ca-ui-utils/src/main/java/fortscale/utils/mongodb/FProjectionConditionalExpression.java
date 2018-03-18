package fortscale.utils.mongodb;

import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FProjectionConditionalExpression extends FIProjectionExpression{
	
	private FIProjectionConditionalSubExpression booleanExp;
	private FIProjectionConditionalSubExpression trueCase;
	private FIProjectionConditionalSubExpression falseCase;
	
	public FProjectionConditionalExpression(FIProjectionConditionalSubExpression booleanExp,
			FIProjectionConditionalSubExpression trueCase,
			FIProjectionConditionalSubExpression falseCase){
		this.booleanExp = booleanExp;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}

	@Override
	public DBObject toDBObject(AggregationOperationContext context) {
//		String value = String.format("[%s,%s,%s]", booleanExp.toElement(), trueCase.toElement(), falseCase.toElement());
		BasicDBList condValues = new BasicDBList();
		condValues.add(booleanExp.toElement());
		condValues.add(trueCase.toElement());
		condValues.add(falseCase.toElement());
		return new BasicDBObject("$cond", condValues);
	}

}
