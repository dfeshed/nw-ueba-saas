package fortscale.ml.model.metrics;


import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.time.Instant;
import java.util.Map;


public interface IModelMetricsContainer {

    /**
     * @return factory name of model conf
     */
    String getFactoryName();

    /**
     * Add tags to specific model metrics container
     * @param tags
     */
    void addTags(Map<MetricEnums.MetricTagKeysEnum, String> tags);

    /**
     * Set logical time
     * @param logicalStartTime
     */
    void setLogicalTime(Instant logicalStartTime);

    /**
     * Set num of contexts
     * @param numOfContexts
     */
    void setNumOfContexts(int numOfContexts);

    void flush();
}
