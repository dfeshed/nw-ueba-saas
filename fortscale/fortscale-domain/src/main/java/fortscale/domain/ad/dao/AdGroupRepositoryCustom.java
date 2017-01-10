package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;

import java.util.List;

public interface AdGroupRepositoryCustom {
	Long getLatestTimeStampepoch();
	long countByTimestampepoch(Long timestampepoch);
	AdGroup findByDistinguishedNameInLastSnapshot(String dn);
	List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);
}
