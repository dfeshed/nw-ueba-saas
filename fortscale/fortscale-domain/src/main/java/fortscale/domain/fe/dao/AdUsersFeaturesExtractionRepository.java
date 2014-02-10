package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.fe.AdUserFeaturesExtraction;

public interface AdUsersFeaturesExtractionRepository extends MongoRepository<AdUserFeaturesExtraction, String>, PagingAndSortingRepository<AdUserFeaturesExtraction, String>,
		AdUsersFeaturesExtractionRepositoryCustom {
	public List<AdUserFeaturesExtraction> findByClassifierId(String classifierId, Pageable pageable);
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestamp(String classifierId, Date timestamp);
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestamp(String classifierId, Date timestamp, Pageable pageable);
	
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestampAndScoreBetween(String classifierId, Date timestamp, int lowestVal, int upperVal, Pageable pageable);
}
