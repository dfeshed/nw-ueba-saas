package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DataSourceScorerConfs {

    @JsonProperty("data-source")
    private String dataSource;

    @JsonProperty("scorers")
    private List<IScorerConf> scorerConfs;

    public DataSourceScorerConfs(@JsonProperty("data-source") String dataSource, @JsonProperty("scorers") List<IScorerConf> scorerConfs) {
        this.dataSource = dataSource;
        this.scorerConfs = scorerConfs;
    }

    public String getDataSource() {
        return dataSource;
    }

    public List<IScorerConf> getScorerConfs() {
        return scorerConfs;
    }
}
