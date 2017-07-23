package presidio.monitoring.aspect.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class PresidioCustomMetrics implements PublicMetrics{

    private Collection<Metric<?>> result;

    private Collection<JsonObjectMetric<?>> applicationMetrics;

    public PresidioCustomMetrics(){
        result = new LinkedHashSet<>();
        applicationMetrics = new LinkedHashSet<>();
    }

    public <T extends Number>void addMetric(String metricName,T metricValue,Set tags,String unit){
        if(applicationMetrics.contains(metricName)){
            java.util.Iterator<JsonObjectMetric<?>> itr = applicationMetrics.iterator();
            while(itr.hasNext()){
                JsonObjectMetric<?> metric = itr.next();
                if (metric.getName().equals(metricName)){
                    metric.set(sumOfValues(metricValue,metric.getValue()));
                    return;
                }
            }

        }
        else{
            applicationMetrics.add(new JsonObjectMetric<>(metricName, metricValue, tags,unit));
        }
    }

    private <T extends Number> T sumOfValues(T value1 , T value2){
        if(value1 instanceof Integer) {
            Integer _result = (Integer) value1 + (Integer) value2;
            return (T) _result;
        }
        else if(value1 instanceof Double) {
            Double _result = (Double) value1 + (Double) value2;
            return (T) _result;
        }
        else {
            Long _result = (Long) value1 +(Long) value2;
            return (T) _result;
        }
    }


    @Override
    public Collection<Metric<?>> metrics() {
        return result;
    }

    public Collection<JsonObjectMetric<?>> applicationMetrics() {
        return applicationMetrics;
    }

    public void cleanMetrics(){
        result.clear();
    }


}
