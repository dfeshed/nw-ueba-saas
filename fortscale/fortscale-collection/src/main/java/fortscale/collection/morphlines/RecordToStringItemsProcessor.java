package fortscale.collection.morphlines;

import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

import com.eclipsesource.json.JsonObject;


/**
 * Converts morphline record into string line
 */
public class RecordToStringItemsProcessor {

	private String[] fields;
	private String separator;
	
	public RecordToStringItemsProcessor(String separator, String... fields) throws IllegalArgumentException {
		Assert.notNull(separator);
		Assert.notNull(fields);
		Assert.notEmpty(fields);
		
		this.fields = fields;
		this.separator = separator;
	}
	
	public String process(Record record) {
		if (record==null) 
			return null;
		
		boolean firstItem = true;
		boolean noValues = true;
		StringBuilder sb = new StringBuilder();
		for (String field : fields) {
			if (!firstItem) {
				sb.append(separator);
			}
		
			Object value = record.getFirstValue(field);
			if (value!=null) {
				sb.append(value.toString().trim());
				noValues = false;
			}
			
			firstItem = false;
		}
		
		if (noValues)
			return null;
		else
			return sb.toString();
	}
	
	public String toJSON(Record record) {
		if (record==null)
			return null;
			
		JsonObject json = new JsonObject();
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			if (value!=null) {
				if (value instanceof Boolean)
					json.add(field, (boolean)value);
				else if (value instanceof Double)
					json.add(field, (double)value);
				else if (value instanceof Float)
					json.add(field, (float)value);
				else if (value instanceof Integer)
					json.add(field, (int)value);
				else if (value instanceof Long)
					json.add(field, (long)value);
				else if (value instanceof String)
					json.add(field, (String)value);
				else
					json.add(field, value.toString());				
			}
		}
		return json.toString();
	}
}
