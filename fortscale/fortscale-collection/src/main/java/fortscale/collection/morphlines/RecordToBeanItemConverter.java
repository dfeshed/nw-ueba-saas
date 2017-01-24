package fortscale.collection.morphlines;

import fortscale.collection.metrics.RecordToBeanItemConverterMetric;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordToBeanItemConverter<T> {
	private static Logger logger = Logger.getLogger(RecordToBeanItemConverter.class);
	private final String name;

	private List<String> fields;
	private RecordToBeanItemConverterMetric metrics;

	@Autowired
	private StatsService statsService;

	public RecordToBeanItemConverter(T bean, String name){
		this.name=name;
		fields = new ArrayList<>();
		for(PropertyDescriptor propertyDescriptor: PropertyUtils.getPropertyDescriptors(bean.getClass())){
			String fieldName = propertyDescriptor.getName();
			if(fieldName.equals("class")){
				continue;
			}
			fields.add(fieldName);
		}
	}
	public RecordToBeanItemConverter(String name, StatsService statsService, String... fields) throws IllegalArgumentException {
		Assert.notEmpty(fields);
		this.fields = Arrays.asList(fields);
		this.name=name;
	}
	
	public void convert(Record record, T bean) throws InstantiationException, IllegalAccessException {
		getMetrics().record++;
		if (record==null){
			getMetrics().recordFailedBecauseEmpty++;
			return;
		}
		
		for (String field : fields) {
			Object value = record.getFirstValue(field);
			try {
				BeanUtils.setProperty(bean, field, value);
			} catch (Exception e) {
				getMetrics().recordFailedBecausePropertyException++;
				logger.debug("while converting Record to {} got an exception for the field {} with the value {}.", bean.getClass(), field, value);
				logger.debug("while converting Record got an exception", e);
				//TODO: save statistics.
			}
		}
		
	}

	private RecordToBeanItemConverterMetric getMetrics() {
		if (metrics == null) {
			metrics = new RecordToBeanItemConverterMetric(statsService, name);
		}
		return metrics;
	}
}
