package fortscale.ml.model.store;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;


public interface ModelWithFeatureOccurencesData extends Model {
    Double getFeatureCount(Feature feature);
    void setFeatureCount(Feature feature, double counter);
}
