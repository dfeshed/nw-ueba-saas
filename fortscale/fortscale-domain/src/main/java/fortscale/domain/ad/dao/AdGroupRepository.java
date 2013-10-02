package fortscale.domain.ad.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdGroup;

public interface AdGroupRepository  extends PagingAndSortingRepository<AdGroup,String>{
	public AdGroup findByDistinguishedName(String distinguishedName);
}
