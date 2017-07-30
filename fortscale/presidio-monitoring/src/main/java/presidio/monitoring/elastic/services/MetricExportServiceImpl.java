package presidio.monitoring.elastic.services;

import fortscale.utils.logging.Logger;
import org.springframework.stereotype.Service;
import presidio.monitoring.elastic.records.PresidioMetric;
import presidio.monitoring.elastic.repositories.MetricRepository;


import java.util.List;

@Service
public class MetricExportServiceImpl implements MetricExportService {

    private final Logger logger = Logger.getLogger(MetricExportServiceImpl.class);

    private MetricRepository metricRepository;

    public MetricExportServiceImpl(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public PresidioMetric save(PresidioMetric presidioMetric) {
        logger.info("Exporting metric to elastic {}",presidioMetric.toString());
        return metricRepository.save(presidioMetric);
    }


    public Iterable<PresidioMetric> save(List<PresidioMetric> presidioMetric) {
        logger.info("Exporting metrics to elastic, number of metrics {}",presidioMetric.size());
        for(PresidioMetric presidioMetric1 : presidioMetric){
            save( presidioMetric1);
        }
        return presidioMetric;
        //return metricRepository.save(presidioMetric);
    }
}
