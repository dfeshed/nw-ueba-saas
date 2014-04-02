package fortscale.collection.morphlines;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

import fortscale.utils.logging.Logger;

public class RecordToBeanItemConverter<T> {
	private static Logger logger = Logger.getLogger(RecordToBeanItemConverter.class);
	
	private List<String> fields;
	
	public RecordToBeanItemConverter(T bean){
		fields = new ArrayList<>();
		for(PropertyDescriptor propertyDescriptor: PropertyUtils.getPropertyDescriptors(bean.getClass())){
			String fieldName = propertyDescriptor.getName();
			if(fieldName.equals("class")){
				continue;
			}
			fields.add(fieldName);
		}
	}
	public RecordToBeanItemConverter(String... fields) throws IllegalArgumentException {
		Assert.notEmpty(fields);
		
		this.fields = Arrays.asList(fields);
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
