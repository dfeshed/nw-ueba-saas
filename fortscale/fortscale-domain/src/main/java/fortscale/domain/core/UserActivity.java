package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = fortscale.domain.core.UserActivity.COLLECTION_NAME)
public class UserActivity {
    private static final long serialVersionUID = 4314533976059073711L;

    public static final String COLLECTION_NAME = "user_activity";
    public static final String KEY_FIELD_NAME = "key";
    public static final String VALUE_FIELD_NAME = "value";

    @Field(KEY_FIELD_NAME)
    @Indexed(unique=true)
    private String key;

    private String value;

    public UserActivity() {
    }

    public UserActivity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


