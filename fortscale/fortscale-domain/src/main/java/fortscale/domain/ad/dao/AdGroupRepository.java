package fortscale.domain.ad.dao;

import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdGroup;

public interface AdGroupRepository  extends PagingAndSortingRepository<AdGroup,ObjectId>{
	public AdGroup findByDistinguishedName(String distinguishedName);
}
