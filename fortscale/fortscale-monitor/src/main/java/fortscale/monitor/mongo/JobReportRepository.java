package fortscale.monitor.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.monitor.JobReport;

public interface JobReportRepository extends MongoRepository<JobReport, String> {
	
}
