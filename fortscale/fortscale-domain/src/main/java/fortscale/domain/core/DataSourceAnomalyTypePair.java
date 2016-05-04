package fortscale.domain.core;
import java.util.List;

/**
 * Used to holds the parsed values of indicator types
 */
public class DataSourceAnomalyTypePair {

    private String dataSource;
    private String anomalyType;

    public DataSourceAnomalyTypePair(){

    }
    public DataSourceAnomalyTypePair (String dataSource, String anomalyType) {
        this.dataSource = dataSource;
        this.anomalyType = anomalyType;
    }


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceAnomalyTypePair)) return false;

        DataSourceAnomalyTypePair that = (DataSourceAnomalyTypePair) o;

        if (!dataSource.equals(that.dataSource)) return false;
        return anomalyType.equals(that.anomalyType);

    }

    @Override
    public int hashCode() {
        int result = dataSource.hashCode();
        result = 31 * result + anomalyType.hashCode();
        return result;
    }
}