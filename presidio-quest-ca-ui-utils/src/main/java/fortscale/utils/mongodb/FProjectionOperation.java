package fortscale.utils.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FProjectionOperation implements AggregationOperation{

	private List<FIProjectionExpression> expressions = new ArrayList<FIProjectionExpression>();
	
	public FProjectionOperation(List<FIProjectionExpression> expressions){
		this.expressions = expressions;
	}
	
	
	@Override
	public DBObject toDBObject(AggregationOperationContext context) {
		BasicDBObject fieldsObject = new BasicDBObject();

		for (FIProjectionExpression exp : expressions) {
			fieldsObject.putAll(exp.toDBObject(context));
		}
	
		return new BasicDBObject("$project", fieldsObject);
	}

}
