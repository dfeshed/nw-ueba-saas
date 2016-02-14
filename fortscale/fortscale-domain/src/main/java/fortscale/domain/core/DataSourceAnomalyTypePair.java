package fortscale.domain.core;
import java.util.List;

/**
 * Used to holds the parsed values of indicator types
 */
public class DataSourceAnomalyTypePair {
    public String getDataSourceId() {
        return dataSourceId;
    }

    public List<String> getAnomalyTypes() {
        return anomalyTypes;
    }

    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public void setAnomalyTypes(List<String> anomalyTypes) {
        this.anomalyTypes = anomalyTypes;
    }

    public void addAnomalyType(String anomalyType) {
        this.anomalyTypes.add(anomalyType);
    }

    private String dataSourceId;
    private List<String> anomalyTypes;

    public DataSourceAnomalyTypePair (String dataSourceId, List<String> anomalyTypes) {
        this.dataSourceId = dataSourceId;
        this.anomalyTypes = anomalyTypes;
    }
}