package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;

import java.util.List;

public interface AdGroupRepositoryCustom {
	Long getLatestTimeStampepoch(String collectionName);
	long countByTimestampepoch(Long timestampepoch, String collectionName);
	AdGroup findByDistinguishedNameInLastSnapshot(String dn);
	List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements);
}
