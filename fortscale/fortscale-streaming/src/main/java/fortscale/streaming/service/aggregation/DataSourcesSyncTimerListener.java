package fortscale.streaming.service.aggregation;

import java.util.List;

public interface DataSourcesSyncTimerListener {
	public void dataSourcesReachedTime(List<String> dataSources, long epochtime);
}
