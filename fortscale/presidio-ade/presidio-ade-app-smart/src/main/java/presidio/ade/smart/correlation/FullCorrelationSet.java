package presidio.ade.smart.correlation;

import fortscale.smart.correlation.conf.FullCorrelation;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.utils.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FullCorrelationSet {

    private Map<String, FullCorrelation> featureToFullCorrelation;
    private static final Logger logger = Logger.getLogger(FullCorrelationSet.class);

    public FullCorrelationSet(List<FullCorrelation> fullCorrelations ) {
        featureToFullCorrelation = new HashMap<>();
        buildFeatureToFullCorrelationMap(fullCorrelations);
    }


    /**
     * Build feature to fullCorrelation map
     *
     * @param fullCorrelations fullCorrelations
     */
    private void buildFeatureToFullCorrelationMap(List<FullCorrelation> fullCorrelations) {
        for (FullCorrelation fullCorrelation : fullCorrelations) {
            fullCorrelation.getFeatures().forEach(feature -> {

                FullCorrelation featureFullCorrelation = featureToFullCorrelation.get(feature);
                if (featureFullCorrelation != null) {
                    String message = String.format(
                            "There should not be any intersection between full correlation features. " +
                                    "The feature %s can not belong to %s. it already exist in %s.", feature, fullCorrelation.getName(), featureFullCorrelation.getName());
                    throw new IllegalArgumentException(message);
                }

                featureToFullCorrelation.put(feature, fullCorrelation);
            });
        }
    }

    public FullCorrelation getFullCorrelation(String feature) {
        return featureToFullCorrelation.get(feature);
    }
}
