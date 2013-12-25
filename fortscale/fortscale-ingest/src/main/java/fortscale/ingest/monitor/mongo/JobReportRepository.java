package fortscale.ingest.monitor.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import fortscale.ingest.monitor.JobReport;

public interface JobReportRepository extends MongoRepository<JobReport, String> {
	
}
