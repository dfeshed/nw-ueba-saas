package fortscale.utils.monitoring.aspect;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;
import scala.collection.Iterator;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
public class CustomMetric implements PublicMetrics{

    private Collection<Metric<?>> result;

    public CustomMetric(){
        result = new LinkedHashSet<>();
    }

    public void addMetric(String metricName,int metricValue){
        if(result.contains(metricName)){
            java.util.Iterator<Metric<?>> itr = result.iterator();
            while(itr.hasNext()){
                Metric<?> metric =itr.next();
                if (metric.getName().equals(metricName)){
                    metric.increment(metricValue);
                    return;
                }
            }

        }
        else{
            result.add(new Metric<>(metricName, 1));
        }
    }

    public void addIncrementMetric(String metricName){
        addMetric(metricName,1);
    }
    public void addGaugeMetric(String metricName,int metricValue){
        addMetric(metricName,metricValue);
    }

    public void cleanMetrics(){
        result.clear();
    }


    @Override
    public Collection<Metric<?>> metrics() {
        return result;
    }


}
