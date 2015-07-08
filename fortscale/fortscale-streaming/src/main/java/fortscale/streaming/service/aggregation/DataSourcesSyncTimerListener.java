package fortscale.streaming.service.aggregation;
import java.util.List;
public interface DataSourcesSyncTimerListener {
    public void dataSourcesReachedTime(long epochtime);
}