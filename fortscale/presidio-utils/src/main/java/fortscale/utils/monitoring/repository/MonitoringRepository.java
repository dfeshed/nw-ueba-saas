package fortscale.utils.monitoring.repository;


import org.springframework.data.elasticsearch.core.query.IndexQuery;

public interface MonitoringRepository {
    String export(IndexQuery query);
}
