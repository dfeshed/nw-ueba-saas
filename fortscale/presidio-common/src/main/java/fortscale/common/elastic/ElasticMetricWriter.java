package fortscale.common.elastic;

import fortscale.utils.logging.Logger;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;


public class ElasticMetricWriter implements MetricWriter {

    private static final Logger logger = Logger.getLogger(ElasticMetricWriter.class);

    private MetricsEndpoint metricsEndpoint;
    public ElasticMetricWriter() {
    }
    public ElasticMetricWriter(MetricsEndpoint metricsEndpoint) {
        this.metricsEndpoint=metricsEndpoint;
    }

    /*  private ElasticExportService elasticExportService;

      public ElasticMetricWriter(ElasticExportService elasticExportService) {
          this.elasticExportService = elasticExportService;
      }
  */
    @Scheduled(fixedRate = 5000)
    public void test(){
        Map<String, Object> map = metricsEndpoint.invoke();
        for (Map.Entry<String, Object> entry :map.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue());
        }
    }

    @Override
    public void increment(Delta<?> delta) {
        System.out.println("increment");
    }

    @Override
    public void reset(String metricName) {
        System.out.println("reset");
    }

    @Override
    public void set(Metric<?> value) {

        System.out.println("set");
        logger.info("This metric is exported {} at time {} in milli", new StringBuilder(value.getName()).append(value.getValue()), System.currentTimeMillis());
        /*IndexQuery query = null;
        elasticExportService.export(query);
        */

    }


}
