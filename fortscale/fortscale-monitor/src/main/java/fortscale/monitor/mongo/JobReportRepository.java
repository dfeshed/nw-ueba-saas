package fortscale.monitor.mongo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.monitor.domain.JobReport;

public interface JobReportRepository extends MongoRepository<JobReport, String> {
	
	Page<JobReport> findByStartLessThan(Date start, Pageable pageable);
	
	Page<JobReport> findByStartGreaterThan(Date start, Pageable pageable);
}
