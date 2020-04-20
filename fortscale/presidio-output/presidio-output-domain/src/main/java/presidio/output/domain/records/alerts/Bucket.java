package presidio.output.domain.records.alerts;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Bucket <K, V> {
    K key;
    V value;
    boolean anomaly = false;

    public Bucket() {

    }

    public Bucket(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public Bucket(K key, V value, boolean anomaly) {
        this(key, value);
        this.anomaly = anomaly;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {
        this.anomaly = anomaly;
    }
}
