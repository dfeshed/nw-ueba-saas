package presidio.ade.smart.correlation;

/**
 * Created by maria_dorohin on 3/15/18.
 */
public class FeatureCorrelation {

    private String name;
    private Double score;
    private Double correlationFactor;
    private String TreeName;
    private String FullCorrelationName;


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
        return TreeName;
    }

    public void setTreeName(String treeName) {
        TreeName = treeName;
    }

    public String getFullCorrelationName() {
        return FullCorrelationName;
    }

    public void setFullCorrelationName(String fullCorrelationName) {
        FullCorrelationName = fullCorrelationName;
    }
}
