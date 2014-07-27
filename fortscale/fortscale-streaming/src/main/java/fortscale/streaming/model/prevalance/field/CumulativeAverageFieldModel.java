package fortscale.streaming.model.prevalance.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.streaming.model.prevalance.FieldModel;

/**
 * Keeps a cumulative average for field values, the field score is the current average
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class CumulativeAverageFieldModel implements FieldModel{

	private static final Logger logger = LoggerFactory.getLogger(CumulativeAverageFieldModel.class);
	
	private int count;
	private double average;
	
	@Override
	public void add(Object value, long timestamp) {
		if (value instanceof Integer || value instanceof Long) {
			int valN = (int)value;
			
			// advance average
			average = ((double) valN / (count + 1)) + (average / (count + 1))*count;
			count++;
			
			// prevent integer oveflow
			if (count > Integer.MAX_VALUE * 0.8)
				count = count / 2;
			
		} else {
			logger.warn("given field value {} (type={}) is not valid for average", value, (value==null)? "null" : value.getClass());
		}
		
	}

	@Override
	public double calculateScore(Object value) {
		return average;
	}

	@Override
	public boolean shouldAffectEventScore() {
		return false;
	}
}
