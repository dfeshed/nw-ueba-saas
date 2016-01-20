package fortscale.ml.scorer;

import fortscale.common.feature.extraction.FeatureExtractService;
import org.apache.commons.lang3.StringUtils;
import org.apache.derby.iapi.util.StringUtil;
import org.eclipse.jdt.internal.core.Assert;
import org.springframework.beans.factory.annotation.Autowired;


abstract public class AbstractScorer implements Scorer {

    private String name;

    @Autowired
    FeatureExtractService featureExtractService;

    public AbstractScorer(String name) {
        Assert.isTrue(StringUtils.isNotEmpty(name) && StringUtils.isNotBlank(name), "scorer name must be provided and cannot be null, blank or empty");
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
