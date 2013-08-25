package fortscale.domain.fe.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.fe.AdUserFeaturesExtraction;

public interface AdUsersFeaturesExtractionRepository extends PagingAndSortingRepository<AdUserFeaturesExtraction, Long>,
		AdUsersFeaturesExtractionRepositoryCustom {

}
