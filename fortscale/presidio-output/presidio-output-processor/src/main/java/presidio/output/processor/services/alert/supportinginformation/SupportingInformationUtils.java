package presidio.output.processor.services.alert.supportinginformation;

import fortscale.common.general.Schema;
import fortscale.utils.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.AnomalyFiltersConfig;
import presidio.output.processor.config.IndicatorConfig;

import java.util.ArrayList;
import java.util.List;

public class SupportingInformationUtils {

    public static final String SEPARATOR_CHARS = ",";

    EventPersistencyService eventPersistencyService;

    public SupportingInformationUtils(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    public List<Pair<String, Object>> buildAnomalyFeatures(IndicatorConfig config) {
        return buildAnomalyFeatures(config, null);
    }

    public List<Pair<String, Object>> buildAnomalyFeatures(IndicatorConfig config, String anomalyIndicatorValue) {

        List<Pair<String, Object>> features = new ArrayList<>();

        String anomalyField = config.getAnomalyDescriptior()!=null? config.getAnomalyDescriptior().getAnomalyField(): null;
        String anomalyValue = anomalyIndicatorValue;
        // if anomaly value is specified explicitly use it, otherwise use the anomaly value from configuration
        if (anomalyValue == null && config.getAnomalyDescriptior() != null ) {
            anomalyValue = config.getAnomalyDescriptior().getAnomalyValue();
        }
        features.addAll(buildFeaturePairs(anomalyField, anomalyValue, config.getSchema()));

        List<AnomalyFiltersConfig> anomalyFiltersConfigs = config.getAnomalyDescriptior().getAnomalyFilters();

        if(anomalyFiltersConfigs!=null) {
            anomalyFiltersConfigs.forEach(anomalyFiltersConfig -> {
                String anomalyFilterField = anomalyFiltersConfig.getFieldName();
                String anomalyFilterValue = anomalyFiltersConfig.getFieldValue();
                features.addAll(buildFeaturePairs(anomalyFilterField, anomalyFilterValue, config.getSchema()));
            });
        }

        return features;
    }

    private List<Pair<String, Object>> buildFeaturePairs(String field, String values, Schema schema) {
        List<Pair<String, Object>> features = new ArrayList<>();
        if (StringUtils.isNoneEmpty(field, values)) {
            for (String value : StringUtils.split(values, SEPARATOR_CHARS)) {
                Object featureValue = ConversionUtils.convertToObject(value, eventPersistencyService.findFeatureType(schema, field));
                features.add(Pair.of(field, featureValue));
            }
        }
        return features;
    }

}
