package fortscale.collection.morphlines;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.kitesdk.morphline.api.Record;
import org.springframework.util.Assert;

import fortscale.utils.logging.Logger;

public class RecordToBeanItemConverter<T> {
	private static Logger logger = Logger.getLogger(RecordToBeanItemConverter.class);
	
	private List<String> fields;
	private fortscale.collection.metrics.RecordToBeanItemConverter metircs;

	public RecordToBeanItemConverter(T bean, String name, StatsService statsService	){
		initMetricsClass(statsService,name);
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
		metircs.record++;
		if (record==null){
			metircs.recordFailedBecauseEmpty++;
			return;
		}
		
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			try {
				BeanUtils.setProperty(bean, field, value);
			} catch (Exception e) {
				metircs.recordFailedBecausePropertyException++;
				logger.debug("while converting Record to {} got an exception for the field {} with the value {}.", bean.getClass(), field, value);
				logger.debug("while converting Record got an exception", e);
				//TODO: save statistics.
			}
		}
		
	}

	public void initMetricsClass(StatsService statsService, String name){

		metircs=new fortscale.collection.metrics.RecordToBeanItemConverter(statsService,name);
	}
}
