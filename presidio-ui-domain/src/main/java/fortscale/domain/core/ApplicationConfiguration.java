package fortscale.domain.core;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = ApplicationConfiguration.COLLECTION_NAME)
public class ApplicationConfiguration extends AbstractDocument {

    private static final long serialVersionUID = 4314533976059073710L;

    public static final String COLLECTION_NAME = "application_configuration";
    public static final String KEY_FIELD_NAME = "key";
    public static final String VALUE_FIELD_NAME = "value";

    @Field(KEY_FIELD_NAME)
    @Indexed(unique=true)
    private String key;

    private String value;

    public ApplicationConfiguration() {
    }

    public ApplicationConfiguration(String key, String value) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationConfiguration)) return false;
        if (!super.equals(o)) return false;

        ApplicationConfiguration that = (ApplicationConfiguration) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}
