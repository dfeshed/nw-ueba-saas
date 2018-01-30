package fortscale.domain.core;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.time.Instant;


public abstract class AbstractAuditableDocument extends AbstractDocument {
    public static final String VERSION_FIELD_NAME = "version";
    public static final String LAST_MODIFIED_FIELD_NAME = "lastModified";
    public static final String CREATED_AT_FIELD_NAME = "createdAt";
    public static final String DATE_TIME_FIELD_NAME = "dateTime";
    private static final long serialVersionUID = -4585812347688862037L;

    @Field(DATE_TIME_FIELD_NAME)
    @NotNull
    @Indexed
    protected Instant dateTime;
    @Version
    @Field(VERSION_FIELD_NAME)
    private Long version;

    @CreatedDate
    @Field(CREATED_AT_FIELD_NAME)
    private Instant creationTime;

    @LastModifiedDate
    @Field(LAST_MODIFIED_FIELD_NAME)
    private Instant lastModified;

    public AbstractAuditableDocument(AbstractAuditableDocument other) {
        super(other);
        this.dateTime = other.dateTime;
        this.version = other.version;
    }

    public AbstractAuditableDocument() {

    }

    public AbstractAuditableDocument(Instant dateTime) {
        super();
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return super.toString() + ", " +
                "CreatedAt: " + getCreationTime() + ", " +
                "Version: " + getVersion() + ", " +
                "LastModified: " + getLastModified();
    }


    public Long getVersion() {
        return version;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }
}
