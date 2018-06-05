package presidio.monitoring.elastic.services;

import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import org.springframework.stereotype.Service;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.monitoring.records.MetricDocument;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class PresidioMetricPersistencyServiceImpl implements PresidioMetricPersistencyService {

    private final Logger logger = Logger.getLogger(PresidioMetricPersistencyServiceImpl.class);

    private MetricRepository metricRepository;

    public PresidioMetricPersistencyServiceImpl(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
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

        return metricRepository.findByNameInAndTimestampGreaterThanEqualAndTimestampLessThan(names,timeRange.getStart().toEpochMilli(),timeRange.getEnd().toEpochMilli());
    }

    @Override
    public List<MetricDocument> getMetricsByNames(Collection<String> names) {
        return metricRepository.findByNameIn(names);
    }
}
