package presidio.output.domain.records;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
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
    @Field(type = FieldType.Date, store = true)
    private Date createdDate;

    public AbstractElasticDocument(String id, Date createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public AbstractElasticDocument() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}