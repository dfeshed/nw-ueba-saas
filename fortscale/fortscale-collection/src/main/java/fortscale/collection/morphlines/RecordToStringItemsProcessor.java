package fortscale.collection.morphlines;

import java.util.ArrayList;
import java.util.List;

import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;
import com.google.common.base.Joiner;

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
			
			List<Object> values = new ArrayList<Object>(fields.length);
			for (String field : fields) {
				values.add(record.getFirstValue(field));
			}
			
			String joined = Joiner.on(separator).skipNulls().join(values);
			if (joined==null || joined.isEmpty())
				return null;
			else
				return joined;
			
		} else {
			return null;
		}
	}
	

}
