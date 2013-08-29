package fortscale.domain.fe.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.fe.AdUserFeaturesExtraction;

public interface AdUsersFeaturesExtractionRepository extends PagingAndSortingRepository<AdUserFeaturesExtraction, String>,
		AdUsersFeaturesExtractionRepositoryCustom {
	
	public List<AdUserFeaturesExtraction> findByUserId(String userId, Pageable pageable); 
}
