package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import presidio.input.core.RawEventsPageIterator;
import presidio.input.core.services.converters.ConverterService;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.transformation.managers.TransformationService;
import presidio.monitoring.aspect.annotations.RunTime;
import presidio.monitoring.records.Metric;
import presidio.monitoring.sdk.api.services.enums.MetricEnums;
import presidio.monitoring.services.MetricCollectingService;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputCoreManager {

    private static final Logger logger = Logger.getLogger(InputCoreManager.class);

    private final int DEFAULT_PAGE_SIZE = 1000;
    private final String LAST_EVENT_TIME_PROCESSED_METRIC_NAME = "last.event.time.processed.input";
    private final String TOTAL_EVENTS_PROCESSED_METRIC_NAME = "total.events.processed.input";

    private final PresidioInputPersistencyService persistencyService;
    private final AdeDataService adeDataService;
    private final OutputDataServiceSDK outputDataServiceSDK;
    private final TransformationService transformationService;
    private final ConverterService converterService;

    @Autowired
    MetricCollectingService metricCollectingService;

    @Value("${page.iterator.page.size}")
    private Integer pageSize;

    public InputCoreManager(PresidioInputPersistencyService persistencyService, AdeDataService adeDataService,
                            OutputDataServiceSDK outputDataServiceSDK, TransformationService transformationService, ConverterService converterService) {
        this.persistencyService = persistencyService;
        this.adeDataService = adeDataService;
        this.outputDataServiceSDK = outputDataServiceSDK;
        this.transformationService = transformationService;
        this.converterService = converterService;
    }

    @RunTime
    public void run(Schema schema, Instant startDate, Instant endDate) {
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        RawEventsPageIterator rawEventsPageIterator = new RawEventsPageIterator(startDate, endDate, persistencyService, schema, pageSize);
        List transformedEvents = null;
        List nextEvents = null;
        Map<MetricEnums.MetricTagKeysEnum, String> tags = new HashMap();
        tags.put(MetricEnums.MetricTagKeysEnum.SCHEMA, schema.toString());
        while (rawEventsPageIterator.hasNext()) {
            try {
                nextEvents = rawEventsPageIterator.next();

                logger.debug("Processing {} events", nextEvents.size());

                transformedEvents = transformationService.run(nextEvents, schema);

                try {
                    storeToAde(schema, startDate, endDate, transformedEvents);
                    storeToOutput(transformedEvents, schema);
                } catch (Exception e) {
                    logger.error("Error storing transformed data , number of transformed events: {} ", transformedEvents != null ? transformedEvents.size() : 0, e);
                }
            } catch (IllegalArgumentException ex) {
                logger.error("Error reading events from repository.", ex);
            } finally {
                metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(TOTAL_EVENTS_PROCESSED_METRIC_NAME).
                        setMetricValue(transformedEvents != null ? transformedEvents.size() : 0).
                        setMetricTags(tags).
                        setMetricUnit(MetricEnums.MetricUnitType.NUMBER).
                        setMetricLogicTime(startDate).
                        build());
            }
        }
        if (CollectionUtils.isNotEmpty(nextEvents)) {
            long time = ((AbstractInputDocument) nextEvents.get(nextEvents.size() - 1)).getDateTime().toEpochMilli();
            metricCollectingService.addMetric(new Metric.MetricBuilder().setMetricName(LAST_EVENT_TIME_PROCESSED_METRIC_NAME).
                    setMetricValue(time).
                    setMetricTags(tags).
                    setMetricUnit(MetricEnums.MetricUnitType.MILLI_SECOND).
                    setMetricLogicTime(startDate).
                    build());
        }
    }

    private void storeToOutput(List<? extends AbstractInputDocument> transformedEvents, Schema schema) throws Exception {
        outputDataServiceSDK.store(schema, converterService.convertToOutput(transformedEvents, schema));
    }

    private void storeToAde(Schema schema, Instant startDate, Instant endDate, List<? extends AbstractAuditableDocument> transformedEvents) {
        adeDataService.store(schema, startDate, endDate, converterService.convertToAde(transformedEvents, schema));
    }

}