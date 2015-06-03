package fortscale.domain.ad.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.ad.AdGroup;

public interface AdGroupRepository  extends PagingAndSortingRepository<AdGroup,String>, AdGroupRepositoryCustom{
	public List<AdGroup> findByLastModifiedExists(boolean exists);
	public String findByName(String adName);
}
