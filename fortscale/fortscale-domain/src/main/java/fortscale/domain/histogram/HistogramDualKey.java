package fortscale.domain.histogram;

/**
 * Histogram dual key representation
 *
 * @author gils
 * Date: 05/08/2015
 */
public class HistogramDualKey implements HistogramKey {

    private String key1;
    private String key2;

    public HistogramDualKey(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    public String getKey1() {
        return key1;
    }

    public String getKey2() {
        return key2;
    }

    public String serialize(HistogramKeyVisitor histogramKeyVisitor) {
        return histogramKeyVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistogramDualKey that = (HistogramDualKey) o;

        if (key1 != null ? !key1.equals(that.key1) : that.key1 != null) return false;
        return !(key2 != null ? !key2.equals(that.key2) : that.key2 != null);

    }

    @Override
    public int hashCode() {
        int result = key1 != null ? key1.hashCode() : 0;
        result = 31 * result + (key2 != null ? key2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistogramDualKey{" +
                "key1='" + key1 + '\'' +
                ", key2='" + key2 + '\'' +
                '}';
    }
}