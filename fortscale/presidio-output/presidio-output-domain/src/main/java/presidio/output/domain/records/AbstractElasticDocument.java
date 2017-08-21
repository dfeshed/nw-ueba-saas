package presidio.output.domain.records;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


/**
 * Created by efratn on 20/08/2017.
 */
public abstract class AbstractElasticDocument {

    @Id
    @Field(type = FieldType.String, store = true)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
