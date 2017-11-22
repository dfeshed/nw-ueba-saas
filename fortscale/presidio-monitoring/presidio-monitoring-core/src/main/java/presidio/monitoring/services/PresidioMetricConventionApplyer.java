package presidio.monitoring.services;

import fortscale.utils.logging.Logger;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * Apply presidio metric structure convention to a givven metric.
 * Apply Presidio metric name convention-  <application_name>.<metricName> (+ ".system" for system metric)
 * <p>
 * Created by efratn on 21/11/2017.
 */
public class PresidioMetricConventionApplyer implements MetricConventionApplyer {

    private static final Logger logger = Logger.getLogger(PresidioMetricConventionApplyer.class);

    private static final String METRIC_NAME_SEPARATOR = ".";
    private static final String SYSTEM_METRIC_PREFIX = "system";
    private String applicationName;

    @Override
    public void apply(Metric metric) {
        applyMetricNameConvention(metric);
        applyAutoTagging(metric);

    }

    public PresidioMetricConventionApplyer(String applicationName) {
        this.applicationName = applicationName;
    }

    private void applyMetricNameConvention(Metric metric) {
        String origName = metric.getName();
        if (StringUtils.isEmpty(origName)) {
            logger.error("metric name cannot be empty");
            return;
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder transformedName = sb.append(applicationName).append(METRIC_NAME_SEPARATOR);

        Map<MetricEnums.MetricTagKeysEnum, String> tags = metric.getTags();
        if ((!MapUtils.isEmpty(metric.getTags())) && tags.containsKey(MetricEnums.MetricTagKeysEnum.IS_SYSTEM_METRIC)) {
            if (tags.get(MetricEnums.MetricTagKeysEnum.IS_SYSTEM_METRIC).equals(Boolean.TRUE.toString())) {
                transformedName = transformedName.append(SYSTEM_METRIC_PREFIX).append(METRIC_NAME_SEPARATOR);
            }
        }

        transformedName.append(origName);

        metric.setName(transformedName.toString());
        return;
    }

    private void applyAutoTagging(Metric metric) {
        Map<MetricEnums.MetricTagKeysEnum, String> metricTags = metric.getTags();
        metricTags.put(MetricEnums.MetricTagKeysEnum.PID, ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        metricTags.put(MetricEnums.MetricTagKeysEnum.APPLICATION_NAME, applicationName);
    }
}
