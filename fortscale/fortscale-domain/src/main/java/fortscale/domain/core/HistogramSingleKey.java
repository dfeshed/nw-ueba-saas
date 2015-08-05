package fortscale.domain.core;

/**
 * @author gils
 *         Date: 05/08/2015
 */
public class HistogramSingleKey implements HistogramKey {

    String key;

    public HistogramSingleKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
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