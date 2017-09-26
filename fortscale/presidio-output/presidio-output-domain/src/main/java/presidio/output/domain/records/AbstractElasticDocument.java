package presidio.output.domain.records;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by efratn on 20/08/2017.
 */
public abstract class AbstractElasticDocument {

    public static final String INDEX_NAME = "presidio-output";

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    @CreatedDate
    @Field(type = FieldType.String, store = true)
    private String createdDate;

    public AbstractElasticDocument() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = Instant.now().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}