package fortscale.common.datastructures;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.common.feature.FeatureValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class GenericHistogram implements Serializable, FeatureValue {
    private static final long serialVersionUID = 1L;
    public static final String FEATURE_VALUE_TYPE = "generic_histogram";

    private Map<String, Double> histogram = new HashMap<>();
    private double totalCount = 0;
    private Object maxObject = null;

    public GenericHistogram() {}

    public double getAvg() {
        return totalCount /getN();
    }

    public double getPopulationStandardDeviation() {
        return calculateHistogramStd(true);
    }

    public double getStandardDeviation() {
        return calculateHistogramStd(false);
    }

    public long getN() {
        return histogram.size();
    }
    
    public double getTotalCount() {
		return totalCount;
	}

	public Double get(Object key) {
        return histogram.get(key.toString());
    }
    public Double get(String key) {
        return histogram.get(key);
    }

    public Set<String> getObjects() {
        return histogram.keySet();
    }

    public Double getMaxCount() { return histogram.get(maxObject);}
    public Object getMaxCountObject() { return maxObject;}
    public Double getMaxCountFromTotalCount() { return getMaxCount()/ totalCount;}

    public void add(Object val, Double count) { add(val.toString(), count); }
    public void add(String val, Double count) {
        if(val == null || count == null){
            return;
        }

        Double oldCount = histogram.get(val);
        Double newValCount = oldCount!=null ? count + oldCount : count;

        histogram.put(val, newValCount);

        if(maxObject==null) {
            maxObject = val;
        } else {
            Double maxCount = histogram.get(maxObject);
            if(maxCount==null) {
                maxObject = val;
            } else if(maxCount<newValCount) {
                maxObject = val;
            }
        }

        this.totalCount +=count;

    }

    public GenericHistogram add(GenericHistogram histogram) {
        for (String key : histogram.getObjects()) {
            Double count = histogram.get(key);
            add(key, count);
        }
        return this;
    }

    private Double calculateHistogramStd(boolean populationStandardDeviation){
        Double sum = 0.0;
        Double avg = getAvg();
        Iterator<Double> iter = histogram.values().iterator();
        while(iter.hasNext()){
            Double value = iter.next();
            sum += Math.pow(value - avg, 2);
        }
        if(populationStandardDeviation) {
            return Math.sqrt(sum/getN());
        } else {
            return Math.sqrt(sum / (getN() - 1));
        }
    }

    public Map<String, Double> getHistogramMap() {
        return histogram;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{ ");
        sb.append("totalCount: ").append(totalCount).append(", ");
        sb.append(", histogram: {");

        Iterator<Map.Entry<String, Double>> iter = histogram.entrySet().iterator();

        if(iter.hasNext()) {
            Map.Entry<String, Double> entry = iter.next();
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        while(iter.hasNext()) {
            Map.Entry<String, Double> entry = iter.next();
            sb.append(", ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        sb.append("}}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericHistogram histogram1 = (GenericHistogram) o;

        if (Math.abs(histogram1.totalCount - totalCount) > 0.0000000001) return false;
        if (!histogram.equals(histogram1.histogram)) return false;
        return maxObject.equals(histogram1.maxObject);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = histogram.hashCode();
        temp = Double.doubleToLongBits(totalCount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + maxObject.hashCode();
        return result;
    }
}
