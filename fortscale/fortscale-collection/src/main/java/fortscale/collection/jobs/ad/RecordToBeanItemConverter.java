package fortscale.collection.jobs.ad;

import org.apache.commons.beanutils.BeanUtils;
import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

import fortscale.utils.logging.Logger;

public class RecordToBeanItemConverter<T> {
	private static Logger logger = Logger.getLogger(RecordToBeanItemConverter.class);
	
	private String[] fields;
	
	public RecordToBeanItemConverter(String... fields) throws IllegalArgumentException {
		Assert.notEmpty(fields);
		
		this.fields = fields;
	}
	
	public void convert(Record record, T bean) throws InstantiationException, IllegalAccessException {
		if (record==null){ 
			return;
		}
		
		
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			try {
				BeanUtils.setProperty(bean, field, value);
			} catch (Exception e) {
				logger.debug("while converting Record to AdUser got an exception for the field {} with the value {}.", field, value);
				logger.debug("while converting Record to AdUser got an exception", e);
				//TODO: save statistics.
			}
		}
		
	}
}
