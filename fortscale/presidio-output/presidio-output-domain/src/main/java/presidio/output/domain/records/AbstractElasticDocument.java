package presidio.output.domain.records;

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
    protected String id;

    @Field(type = FieldType.Date, store = true)
    private Date createdDate;

    @Field(type = FieldType.Date, store = true)
    private Date updatedDate;

    @Field(type = FieldType.String, store = true)
    private String updatedBy;

    public AbstractElasticDocument(String id, Date createdDate, Date updatedDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.updatedBy = Thread.currentThread().getName();
    }

    public AbstractElasticDocument() {
        this.id = UUID.randomUUID().toString();
        Date date = new Date();
        this.createdDate = date;
        this.updatedDate = date;
        this.updatedBy = Thread.currentThread().getName();
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void updateFieldsBeforeSave() {
        Date date = new Date();
        String updatedBy = Thread.currentThread().getName();
        setUpdatedDate(date);
        setUpdatedBy(updatedBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractElasticDocument that = (AbstractElasticDocument) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}