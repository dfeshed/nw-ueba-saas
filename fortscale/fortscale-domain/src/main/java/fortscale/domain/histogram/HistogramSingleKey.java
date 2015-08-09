package fortscale.domain.histogram;

import java.util.ArrayList;
import java.util.List;

/**
 * a histogram key with one key. usage example: histogram
 * @author gils
 *         Date: 05/08/2015
 */
public class HistogramSingleKey implements HistogramKey {

    private String key;

    public HistogramSingleKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * generates key representation to json serialization - in this case a list of one key.
     * @return
     */
    @Override
    public List<String> generateKey(){

        List<String> genKey = new ArrayList<>();
        genKey.add(key);
        return genKey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistogramSingleKey that = (HistogramSingleKey) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "HistogramSingleKey{" +
                "key='" + key + '\'' +
                '}';
    }
}