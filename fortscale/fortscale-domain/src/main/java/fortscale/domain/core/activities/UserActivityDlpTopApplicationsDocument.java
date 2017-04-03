package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityDlpTopApplicationsDocument.COLLECTION_NAME)
@CompoundIndexes({@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityDlpTopApplicationsDocument extends UserActivityDocument {

    public static final String COLLECTION_NAME = "user_activity_dlp_top_applications";
    public static final String APPLICATION_FIELD_NAME = "applications";
    public static final String APPLICATION_HISTOGRAM_FIELD_NAME = APPLICATION_FIELD_NAME + "Histogram";


    @Field(APPLICATION_FIELD_NAME)
    private Applications applications = new Applications();

    public Applications getApplications() {
        return applications;
    }

    public void setApplications(Applications applications) {
        this.applications = applications;
    }

    @Override
    public Map<String, Double> getHistogram() {
        return getApplications().getApplicationsHistogram();
    }

    public static class Applications {

        @Field(APPLICATION_HISTOGRAM_FIELD_NAME)
        private Map<String, Double> applicationsHistogram = new HashMap<>();

        public Map<String, Double> getApplicationsHistogram() {
            return applicationsHistogram;
        }

        public void setApplicationsHistogram(Map<String, Double> applicationsHistogram) {
            this.applicationsHistogram = applicationsHistogram;
        }
    }
}
