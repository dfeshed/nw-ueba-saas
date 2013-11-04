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
	public List<AdUserFeaturesExtraction> findByClassifierIdAndUserId(String classifierId, String userId, Pageable pageable);
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestamp(String classifierId, Date timestamp);
	public AdUserFeaturesExtraction findClassifierIdAndByUserIdAndTimestamp(String classifierId, String userId, Date timestamp);
	public AdUserFeaturesExtraction findClassifierIdAndByUserIdAndTimestampepoch(String classifierId, String userId, Long timestampepoch);
	
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestampAndScoreBetween(String classifierId, Date timestamp, int lowestVal, int upperVal, Pageable pageable);
	
	public List<AdUserFeaturesExtraction> findByUserId(String userId, Pageable pageable);
//	public AdUserFeaturesExtraction findByUserIdAndTimestamp(String userId, Date timestamp);
	public List<AdUserFeaturesExtraction> findByUserId(String userId);
	
	
}
