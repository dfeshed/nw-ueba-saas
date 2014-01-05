package fortscale.monitor.mongo;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.monitor.domain.JobReport;

public interface JobReportRepository extends MongoRepository<JobReport, String> {
	
	List<JobReport> findByStartGreaterThan(Date start, Sort sort);
}
