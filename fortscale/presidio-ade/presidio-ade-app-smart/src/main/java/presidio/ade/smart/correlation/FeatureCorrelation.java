package presidio.ade.smart.correlation;

/**
 * Created by maria_dorohin on 3/15/18.
 */
public class FeatureCorrelation {

    private String name;
    private Double score;
    private Double correlationFactor;
    private String treeName;
    private String fullCorrelationName;


    public FeatureCorrelation(String name, Double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public Double getScore() {
        return score;
    }

    public Double getCorrelationFactor() {
        return correlationFactor;
    }

    public void setCorrelationFactor(Double correlationFactor) {
        this.correlationFactor = correlationFactor;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public String getFullCorrelationName() {
        return fullCorrelationName;
    }

    public void setFullCorrelationName(String fullCorrelationName) {
        this.fullCorrelationName = fullCorrelationName;
    }
}
