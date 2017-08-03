package presidio.monitoring.aspect.metrics;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import presidio.monitoring.elastic.records.PresidioMetric;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class PresidioCustomMetrics implements PublicMetrics{

    private Collection<PresidioMetric> applicationMetrics;

    public static Collection<PresidioMetric> customInMethodMetrics;

    public PresidioCustomMetrics(){
        applicationMetrics = new LinkedHashSet<>();
        customInMethodMetrics= new LinkedHashSet<>();
    }

    public static void addInMethodMetric(String metricName,double metricValue,Set tags,String unit){
        if(customInMethodMetrics.contains(metricName)){
            java.util.Iterator<PresidioMetric> itr = customInMethodMetrics.iterator();
            while(itr.hasNext()){
                PresidioMetric metric = itr.next();
                if (metric.getName().equals(metricName)){
                    metric.setValue(metricValue+metric.getValue());
                    return;
                }
            }

        }
        else{
            customInMethodMetrics.add(new PresidioMetric(metricName, metricValue, tags,unit));
        }
    }


    public void addMetric(String metricName,double metricValue,Set tags,String unit){
        if(applicationMetrics.contains(metricName)){
            java.util.Iterator<PresidioMetric> itr = applicationMetrics.iterator();
            while(itr.hasNext()){
                PresidioMetric metric = itr.next();
                if (metric.getName().equals(metricName)){
                    metric.setValue(metricValue+metric.getValue());
                    return;
                }
            }

        }
        else{
            applicationMetrics.add(new PresidioMetric(metricName, metricValue, tags,unit));
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
        return null;
    }

    public Collection<PresidioMetric> applicationMetrics() {
        if(customInMethodMetrics.isEmpty()){
            return applicationMetrics;
        }
        applicationMetrics.addAll(customInMethodMetrics);
        return applicationMetrics;
    }

}
