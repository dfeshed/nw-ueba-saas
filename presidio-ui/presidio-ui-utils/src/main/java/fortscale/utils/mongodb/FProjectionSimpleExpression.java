package fortscale.utils.mongodb;

import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class FProjectionSimpleExpression extends FIProjectionExpression {
	
	private String fieldName;
	private Object value;
	
	public FProjectionSimpleExpression(String fieldName){
		this.fieldName = fieldName;
		this.value = new Integer(1);
	}
	
	public FProjectionSimpleExpression(String fieldName, boolean showField){
		this.fieldName = fieldName;
		this.value = new Integer(showField ? 1 : 0);
	}
	
	public FProjectionSimpleExpression(String fieldName, Object value){
		this.fieldName = fieldName;
		this.value = value;
	}

	@Override
	public DBObject toDBObject(AggregationOperationContext context) {
		if(value instanceof FIProjectionExpression){
			return new BasicDBObject(fieldName, ((FIProjectionExpression) value).toDBObject(context));
		} else{
			return new BasicDBObject(fieldName, value);
		}
	}

}
