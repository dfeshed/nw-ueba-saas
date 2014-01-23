package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdUserThumbnail;

public interface AdUserThumbnailRepository extends PagingAndSortingRepository<AdUserThumbnail, String>{

	public List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable);
}
