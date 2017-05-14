package fortscale.domain.core.activities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityClassificationExposureDocument.COLLECTION_NAME)
@CompoundIndexes({@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityClassificationExposureDocument extends UserActivityDocument {

    public static final String COLLECTION_NAME = "user_activity_classification_exposure";
    public static final String CLASSIFICATION_EXPOSURE_FIELD_NAME = "classificationExposure";
    public static final String CLASSIFICATION_EXPOSURE_HISTOGRAM_FIELD_NAME = CLASSIFICATION_EXPOSURE_FIELD_NAME + "Histogram";
    public static final String FIELD_NAME_HISTOGRAM_EVENT_COUNTER = "eventCounter";
    public static final String FIELD_NAME_HISTOGRAM_WAS_CLASSIFIED = "wasClassified";


    @Field(CLASSIFICATION_EXPOSURE_FIELD_NAME)
    private ClassificationExposure classificationExposure = new ClassificationExposure();

    public ClassificationExposure getClassificationExposure() {
        return classificationExposure;
    }

    public void setClassificationExposure(ClassificationExposure classificationExposure) {
        this.classificationExposure = classificationExposure;
    }

    @Override
    public Map<String, Double> getHistogram() {
        return getClassificationExposure().getDirectoriesHistogram();
    }

    public static class ClassificationExposure {

        @Field(CLASSIFICATION_EXPOSURE_HISTOGRAM_FIELD_NAME)
        private Map<String, Double> directoriesHistogram = new HashMap<>();

        public Map<String, Double> getDirectoriesHistogram() {
            return directoriesHistogram;
        }

        public void setDirectoriesHistogram(Map<String, Double> directoriesHistogram) {
            this.directoriesHistogram = directoriesHistogram;
        }
    }
}
