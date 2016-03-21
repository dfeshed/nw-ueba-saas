package fortscale.ml.scorer;

import fortscale.common.feature.extraction.FeatureExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

@Configurable(preConstruction = true)
abstract public class AbstractScorer implements Scorer {

    private String name;

    @Autowired
    FeatureExtractService featureExtractService;

    public AbstractScorer(String name) {
        Assert.hasText(name, "scorer name must be provided and cannot be null, empty or blank");
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
