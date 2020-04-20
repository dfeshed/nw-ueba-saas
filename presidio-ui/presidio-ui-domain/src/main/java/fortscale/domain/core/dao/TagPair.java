package fortscale.domain.core.dao;

/**
 * Created by Amir Keren on 09/09/15.
 *
 * This POJO class is used as the result of the api call usersTagsCount in user controller
 */
public class TagPair {

    private String key;
    private long value;

    public TagPair() {}

    public TagPair(String key, long value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}