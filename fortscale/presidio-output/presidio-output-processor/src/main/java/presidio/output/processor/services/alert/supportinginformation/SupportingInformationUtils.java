package presidio.output.processor.services.alert.supportinginformation;

import fortscale.common.general.Schema;
import fortscale.utils.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.config.AnomalyDescriptiorConfig;
import presidio.output.processor.config.AnomalyFiltersConfig;
import presidio.output.processor.config.IndicatorConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SupportingInformationUtils {
    private static final String SEPARATOR_CHARS = ",";

    private final EventPersistencyService eventPersistencyService;

    public SupportingInformationUtils(EventPersistencyService eventPersistencyService) {
        this.eventPersistencyService = eventPersistencyService;
    }

    public List<Pair<String, Object>> buildAnomalyFeatures(IndicatorConfig config, Map<String, String> contexts) {
        Schema schema = config.getSchema();
        AnomalyDescriptiorConfig anomalyDescriptiorConfig = config.getAnomalyDescriptior();
        List<Pair<String, Object>> anomalyFeatures = new ArrayList<>();

        // TODO: This if clause is temporary - Anomaly and filter key-value pairs should be added to the contexts map.
        if (anomalyDescriptiorConfig != null) {
            String anomalyField = anomalyDescriptiorConfig.getAnomalyField();
            String anomalyValues = anomalyDescriptiorConfig.getAnomalyValue();
            List<AnomalyFiltersConfig> anomalyFiltersConfigs = anomalyDescriptiorConfig.getAnomalyFilters();

            if (StringUtils.isNotEmpty(anomalyField) && StringUtils.isNotEmpty(anomalyValues)) {
                anomalyFeatures.addAll(buildAnomalyFeatures(schema, anomalyField, anomalyValues));
            }

            if (anomalyFiltersConfigs != null) {
                anomalyFiltersConfigs.stream()
                        .map(anomalyFiltersConfig -> buildAnomalyFeatures(schema, anomalyFiltersConfig))
                        .forEach(anomalyFeatures::addAll);
            }
        }

        if (contexts != null) {
            contexts.forEach((key, value) -> anomalyFeatures.add(buildAnomalyFeature(schema, key, value)));
        }

        return anomalyFeatures;
    }

    private List<Pair<String, Object>> buildAnomalyFeatures(Schema schema, String key, String values) {
        Class clazz = eventPersistencyService.findFeatureType(schema, key);
        return Stream.of(StringUtils.split(values, SEPARATOR_CHARS))
                .map(value -> Pair.of(key, ConversionUtils.convertToObject(value, clazz)))
                .collect(Collectors.toList());
    }

    private List<Pair<String, Object>> buildAnomalyFeatures(Schema schema, AnomalyFiltersConfig anomalyFiltersConfig) {
        return buildAnomalyFeatures(schema, anomalyFiltersConfig.getFieldName(), anomalyFiltersConfig.getFieldValue());
    }

    private Pair<String, Object> buildAnomalyFeature(Schema schema, String key, String value) {
        Class clazz = eventPersistencyService.findFeatureType(schema, key);
        return Pair.of(key, ConversionUtils.convertToObject(value, clazz));
    }
}
