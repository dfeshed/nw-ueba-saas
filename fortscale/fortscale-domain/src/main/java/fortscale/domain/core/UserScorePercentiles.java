package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 29/05/2016.
 */

@Document(collection = UserScorePercentiles.COLLECTION_NAME)
public class UserScorePercentiles extends AbstractDocument implements Serializable {

    private static final long serialVersionUID = -8514041678913795872L;


    public static final String COLLECTION_NAME = "UserScorePercentiles";
    public static final String timestampField = "timestamp";
    public static final String userScorePercentileListField = "userScorePercentileList";
    public static final String expirationTimeField = "expirationTime";
    public static final String activeField = "active";


    @Field(timestampField)
    private long timestamp;

    @Field(userScorePercentileListField)
    private Collection<UserSingleScorePercentile> userScorePercentileCollection;

    @Field(expirationTimeField)
    private Long expirationTime = null;

    @Field(activeField)
    private  boolean active;

    public UserScorePercentiles() {
    }

    public UserScorePercentiles(long timestamp, Collection<UserSingleScorePercentile> userScorePercentileCollection, boolean active) {
        this.timestamp = timestamp;
        this.userScorePercentileCollection = userScorePercentileCollection;
        this.expirationTime = null;
        this.active = active;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Collection<UserSingleScorePercentile> getUserScorePercentileCollection() {
        return userScorePercentileCollection;
    }

    public void setUserScorePercentileCollection(Collection<UserSingleScorePercentile> userScorePercentileCollection) {
        this.userScorePercentileCollection = userScorePercentileCollection;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public boolean isActive() {
        return active;
    }


    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
