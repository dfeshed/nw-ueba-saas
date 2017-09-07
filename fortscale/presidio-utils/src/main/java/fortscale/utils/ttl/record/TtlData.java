package fortscale.utils.ttl.record;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by maria_dorohin on 8/30/17.
 */
@Document(collection = "management_ttl")
public class TtlData {
    public static final String APPLICATION_NAME_FIELD = "applicationName";

    @Id
    private String id;
    @CreatedDate
    private Instant createdDate;
    @Indexed
    @Field(APPLICATION_NAME_FIELD)
    private String applicationName;
    @Field
    private String storeName;
    @Field
    private String collectionName;
    @Field
    private Duration ttlDuration;
    @Field
    private Duration cleanupInterval;


    public TtlData(String applicationName, String storeName, String collectionName, Duration ttlDuration, Duration cleanupInterval){
        this.applicationName = applicationName;
        this.storeName = storeName;
        this.collectionName = collectionName;
        this.ttlDuration = ttlDuration;
        this.cleanupInterval = cleanupInterval;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Duration getTtlDuration() {
        return ttlDuration;
    }

    public void setTtlDuration(Duration ttlDuration) {
        this.ttlDuration = ttlDuration;
    }

    public Duration getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
}
