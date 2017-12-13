package fortscale.ml.model.metrics;


import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.Map;


public interface IModelMetricsContainer {

     String getFactoryName();

     void addTags(Map<MetricEnums.MetricTagKeysEnum, String> tags);

     void setLogicalStartTime(Instant logicalStartTime);

     void setNumOfContexts(int numOfContexts);

     void flush();
}
