package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;

import java.util.List;

public interface AdGroupRepositoryCustom {
	String getLatestRuntime();
	long countByRuntime(String runtime);
	AdGroup findByDistinguishedNameInLastSnapshot(String dn);
	List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);
}
