package fortscale.collection.morphlines;

import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

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
	
	public String process(Record item) {
		if (item==null) 
			return null;
		
		if (item instanceof Record) {
			Record record = (Record)item;
			
			boolean firstItem = true;
			boolean noValues = true;
			StringBuilder sb = new StringBuilder();
			for (String field : fields) {
				if (!firstItem) {
					sb.append(separator);
				}
			
				Object value = record.getFirstValue(field);
				if (value!=null) {
					sb.append(value.toString());
					noValues = false;
				}
				
				firstItem = false;
			}
			
			if (noValues)
				return null;
			else
				return sb.toString();
			
		} else {
			return null;
		}
	}
	

}
