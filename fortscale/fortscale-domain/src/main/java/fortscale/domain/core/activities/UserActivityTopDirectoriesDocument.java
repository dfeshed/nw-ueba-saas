package fortscale.domain.core.activities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document(collection = UserActivityTopDirectoriesDocument.COLLECTION_NAME)
@CompoundIndexes({@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserActivityTopDirectoriesDocument extends UserActivityDocument {

    public static final String COLLECTION_NAME = "user_activity_top_directories";
    public static final String DIRECTORIES_FIELD_NAME = "directories";
    public static final String DIRECTORIES_HISTOGRAM_FIELD_NAME = DIRECTORIES_FIELD_NAME + "Histogram";


    @Field(DIRECTORIES_FIELD_NAME)
    private Directories directories = new Directories();

    public Directories getDirectories() {
        return directories;
    }

    public void setDirectories(Directories directories) {
        this.directories = directories;
    }

    @Override
    public Map<String, Double> getHistogram() {
        return getDirectories().getDirectoriesHistogram();
    }

    public static class Directories {

        @Field(DIRECTORIES_HISTOGRAM_FIELD_NAME)
        private Map<String, Double> directoriesHistogram = new HashMap<>();

        public Map<String, Double> getDirectoriesHistogram() {
            return directoriesHistogram;
        }

        public void setDirectoriesHistogram(Map<String, Double> directoriesHistogram) {
            this.directoriesHistogram = directoriesHistogram;
        }
    }
}
