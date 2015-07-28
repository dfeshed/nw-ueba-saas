package fortscale.streaming;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.Partition;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.task.TaskContext;

public class ExtendedSamzaTaskContext implements TaskContext{

	private TaskContext taskContext;
	private Map<Class<?>, Object> beanMap = new HashMap<Class<?>, Object>();
	
	public ExtendedSamzaTaskContext(TaskContext taskContext){
		this.taskContext = taskContext;
	}
	
	public void registerBean(Class<?> beanType, Object bean){
		beanMap.put(beanType, bean);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T resolve(Class<T> requiredType) {
		return (T) beanMap.get(requiredType);
	}

	@Override
	public MetricsRegistry getMetricsRegistry() {
		return taskContext.getMetricsRegistry();
	}

	@Override
	public Partition getPartition() {
		return taskContext.getPartition();
	}

	@Override
	public Object getStore(String name) {
		return taskContext.getStore(name);
	}
	
	
}
