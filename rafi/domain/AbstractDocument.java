package domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.Instant;

@Document
public class AbstractDocument implements Serializable {

    public static final String DATE_TIME_FIELD_NAME = "dateTime";
    public static final String BODY_FIELD_NAME = "body";

    @Id
    private String id;

    @Field(DATE_TIME_FIELD_NAME)
    private Instant dateTime;

    @Field(BODY_FIELD_NAME)
    private String body;

    public String getId() {
        return id;
    }

    public Instant getDateTime() {
        return dateTime;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
