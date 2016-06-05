package fortscale.streaming.service.config;

/**
 * key that allows to each streaming task to set its behavior according to data source and previous state (mostly previous task).
 * this enables to support several data sources on the same topic.
 * @author gils
 * Date: 11/11/2015
 */
public class StreamingTaskDataSourceConfigKey {
    private String dataSource;
    private String lastState;

    public StreamingTaskDataSourceConfigKey(String dataSource, String lastState) {
        this.dataSource = dataSource;
        this.lastState = lastState;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getLastState() {
        return lastState;
    }

    public String getConfigKeyStr(){
        return dataSource +"_" + lastState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamingTaskDataSourceConfigKey that = (StreamingTaskDataSourceConfigKey) o;

        if (dataSource != null ? !dataSource.equals(that.dataSource) : that.dataSource != null) return false;
        return !(lastState != null ? !lastState.equals(that.lastState) : that.lastState != null);

    }

    @Override
    public int hashCode() {
        int result = dataSource != null ? dataSource.hashCode() : 0;
        result = 31 * result + (lastState != null ? lastState.hashCode() : 0);
        return result;
    }
    @Override public String toString() {
        return getDataSource() + "/" + getLastState();
    }

    public String getMetricsName() {
        String name = getDataSource() + "/" + getLastState();
        return name;

    }
}
