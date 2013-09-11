package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.fe.AdUserFeaturesExtraction;

public interface AdUsersFeaturesExtractionRepository extends PagingAndSortingRepository<AdUserFeaturesExtraction, String>,
		AdUsersFeaturesExtractionRepositoryCustom {
	public List<AdUserFeaturesExtraction> findByClassifierId(String classifierId, Pageable pageable);
	public List<AdUserFeaturesExtraction> findByUserId(String userId, Pageable pageable);
	public List<AdUserFeaturesExtraction> findByUserIdAndClassifierId(String userId, String classifierId, Pageable pageable);
//	public AdUserFeaturesExtraction findByUserIdAndTimestamp(String userId, Date timestamp);
	public AdUserFeaturesExtraction findByUserIdAndClassifierIdAndTimestamp(String userId, String classifierId, Date timestamp);
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestampAndScoreBetween(String classifierId, Date timestamp, int lowestVal, int upperVal, Pageable pageable);
}
