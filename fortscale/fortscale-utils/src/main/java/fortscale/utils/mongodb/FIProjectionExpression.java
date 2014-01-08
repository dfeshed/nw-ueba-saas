package fortscale.utils.mongodb;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

public abstract class FIProjectionExpression implements AggregationOperation{

	@Override
	public String toString(){
		return toDBObject(Aggregation.DEFAULT_CONTEXT).toString();
	}
}
