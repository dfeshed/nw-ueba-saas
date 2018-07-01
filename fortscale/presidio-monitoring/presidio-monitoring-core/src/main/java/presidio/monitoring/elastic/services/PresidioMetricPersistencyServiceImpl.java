package presidio.monitoring.elastic.services;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import org.springframework.stereotype.Service;
import presidio.monitoring.elastic.allindexrepo.MetricsAllIndexesRepository;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.List;

@Service
public class PresidioMetricPersistencyServiceImpl implements PresidioMetricPersistencyService {

    private final Logger logger = Logger.getLogger(PresidioMetricPersistencyServiceImpl.class);

    private MetricRepository metricRepository;
    private MetricsAllIndexesRepository metricsAllIndexesRepository;

    public PresidioMetricPersistencyServiceImpl(MetricRepository metricRepository, MetricsAllIndexesRepository metricsAllIndexesRepository) {
        this.metricRepository = metricRepository;
        this.metricsAllIndexesRepository = metricsAllIndexesRepository;
    }

    public MetricDocument save(MetricDocument metricDocument) {
        logger.debug("Exporting metric to elastic {}", metricDocument);
        return metricRepository.save(metricDocument);

    }


    public Iterable<MetricDocument> save(List<MetricDocument> metricDocument) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exporting metrics to elastic, number of metrics {}", metricDocument.size());
        }
        return metricRepository.save(metricDocument);
    }

    @Override
    public List<MetricDocument> getMetricsByNamesAndTime(Collection<String> names, TimeRange timeRange){
        //retrieving metrics according to logical (!) time because we want to understand what was the load on the system (and not the amount of processed data)
        return metricsAllIndexesRepository.findByNameInAndLogicTimeGreaterThanEqualAndLogicTimeLessThan(names,timeRange.getStart().toEpochMilli(),timeRange.getEnd().toEpochMilli());
    }

}
