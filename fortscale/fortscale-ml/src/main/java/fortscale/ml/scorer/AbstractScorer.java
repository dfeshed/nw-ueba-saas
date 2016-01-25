package fortscale.ml.scorer;

import fortscale.common.feature.extraction.FeatureExtractService;
import org.springframework.beans.factory.annotation.Autowired;


abstract public class AbstractScorer implements Scorer {

    private String name;

    @Autowired
    FeatureExtractService featureExtractService;

    public AbstractScorer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
